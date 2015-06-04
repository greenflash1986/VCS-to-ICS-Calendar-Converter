/** 
	Copyright 2011 dragomerlin
	
 	This file is part of VCS to ICS Calendar Converter.
  
    VCS to ICS Calendar Converter is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    VCS to ICS Calendar Converter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VCS to ICS Calendar Converter.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.users.dragomerlin.vcs2icsCalendarConverter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import com.github.greenflash1986.vcs2icsCalendarConverter.ICSWriter;

public class ConvertSingleFile {

	/**
	 * QUOTED-PRINTABLE-UTF8 decoder with CRLF manual replacement: The CRLF must be replaced manually so the resulting stream goes
	 * on a single line.
	 * 
	 * Multi-line QUOTED-PRINTABLE encoded fields must be parsed to be on one single line, since
	 * org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable works that way. The reason is that the
	 * quoted-printable code for any character can be split into two lines making useless the parse of one line at a time.
	 * 
	 * The way to do it is to remove the '=' at the end of each line and append the next until the field is complete. Then send it
	 * to decode. It automatically removes the intermediate '=' and appends the two hex chars after each equal sign. The
	 * characters that are not encoded are appended directly. Only printable ASCII characters can be non-encoded on a field that
	 * is specified to be quoted-printable.
	 * 
	 * A character represented in quoted-printable UTF-8 can take 1, 2 ,3 or 4 groups of an equal sign and two hex chars.
	 * 
	 * For more information: http://www.utf8-chartable.de
	 */

	/**
	 * Not QUOTED-PRINTABLE: Fields like summary, location or description can be multiline even if not quoted-printable encoded.
	 * It is usually done when a line has more than 69 chars length and has at least one space. If not, goes on a single line.
	 * This is detected because the next line has at least 1 space char at the beginning.
	 * 
	 * It is officially supported for external applications that resulting summary, location and description fields go multiline
	 * (specifying it starting with a space char at the beginning of next line), but even so this software converts and keeps
	 * resulting ones on a single line, to simplify things.
	 */

	/*
	 * About UFT: there are 6 main types, 5 of which contain BOM. Such BOM has to be removed at the beginning of the stream since
	 * Java doesn't do that for backward compatibility, and specify the utf8 type when opening the stream. Main types: UTF-8 (no
	 * BOM), UTF-8 with BOM, UTF-16BE, UTF-16LE, UTF-32BE and UTF-32LE.
	 */

	public static String decode(String paramString) {
		// Some special characters like \ will be skipped after importing them
		// with the calendar application. To prevent it we must scape them, like
		// \\
		paramString = paramString.replace("=0D=0A", "\\n"); // Usually it would
															// be "\n" but the
															// \n must go to
															// file instead of
															// inserting a
															// newline
		// The principal operations on a StringBuffer are the append and insert
		// methods, which are overloaded so as to accept data of any type.
		StringBuffer localStringBuffer1 = new StringBuffer();
		try {
			// TODO Add support for more source charsets than the UTF-8. I hope
			// all Nokia phones use UTF-8 by default
			localStringBuffer1.append(new String(org.apache.commons.codec.net.QuotedPrintableCodec
					.decodeQuotedPrintable(paramString.getBytes()), "UTF-8"));
		} catch (DecoderException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		return localStringBuffer1.toString();
	}

	private static BufferedReader detectEncodingAndOpenFile(File inFile) throws IOException {
		String encodingType = null;
		BufferedReader input = null;
		BOMInputStream bomIn = null;

		// Detect file encoding
		encodingType = TestDetector.main(inFile.getAbsolutePath().toString());

		// Entire file reading. FileReader always assumes default encoding is
		// OK!
		// We must check for BOM in UTF files and remove them with
		// org.apache.commons.io.input.BOMInputStream because
		// java doesn't do that automatically. See Oracle bug 4508058.
		if (encodingType == null) {
			// ASCII expected
			input = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
		} else if (encodingType.startsWith("UTF-8")) {
			// UTF-8 requires an exclusive call to BOMInputStream
			bomIn = new BOMInputStream(new FileInputStream(inFile));
			input = new BufferedReader(new InputStreamReader(bomIn, encodingType));
			if (bomIn.hasBOM())
				System.out.println("This file has UTF-8 BOM, removing it");
			else
				System.out.println("This file has UTF-8 without BOM");
		} else if (encodingType.startsWith("UTF-")) {
			// The other UTF cases except UTF-8
			bomIn = new BOMInputStream(new FileInputStream(inFile), ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE,
					ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
			input = new BufferedReader(new InputStreamReader(bomIn, encodingType));
			System.out.println("This file has " + bomIn.getBOMCharsetName() + " BOM, removing it");
		} else {
			// Any other encoding
			input = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), encodingType));
		}
		return input;
	}

	private static String readEncryptedField(String fieldContent, BufferedReader inStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		String tmp = fieldContent;
		while (tmp != null && tmp.endsWith("=")) {
			sb.append(tmp.substring(0, tmp.length() - 1));
			tmp = inStream.readLine();
		}
		if (tmp == null) {
			throw new IOException("Error while reading multiline: EOF.");
		}
		
		sb.append(tmp);
		return decode(sb.toString());
	}

	private static String readPossibleMultiline(String fieldContent, BufferedReader inStream) throws IOException {
		char[] buf = new char[1];
		boolean multilineFound = false;
		do {
			inStream.mark(1);
			int res = inStream.read(buf);
			if (res == -1) {
				throw new IOException("Error while reading multiline: EOF.");
			}
			
			if (buf[0] == ' ') {
				multilineFound = true;
				String line = inStream.readLine();
				fieldContent += line;
			} else {
				multilineFound = false;
			}
		} while (multilineFound);
		inStream.reset();
		return fieldContent;
	}

	public static void convert(File inFile, String email, ICSWriter icsWriter) throws IOException, ParseException {
		/**
		 * Fetch the entire contents of a text file, and return it in a StringBuffer. This style of implementation does not throw
		 * Exceptions to the caller.
		 * 
		 * @param inFile
		 *            is a file which already exists and can be read.
		 */

		BufferedReader input = detectEncodingAndOpenFile(inFile);
		

		// use buffering, reading one line at a time
		/*
		 * readLine is a bit quirky : it returns the content of a line MINUS the newline. it returns null only for the END of the
		 * stream. it returns an empty String if two newlines appear in a row.
		 */
		String line = null; // not declared within while loop
		boolean flag_continue = true; // Detection of END:VCALENDAR

		try {
			while (flag_continue && (line = input.readLine()) != null) { // First check flag to prevent reading one more line if
																			// not needed

				// .toUpperCase() is used because some software, like
				// Microsoft Works, may not export all
				// field descriptions uppercase.

				// Jump from one event or todo to the next
				if (line.toUpperCase().equals("END:VCALENDAR")) {
					flag_continue = false;
				} else if (!line.toUpperCase().equals("BEGIN:VTODO") && !line.toUpperCase().equals("BEGIN:VEVENT")) {
					// Data that may apply to all events and todos
					if (line.toUpperCase().equals("BEGIN:VCALENDAR"))
						;
					else if (line.toUpperCase().startsWith("PRODID:"))
						;
					else if (line.toUpperCase().startsWith("VERSION:"))
						;
					else
						System.out.println("* New unknown header entry detected: " + line);
				} else {
					// Only one event or todo at a time, fetch the contents on
					// any of either here and get all the fields
					boolean event_continue = true; // Detection of END:VEVENT or
													// END:VTODO
					boolean isevent = true; // true if event, false if todo

					String summary = null;
					String location = null;
					String description = null;
					String status = null;
					String due = null;
					String sequence = null;
					String dtstart = null;
					String dtend = null;
					String dtstamp = null;
					String rrule = null;
					String alarm = null;

					if (line.toUpperCase().startsWith("BEGIN:VEVENT")) // Must check it before a new line is read overwritting
																		// existent
						isevent = true;
					else
						isevent = false;

					// First check flag to prevent reading one more line that
					// will be skipped if flag is false
					while (event_continue && (line = input.readLine()) != null) {
						/*
						 * If summary, location or description are not quoted-printable but still multiline. Lines are split if
						 * they are more than 69 (NOKIA) chars lenght, unless the field has no space chars.
						 */
						if (line.toUpperCase().startsWith("SUMMARY:")) { // In case not quoted-printable
							summary = readPossibleMultiline(line.substring("SUMMARY:".length()), input);
						} else if (line.toUpperCase().startsWith("SUMMARY;ENCODING=QUOTED-PRINTABLE")) {
							// Any encoding may be specified following, but UTF8 is presumed since is the standard.
							summary = readEncryptedField(line.substring(line.indexOf(":") + 1), input);
						} else if (line.toUpperCase().startsWith("LOCATION:")) {
							location = readPossibleMultiline(line.substring("LOCATION:".length()), input);
						} else if (line.toUpperCase().startsWith("LOCATION;ENCODING=QUOTED-PRINTABLE")) {
							// Any encoding may be specified following, but UTF8 is presumed since is the standard.
							location = readEncryptedField(line.substring(line.indexOf(":") + 1), input);
						} else if (line.toUpperCase().startsWith("DESCRIPTION:")) {
							description = readPossibleMultiline(line.substring("DESCRIPTION:".length()), input);
						} else if (line.toUpperCase().startsWith("DESCRIPTION;ENCODING=QUOTED-PRINTABLE")) {
							// Any encoding may be specified following, but UTF8 is presumed since is the standard.
							description = readEncryptedField(line.substring(line.indexOf(":") + 1), input);
						} else if (line.toUpperCase().startsWith("VERSION:"))
							;
						else if (line.toUpperCase().startsWith("ORGANIZER:"))
							;
						else if (line.toUpperCase().startsWith("DTSTAMP:"))
							// TODO this should be used as creation date
							;
						else if (line.toUpperCase().startsWith("UID:"))
							;
						else if (line.toUpperCase().startsWith("DTSTART:")) {
							dtstart = line.substring("DTSTART:".length());
						} else if (line.toUpperCase().startsWith("DTEND:")) {
							dtend = line.substring("DTEND:".length());
						} else if (line.toUpperCase().startsWith("DUE:")) {
							due = line.substring("DUE:".length());
						} else if (line.toUpperCase().startsWith("X-EPOCAGENDAENTRYTYPE:"))
							;
						else if (line.toUpperCase().startsWith("X-EPOCTODOLIST:"))
							;
						else if (line.toUpperCase().startsWith("STATUS:")) {
							// TODO: Probably may need uppercase or check if
							// status is among valid values
							status = line.substring("STATUS:".length());
						} else if (line.toUpperCase().startsWith("X-BLUETOOTH-ALLDAYEVENT:"))
							;
						else if (line.toUpperCase().startsWith("CLASS:"))
							;
						else if (line.toUpperCase().startsWith("SEQUENCE:")) {
							sequence = line.substring("SEQUENCE:".length());
						} else if (line.toUpperCase().startsWith("X-METHOD:"))
							;
						else if (line.toUpperCase().startsWith("RRULE:")) {
							rrule = line.substring("RRULE:".length());
						} else if (line.toUpperCase().startsWith("DALARM:"))
							;
						else if (line.toUpperCase().startsWith("AALARM:")
								|| line.toUpperCase().startsWith("AALARM;TYPE=X-EPOCSOUND:")) {
							// this is enough for Nokia 5500 Sport
							alarm = line.substring(line.indexOf(":") + 1, line.indexOf(";", line.indexOf(":") + 1));
						} else if (line.toUpperCase().startsWith("LAST-MODIFIED:")) {
							// TODO don't use this as creation date
							dtstamp = line.substring("LAST-MODIFIED:".length());
						} else if (line.toUpperCase().startsWith("PRIORITY:"))
							;
						else if (line.toUpperCase().startsWith("X-SYMBIAN-LUID:"))
							;
						else if (line.toUpperCase().startsWith("COMPLETED:"))
							;
						else if (line.toUpperCase().startsWith("TZ:"))
							;
						else if (line.toUpperCase().startsWith("DAYLIGHT:"))
							;
						else if (line.toUpperCase().startsWith("PRODID:"))
							;
						else if (line.toUpperCase().startsWith("CATEGORIES:"))
							;
						else if (line.toUpperCase().startsWith("CATEGORIES;ENCODING=QUOTED-PRINTABLE:"))
							;
						else if (line.toUpperCase().startsWith("END:VEVENT")) {
							// Stop reading this file once first calendar of
							// file has been read.
							event_continue = false;
						} else if (line.toUpperCase().startsWith("END:VTODO")) {
							// Stop reading this file once first calendar of
							// file has been read.
							event_continue = false;
						} else {
							System.out.println("* New unknown entry detected: " + line);
						}
					}

					icsWriter.addEvent(isevent, summary, description, location, dtstart, dtend, rrule, dtstamp, sequence, due,
							status, alarm);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
