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

package dragomerlin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;

import javax.annotation.Generated;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

public class convertsinglefile {

	/**
	 * QUOTED-PRINTABLE-UTF8 decoder with CRLF manual replacement: The CRLF must
	 * be replaced manually so the resulting stream goes on a single line.
	 * 
	 * Multi-line QUOTED-PRINTABLE encoded fields must be parsed to be on one
	 * single line, since
	 * org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable
	 * works that way. The reason is that the quoted-printable code for any
	 * character can be split into two lines making useless the parse of one
	 * line at a time.
	 * 
	 * The way to do it is to remove the '=' at the end of each line and append
	 * the next until the field is complete. Then send it to decode. It
	 * automatically removes the intermediate '=' and appends the two hex chars
	 * after each equal sign. The characters that are not encoded are appended
	 * directly. Only printable ASCII characters can be non-encoded on a field
	 * that is specified to be quoted-printable.
	 * 
	 * A character represented in quoted-printable UTF-8 can take 1, 2 ,3 or 4
	 * groups of an equal sign and two hex chars.
	 * 
	 * For more information: http://www.utf8-chartable.de
	 */

	/**
	 * Not QUOTED-PRINTABLE: Fields like summary, location or description can be
	 * multiline even if not quoted-printable encoded. It is usually done when a
	 * line has more than 69 chars length and has at least one space. If not,
	 * goes on a single line. This is detected because the next line has at
	 * least 1 space char at the beginning.
	 * 
	 * It is officially supported for external applications that resulting
	 * summary, location and description fields go multiline (specifying it
	 * starting with a space char at the beginning of next line), but even so
	 * this software converts and keeps resulting ones on a single line, to
	 * simplify things.
	 */

	/*
	 * About UFT: there are 6 main types, 5 of which contain BOM. Such BOM has
	 * to be removed at the beginning of the stream since Java doesn't do that
	 * for backward compatibility, and specify the utf8 type when opening the
	 * stream. Main types: UTF-8 (no BOM), UTF-8 with BOM, UTF-16BE, UTF-16LE,
	 * UTF-32BE and UTF-32LE.
	 */

	public static String decode(String paramString) throws UnsupportedEncodingException {
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
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return localStringBuffer1.toString();
	}

