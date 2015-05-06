package greenflash1986;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CalendarDate {

	private static final SimpleDateFormat DATEFORMAT_GMT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	private static final SimpleDateFormat DATEFORMAT_LOCAL = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	
	static {
		DATEFORMAT_GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static Date parse(String date) throws ParseException {
		if (date.endsWith("Z")) { // UTC
			return DATEFORMAT_GMT.parse(date);
		} else {
			// TODO missing Timezone information, this SHOULD be available in the vcalendar but is not parsed yet
			return DATEFORMAT_LOCAL.parse(date);
		}
	}

	public static String formatTimeForDayEvent(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String tmp = sdf.format(date);
		return tmp;
	}
	
	public static String format(Date date) {
		return DATEFORMAT_GMT.format(date);
	}
}
