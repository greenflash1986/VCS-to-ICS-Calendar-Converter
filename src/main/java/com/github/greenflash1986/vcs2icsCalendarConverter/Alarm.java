package com.github.greenflash1986.vcs2icsCalendarConverter;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static com.github.greenflash1986.vcs2icsCalendarConverter.ICSWriter.NEWLINE;

public class Alarm {
	
	private long difference;

	public Alarm(ZonedDateTime start, String alarm) {
		ZonedDateTime alarmTime = CalendarDate.parse(alarm);
		if (start != null) {
			// TODO what happens, if the two dates are in different timezones (UTC / local)
			// acc. to spec the duration should not be affected by daylight saving time
			difference = ChronoUnit.SECONDS.between(start.toLocalDateTime(), alarmTime.toLocalDateTime());
		}
	}
	
	public String toICS(String description) {
		return "BEGIN:VALARM" + NEWLINE
				+ "ACTION:DISPLAY" + NEWLINE
				+ "DESCRIPTION:" + description + NEWLINE
				+ "TRIGGER:" + parseDuration() + NEWLINE
				+ "END:VALARM";
	}
	
	private String parseDuration() {
		// month and year are not supported according to spec (http://tools.ietf.org/html/rfc5545#section-3.3.6)
		int minLength = 60;
		int hourLength = minLength * 60;
		int dayLength = hourLength * 24;
		int weekLenght = dayLength * 7;
		
		StringBuilder sb = difference > 0 ? new StringBuilder("P") : new StringBuilder("-P");

		long seconds = difference > 0 ? difference : difference * -1;
		
		if (seconds % (weekLenght) == 0) {
			sb.append(Integer.toString((int) (seconds / weekLenght)) + "W");
		} else {
			int days = (int) (seconds / dayLength);
			seconds = seconds % dayLength;
			int hours = (int) (seconds / hourLength);
			seconds = seconds % hourLength;
			int minutes = (int) (seconds / minLength);
			seconds = seconds % minLength;
			
			if (days > 0) {
				sb.append(Integer.toString(days) + "D");
			}
			if (hours > 0 || minutes > 0 || seconds > 0) {
				sb.append("T");
				if (hours > 0)
					sb.append(Integer.toString(hours) + "H");
				if (minutes > 0)
					sb.append(Integer.toString(minutes) + "M");
				if (seconds > 0)
					sb.append(Long.toString(seconds) + "S");
			}
		}
		return sb.toString();
	}
}