	public static void getnumber(File inFile, String email, File outFile) {
		// TODO: separate the import and export
		// TODO: convert AALARM
		// TODO: import more than one file into calendar
		// TODO: give the user the choice, if export to single or multifile
		// TODO: write tests
		/**
		 * Fetch the entire contents of a text file, and return it in a
		 * StringBuffer. This style of implementation does not throw Exceptions
		 * to the caller.
		 * 
		 * @param inFile
		 *            is a file which already exists and can be read.
		 */

		String encodingType = null;
		BufferedReader input = null;
		BOMInputStream bomIn = null;
		StringBuilder contents = new StringBuilder(); // Contents of the
														// generated ics file

		// Detect file encoding
		try {
			encodingType = TestDetector.main(inFile.getAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Entire file reading. FileReader always assumes default encoding is
		// OK!
		// We must check for BOM in UTF files and remove them with
		// org.apache.commons.io.input.BOMInputStream because
		// java doesn't do that automatically. See Oracle bug 4508058.
		try {
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// use buffering, reading one line at a time
		/*
		 * readLine is a bit quirky : it returns the content of a line MINUS the
		 * newline. it returns null only for the END of the stream. it returns
		 * an empty String if two newlines appear in a row.
		 */
		String line = null; // not declared within while loop
		boolean flag_continue = true; // Detection of END:VCALENDAR

		// Write the first 3 lines of the output file only once
		contents.append("BEGIN:VCALENDAR" + System.getProperty("line.separator"));
		if (email != null)
			contents.append("PRODID:" + email + System.getProperty("line.separator"));
		else
			contents.append("PRODID:" + System.getProperty("line.separator"));
		contents.append("VERSION:2.0" + System.getProperty("line.separator"));

		try {
			while (flag_continue && (line = input.readLine()) != null) { // First
																			// check
																			// flag
																			// to
																			// prevent
																			// reading
																			// one
																			// more
																			// line
																			// if
																			// not
																			// needed
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
					boolean one_more_encLine = false; // Check if
														// QUOTED-PRINTABLE is
														// multiline
					boolean summaryIsEnc = false; // To convert summaryEnc or
													// summaryNoEnc
					boolean locationIsEnc = false; // To convert locationEnc or
													// locationNoEnc
					boolean descriptionIsEnc = false; // To convert
														// descriptionEnc or
														// descriptionNoEnc
					boolean isevent = true; // true if event, false if todo
					int field = 0; // 0=summaryEnc, 1=locationEnc,
									// 2=descriptionEnc, 3=summary, 4=location,
									// 5=description
					String summaryNoEnc = null;
					String locationNoEnc = null;
					String descriptionNoEnc = null;
					String summaryEnc = null;
					String locationEnc = null;
					String descriptionEnc = null;
					String summary = null;
					String location = null;
					String description = null;
					String status = null;
					String due = null;
					String sequence = null;
					String dtstart = null;
					String dtend = null;
					String dtstamp = null;

					if (line.toUpperCase().startsWith("BEGIN:VEVENT")) // Must
																		// check
																		// it
																		// before
																		// a new
																		// line
																		// is
																		// read
																		// overwritting
																		// existent
						isevent = true;
					else
						isevent = false;

					// First check flag to prevent reading one more line that
					// will be skipped is flag is false
					while (event_continue && (line = input.readLine()) != null) {
						// *//

						// Do the line by line read job.
						if (field > 2) {
							/**
							 * If summary, location or description are not
							 * quoted-printable but still multiline. Lines are
							 * split if they are more than 69 (NOKIA) chars
							 * lenght, unless the field has no space chars.
							 */
							switch (field) {
							case 3: // summary multiline
								if (line.startsWith(" ")) {
									summaryNoEnc = summaryNoEnc + line.substring(1);
								} else {
									field = 0;
								}
								break;
							case 4: // location multiline
								if (line.startsWith(" ")) {
									locationNoEnc = locationNoEnc + line.substring(1);
								} else {
									field = 0;
								}
								break;
							case 5: // description multiline
								if (line.startsWith(" ")) {
									descriptionNoEnc = descriptionNoEnc + line.substring(1);
								} else {
									field = 0;
								}
								break;
							}
						}
						if (one_more_encLine) {
							if (line.endsWith("=")) {
								one_more_encLine = true;
								line = line.substring(0, line.length() - 1);
							} else {
								one_more_encLine = false;
							}

							switch (field) {
							case 0: // summaryEnc case
								summaryEnc = summaryEnc + line;
								break;
							case 1: // locationEnc case
								locationEnc = locationEnc + line;
								break;
							case 2: // descriptionEnc case
								descriptionEnc = descriptionEnc + line;
								break;
							}
						}
						// .toUpperCase() is used because some software, like
						// Microsoft Works, may not export all
						// field descriptions uppercase.
						else if (field <= 2) {
							if (line.toUpperCase().startsWith("SUMMARY:")) { // In
																				// case
																				// not
																				// quoted-printable
								summaryNoEnc = line.substring("SUMMARY:".length());
								field = 3;
								summaryIsEnc = false;
							} // Any encoding may be specified following, but
								// UTF8 is presumed since is the standard.
							else if (line.toUpperCase().startsWith("SUMMARY;ENCODING=QUOTED-PRINTABLE")) {
								field = 0;
								summaryIsEnc = true;
								summaryEnc = line.substring(line.indexOf(":") + 1);
								if (summaryEnc != null) {
									if (line.endsWith("=")) {
										one_more_encLine = true;
										summaryEnc = summaryEnc.substring(0, summaryEnc.length() - 1);
									}
								}
							} else if (line.toUpperCase().startsWith("LOCATION:")) { // In
																						// case
																						// not
																						// quoted-printable
								locationNoEnc = line.substring("LOCATION:".length());
								field = 4;
								locationIsEnc = false;
							} else if (line.toUpperCase().startsWith("LOCATION;ENCODING=QUOTED-PRINTABLE")) {
								field = 1;
								locationIsEnc = true;
								locationEnc = line.substring(line.indexOf(":") + 1);
								if (locationEnc != null) {
									if (line.endsWith("=")) {
										one_more_encLine = true;
										locationEnc = locationEnc.substring(0, locationEnc.length() - 1);
									}
								}
							} else if (line.toUpperCase().startsWith("DESCRIPTION:")) { // In
																						// case
																						// not
																						// quoted-printable
								descriptionNoEnc = line.substring("DESCRIPTION:".length());
								field = 5;
								descriptionIsEnc = false;
							} else if (line.toUpperCase().startsWith("DESCRIPTION;ENCODING=QUOTED-PRINTABLE")) {
								field = 2;
								descriptionIsEnc = true;
								descriptionEnc = line.substring(line.indexOf(":") + 1);
								if (descriptionEnc != null) {
									if (line.endsWith("=")) {
										one_more_encLine = true;
										descriptionEnc = descriptionEnc.substring(0, descriptionEnc.length() - 1);
									}
								}
							} else if (line.toUpperCase().startsWith("VERSION:"))
								;
							else if (line.toUpperCase().startsWith("ORGANIZER:"))
								;
							else if (line.toUpperCase().startsWith("DTSTAMP:"))
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
							else if (line.toUpperCase().startsWith("RRULE:")) { // TODO
																				// check
																				// the
																				// uppercase
																				// stuff
																				// here
																				// *Unfinished*
								String myString;
								String frec = null;
								String frec2 = null;
								myString = line.substring("RRULE:".length());
								if (myString.startsWith("D")) {
									if (myString.contains(" ")) {
										frec = myString.substring(myString.indexOf("D") + 1, myString.indexOf(" "));
										myString = myString.substring(myString.indexOf(" ") + 1);
										System.out.println(myString);
										if (myString.startsWith("#")) {
											frec2 = myString.substring(myString.indexOf("#") + 1);
										}
									} else {
										frec = myString.substring(myString.indexOf("D") + 1);
										// End of field
									}
									// System.out.println("frec is ."+frec+".");
									// System.out.println("number of events is ."+frec2+".");
								} else if (myString.startsWith("W"))
									;
								else if (myString.startsWith("MD"))
									;
								else if (myString.startsWith("YM"))
									;
							} else if (line.toUpperCase().startsWith("DALARM:"))
								;
							else if (line.toUpperCase().startsWith("AALARM:"))
								;
							else if (line.toUpperCase().startsWith("AALARM;TYPE=X-EPOCSOUND:"))
								;
							else if (line.toUpperCase().startsWith("LAST-MODIFIED:")) {
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
						// *//
					}

					// Begin single event or todo generation
					// Begin summary, location and description decoding
					try {
						if (summaryIsEnc) {
							if (summaryEnc != null) { // To prevent
														// java.lang.NullPointerException
								summary = dragomerlin.convertsinglefile.decode(summaryEnc);
							}
						} else {
							if (summaryNoEnc != null)
								summary = summaryNoEnc; // No need to decode
														// here
						}

						if (locationIsEnc) {
							if (locationEnc != null) {
								location = dragomerlin.convertsinglefile.decode(locationEnc);
							}
						} else {
							if (locationNoEnc != null)
								location = locationNoEnc; // No need to decode
															// here
						}

						if (descriptionIsEnc) {
							if (descriptionEnc != null) {
								description = dragomerlin.convertsinglefile.decode(descriptionEnc);
							}
						} else {
							if (descriptionNoEnc != null)
								description = descriptionNoEnc; // No need to
																// decode here
						}
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					// End summary, location and description decoding
					// Begin rest of event
					if (isevent) {
						contents.append("BEGIN:VEVENT" + System.getProperty("line.separator"));
						if (email != null) {
							contents.append("ORGANIZER:" + email + System.getProperty("line.separator"));
						} else {
							contents.append("ORGANIZER:" + System.getProperty("line.separator"));
						}
						if (summary != null) {
							contents.append("SUMMARY:" + summary + System.getProperty("line.separator"));
						} else {
							contents.append("SUMMARY:" + System.getProperty("line.separator"));
						}
						if (description != null) {
							contents.append("DESCRIPTION:" + description + System.getProperty("line.separator"));
						} else {
							contents.append("DESCRIPTION:" + System.getProperty("line.separator"));
						}
						if (location != null) {
							contents.append("LOCATION:" + location + System.getProperty("line.separator"));
						} else {
							contents.append("LOCATION:" + System.getProperty("line.separator"));
						}
						// RRULE
						if (dtstart != null) {
							contents.append("DTSTART:" + dtstart + System.getProperty("line.separator"));
						} else {
							contents.append("DTSTART:" + System.getProperty("line.separator"));
						}
						if (dtend != null) {
							contents.append("DTEND:" + dtend + System.getProperty("line.separator"));
						} else {
							contents.append("DTEND:" + System.getProperty("line.separator"));
						}
						if (dtstamp != null) {
							contents.append("DTSTAMP:" + dtstamp + System.getProperty("line.separator"));
						} else {
							// Get UTC (GMT) time of the current computer in
							// case read file doesn't have DTSTAMP
							contents.append("DTSTAMP:" + generateCreationDate() + System.getProperty("line.separator"));
						}
						contents.append("END:VEVENT" + System.getProperty("line.separator"));
					} else {
						contents.append("BEGIN:VTODO" + System.getProperty("line.separator"));
						if (dtstamp != null) {
							contents.append("DTSTAMP:" + dtstamp + System.getProperty("line.separator"));
						} else {
							// Get UTC (GMT) time of the current computer in
							// case read file doesn't have DTSTAMP

							contents.append("DTSTAMP:" + generateCreationDate() + System.getProperty("line.separator"));
							contents.append("END:VEVENT" + System.getProperty("line.separator"));
							contents.append("END:VCALENDAR" + System.getProperty("line.separator"));
						}
						if (sequence != null) {
							contents.append("SEQUENCE:" + sequence + System.getProperty("line.separator"));
						} else {
							contents.append("SEQUENCE:0" + System.getProperty("line.separator"));
						}
						if (email != null) {
							contents.append("ORGANIZER:" + email + System.getProperty("line.separator"));
						} else {
							contents.append("ORGANIZER:" + System.getProperty("line.separator"));
						}
						if (due != null) {
							contents.append("DUE:" + due + System.getProperty("line.separator"));
						} else {
							contents.append("DUE:" + System.getProperty("line.separator"));
						}
						if (status != null) {
							contents.append("STATUS:" + status + System.getProperty("line.separator"));
						} else {
							contents.append("STATUS:NEEDS-ACTION" + System.getProperty("line.separator"));
						}
						if (summary != null) {
							contents.append("SUMMARY:" + summary + System.getProperty("line.separator"));
						} else {
							contents.append("SUMMARY:" + System.getProperty("line.separator"));
						}
						contents.append("END:VTODO" + System.getProperty("line.separator"));
					}
					// End rest of event
					// End single event or todo generation
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

		// Write the last line of the file, only once
		contents.append("END:VCALENDAR"); // No line.separator in the end (no
											// blank line)

		// Begin file writting
		try {
			if (outFile.exists()) {
				outFile.delete();
				outFile.createNewFile();
			}
			/**
			 * outFile MUST be UTF-8 encoded, because in the quoted-printable
			 * encoding there can be characters that when decoded won't fit
			 * US-ASCII or ANSI character sets. So, in the quoted-printable
			 * section can't be characters that are non ASCII printable, like
			 * Euro symbol, japanese kanji or greek letter.
			 */
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			try {
				// org.apache.commons.io.FileUtils.write(outFile, contents,
				// "UTF-8");
				output.write(contents.toString());
			} finally {
				// Always close streams
				output.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// End file writting

		// Local stream closing. bomIn stream only exists when file encoding is
		// UTF.
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try { // Check first if encodingType is null to prevent
				// java.lang.NullPointerException
			if (encodingType != null && encodingType.startsWith("UTF-"))
				bomIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// End local stream closing

		// Write to console
		System.out.println("Out file is: " + outFile.getAbsolutePath().toString());
		System.out.println("The ICS content is:");
		System.out.println(contents.toString());
		System.out.println();
	}

	/**
	 * generates a nice formatted time string for <code>NOW</code> in UTC for
	 * iCalendar with trailing Z
	 * 
	 * @return a String for NOW in UTC
	 */
	private static String generateCreationDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd'T'HHmmss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String formatted = sdf.format(cal.getTime());
		return formatted;
	}
}
