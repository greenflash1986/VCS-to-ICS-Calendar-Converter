package com.github.greenflash1986.vcs2icsCalendarConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CalendarDate {

	private static final DateTimeFormatter DATEFORMAT_GMT = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmss'Z'").withZone(
			ZoneId.of("UTC"));
	private static final DateTimeFormatter DATEFORMAT_LOCAL = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

	public static ZonedDateTime parse(String date) throws DateTimeParseException {
		if (date.endsWith("Z")) {
			return ZonedDateTime.parse(date, DATEFORMAT_GMT);
		} else {
			// TODO missing Timezone information, this SHOULD be available in the vcalendar but is not parsed yet
			LocalDateTime tmp = LocalDateTime.parse(date, DATEFORMAT_LOCAL);
			return tmp.atZone(ZoneId.systemDefault());
		}
	}

	public static String formatTimeForDayEvent(ZonedDateTime date) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		String tmp = dtf.format(date);
		return tmp;
	}

	public static String format(ZonedDateTime date) {
		return DATEFORMAT_GMT.format(date);
	}
}
