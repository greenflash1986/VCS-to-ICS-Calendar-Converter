package greenflash1986;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class ICSWriter {
	// TODO maybe use ical4j https://github.com/ical4j/ical4j/wiki
	private static final String NEWLINE = System.getProperty("line.separator");
	private String email;
	private StringBuilder contents = new StringBuilder();

	public ICSWriter(String email) {
		this.email = email;
		// Write the first 3 lines of the output file only once
		contents.append("BEGIN:VCALENDAR" + NEWLINE);
		if (email != null)
			contents.append("PRODID:" + email + NEWLINE);
		else
			contents.append("PRODID:" + NEWLINE);
		contents.append("VERSION:2.0" + NEWLINE);
	}

	/**
	 * 
	 * @param isEvent
	 *            if it's an event or a todo
	 * @param summary
	 * @param description
	 * @param location
	 * @param dtStart
	 *            the begin of the event, see <a href=https://tools.ietf.org/html/rfc5545#section-3.6.1>RFC 5545 section 3.6.1</a>
	 * @param dtEnd
	 *            the end of the event
	 * @param dtStamp
	 *            the creation date of the iCal file, see <a
	 *            href=http://stackoverflow.com/questions/11594921/whats-the-difference-between-
	 *            created-and-dtstamp-in-the-icalendar-format>Stackoverflow</a> for clarification
	 * @param sequence
	 * @param due
	 * @param status
	 * @throws ParseException
	 * 
	 * @see <a href=https://tools.ietf.org/html/rfc5545#section-3.3.5>RFC 5545 section 3.3.5</a> for format of date and time
	 */
	public void addEvent(boolean isEvent, String summary, String description, String location, String dtStart, String dtEnd,
			String rrule, String dtStamp, String sequence, String due, String status, String alarm) throws ParseException {
		if (isEvent) {
			contents.append("BEGIN:VEVENT" + NEWLINE);
			if (email != null) {
				contents.append("ORGANIZER:" + email + NEWLINE);
			}

			if (summary != null) {
				contents.append("SUMMARY:" + summary + NEWLINE);
			}

			if (description != null) {
				contents.append("DESCRIPTION:" + description + NEWLINE);
			}

			if (location != null) {
				contents.append("LOCATION:" + location + NEWLINE);
			}

			if (rrule != null) {
				RepeatRule repeatRule = RepeatRule.parse(rrule, false); // TODO make it configurable to parse use the endDate
				if (repeatRule != null) {
					contents.append(repeatRule.toICS());
				}
			}
			
			if (dtStart != null) {
				if (checkForAllDayEvent(dtStart, dtEnd)) {
					ZonedDateTime start = CalendarDate.parse(dtStart);
					contents.append("DTSTART;VALUE=DATE:" + CalendarDate.formatTimeForDayEvent(start) + NEWLINE);
				} else {
					contents.append("DTSTART:" + dtStart + NEWLINE);
					if (dtEnd != null) {
						contents.append("DTEND:" + dtEnd + NEWLINE);
					}
				}
			} else {
				throw new IllegalArgumentException("No Start date specified"); // TODO according to RFC its possible to specifiy
																				// the METHOD property, but at this time, this is
																				// not implemented
			}

			if (dtStamp != null) {
				contents.append("DTSTAMP:" + dtStamp + NEWLINE);
			} else {
				// Get UTC (GMT) time of the current computer in
				// case read file doesn't have DTSTAMP
				contents.append("DTSTAMP:" + generateCreationDate() + NEWLINE);
			}
			contents.append("END:VEVENT" + NEWLINE);
		} else {
			contents.append("BEGIN:VTODO" + NEWLINE);
			if (dtStamp != null) {
				contents.append("DTSTAMP:" + dtStamp + NEWLINE);
			} else {
				// Get UTC (GMT) time of the current computer in
				// case read file doesn't have DTSTAMP
				contents.append("DTSTAMP:" + generateCreationDate() + NEWLINE);
			}
			if (sequence != null) {
				contents.append("SEQUENCE:" + sequence + NEWLINE);
			} else {
				contents.append("SEQUENCE:0" + NEWLINE);
			}
			if (email != null) {
				contents.append("ORGANIZER:" + email + NEWLINE);
			}

			if (due != null) {
				contents.append("DUE:" + due + NEWLINE);
			}

			if (status != null) {
				contents.append("STATUS:" + status + NEWLINE);
			}

			if (summary != null) {
				contents.append("SUMMARY:" + summary + NEWLINE);
			}

			contents.append("END:VTODO" + NEWLINE);
		}
	}

	/**
	 * generates a nice formatted time string for <code>NOW</code> in UTC for iCalendar with trailing Z
	 * 
	 * @return a String for NOW in UTC
	 */
	private static String generateCreationDate() {
		String formatted = CalendarDate.format(ZonedDateTime.now());
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

	private static boolean checkForAllDayEvent(String dtstart, String dtend) {
		// for Nokia 5500 Sport, this is enough, maybe make it more generic in the future
		if (dtstart != null) {
			if (dtstart.equals(dtend)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
