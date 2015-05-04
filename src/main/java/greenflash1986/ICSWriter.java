package greenflash1986;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ICSWriter {
	// TODO maybe use ical4j https://github.com/ical4j/ical4j/wiki
	private String email;
	private StringBuilder contents = new StringBuilder();

	public ICSWriter(String email) {
		this.email = email;
		// Write the first 3 lines of the output file only once
		contents.append("BEGIN:VCALENDAR" + System.getProperty("line.separator"));
		if (email != null)
			contents.append("PRODID:" + email + System.getProperty("line.separator"));
		else
			contents.append("PRODID:" + System.getProperty("line.separator"));
		contents.append("VERSION:2.0" + System.getProperty("line.separator"));
	}

	public void addEvent(boolean isEvent, String summary, String description, String location, String dtStart, String dtEnd,
			String dtStamp, String sequence, String due, String status) {
		if (isEvent) {
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
			if (dtStart != null) {
				contents.append("DTSTART:" + dtStart + System.getProperty("line.separator"));
			} else {
				contents.append("DTSTART:" + System.getProperty("line.separator"));
			}
			if (dtEnd != null) {
				contents.append("DTEND:" + dtEnd + System.getProperty("line.separator"));
			} else {
				contents.append("DTEND:" + System.getProperty("line.separator"));
			}
			if (dtStamp != null) {
				contents.append("DTSTAMP:" + dtStamp + System.getProperty("line.separator"));
			} else {
				// Get UTC (GMT) time of the current computer in
				// case read file doesn't have DTSTAMP
				contents.append("DTSTAMP:" + generateCreationDate() + System.getProperty("line.separator"));
			}
			contents.append("END:VEVENT" + System.getProperty("line.separator"));
		} else {
			contents.append("BEGIN:VTODO" + System.getProperty("line.separator"));
			if (dtStamp != null) {
				contents.append("DTSTAMP:" + dtStamp + System.getProperty("line.separator"));
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
	}

	/**
	 * generates a nice formatted time string for <code>NOW</code> in UTC for iCalendar with trailing Z
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

	public String write(File outFile) {

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
			 * outFile MUST be UTF-8 encoded, because in the quoted-printable encoding there can be characters that when decoded
			 * won't fit US-ASCII or ANSI character sets. So, in the quoted-printable section can't be characters that are non
			 * ASCII printable, like Euro symbol, japanese kanji or greek letter.
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
		return contents.toString();
	}

}
