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

import java.io.*;
import java.text.ParseException;

public class Main {

	public static void main(String[] args) throws IOException {
		String email = null;

		System.out.println("VCS to ICS calendar converter v2.0");
		System.out.println("Working directory: " + System.getProperty("user.dir"));

		// Check whether Java has some bug causing bad quoted printable decoding
		// for non encoded characters
		String original_seq = "Steuererklär";
		String decoded_seq = dragomerlin.ConvertSingleFile.decode(original_seq);
		if (original_seq.contentEquals(decoded_seq))
			System.out.println("org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable\n"
					+ " seems working ok on your system!\n");
		else
			System.out.println("\nWARNING:\n" + " org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable\n"
					+ " is not working properly on your system! Probably this is caused by a bug in Java.\n"
					+ " Try using a diferent operating system or your resulting files may contain errors.\n");

		File workingdir = new File(System.getProperty("user.dir"));
		File dir_vcs = new File(System.getProperty("user.dir") + File.separator + "vcs" + File.separator);
		File dir_ics = new File(System.getProperty("user.dir") + File.separator + "ics" + File.separator);

		// Check if there are arguments for the jar file, and if there are, if
		// the first one
		// equals "--email" or "-e" and the second is somewhat valid, use it as
		// email.
		// If "--email" or "-e" was given but the following argument is missing
		// just don't ask any more.
		// Spaces are trimmed by the input automatically.

		// NOTE: Is a bad idea start reading the first argument first.
		// Instead start reading first the last argument to catch excepctions
		// and go down one by one.

		if (args.length > 0) {
			if ("-e".equals(args[0]) || "--email".equals(args[0])) {
				if (args.length > 1) {
					email = args[1];
				}
			} else {
				System.out.println("Invalid parameter: " + args[0]);
				// FIXME print usage
				email = readEmail();
			}
		} else {
			email = readEmail();
		}

		
		// TODO: separate this into other method, in case we want to create a GUI later
		
		// Check if VCS directory exists and is readable, create ICS dir if
		// needed and check that is writable.
		if (!dir_vcs.exists())
			System.out.println("The vcs directory doesn't exist. Create it and put into it the calendar files");
		if (!dir_vcs.canRead())
			System.out.println("The vcs directory is not readable");
		if (!workingdir.canWrite())
			System.out.println("The working dir is write protected. You can't write in this folder");
		if (!dir_ics.exists() && workingdir.canWrite())
			dir_ics.mkdir();
		if (!dir_ics.exists() || !dir_ics.canWrite())
			System.out.print("The ics dir does not exist or is not writable");
		if (dir_vcs.exists() && dir_vcs.canRead() && dir_ics.exists() && dir_ics.canWrite()) {
			File[] list = dir_vcs.listFiles();
			int vcs_counter = 0;
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory() && !list[i].isFile()) {
					// Check that is directory
					System.out.println("\"" + list[i].getName() + "\"" + " not valid, is a directory"
							+ System.getProperty("line.separator"));
				} else if (!list[i].getName().toLowerCase().endsWith(".vcs"))
					System.out.println("\"" + list[i].getName() + "\"" + " not valid file, is not VCS"
							+ System.getProperty("line.separator"));
				else {
					vcs_counter++;
					System.out.println("Found file: " + list[i].getAbsolutePath());
					// Start conversion here
					int numchars = list[i].getName().length();
					numchars = numchars - 4; // Remove .vcs from filenames
					File outFile = new File(dir_ics.toString() + File.separator
							+ list[i].getName().toString().substring(0, numchars) + ".ics");
					try {
					ConvertSingleFile.getnumber(list[i], email, outFile);
					} catch (ParseException pe) {
						System.out.println("Could not parse file " + list[i] + "Message was " + pe.getMessage());
					}
					// fileconverter.filetoUTF8(outFile);
				}
			}
			System.out.println("Found " + vcs_counter + " valid files");
			// System.out.println(java.nio.charset.Charset.defaultCharset().name());
		}
	}

	public static String readEmail() {
		// FIXME: this don't have to be the email, it's the name of the creator
		// of the event
		System.out.println("Enter here the email where you will store the calendar: ");
		String s = null;

		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			s = bufferRead.readLine();
			// Always close streams
			bufferRead.close();
			System.out.println("Your saved email is: " + s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
}
