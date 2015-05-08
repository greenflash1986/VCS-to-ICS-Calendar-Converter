package greenflash1986;

import java.time.ZonedDateTime;

/**
 * @see <a href=http://www.kanzaki.com/docs/ical/rrule.html>Doku</a>
 * @author GreenFlash1986
 *
 */
public class RepeatRule {

	public enum Frequency {
		DAILY, WEEKLY, MONTHLY, YEARLY
	};

	private Frequency frequency;
	private int interval;
	private ZonedDateTime until;
	private int occurences;

	public RepeatRule(Frequency frequency, int interval) {
		this.frequency = frequency;
		this.interval = interval;
	}

	public RepeatRule(Frequency frequency, int interval, ZonedDateTime until) {
		this(frequency, interval);
		this.until = until;
	}

	public RepeatRule(Frequency frequency, int interval, int occurences) {
		this(frequency, interval);
		this.occurences = occurences;
	}

	public String toICS() {
		StringBuilder sb = new StringBuilder("RRULE:FREQ=" + frequency.toString());
		if (interval > 0) {
			sb.append(";INTERVAL=" + Integer.toString(interval));
		}

		if (occurences > 0) {
			sb.append(";COUNT=" + Integer.toString(occurences));
		} else if (until != null) {
			sb.append(";UNTIL=" + CalendarDate.format(until)); // TODO at this time, the enddate is read in local
																// timezone put the output is GMT
		}
		return sb.toString();
	}

	public static RepeatRule parse(String rrule, boolean useEndDate) {

		// Examples:
		// RRULE:W2 TU TH // Every other week, on Tuesday and Thursday
		// RRULE:D1 #10 // Daily for 10 occurrences
		// RRULE:YM1 6 7 #8 // Yearly in June and July for 8 occurrences
		// RRULE:YM1 2 21001231T000000 // every year in February
		// till 31.12.2100
		//

		String[] parts = rrule.split(" ");
		String part0 = parts[0];

		// check for end date
		// check for number of occurences
		String occur = parts[parts.length - 1];
		ZonedDateTime occurDate = null;
		int occurCnt = 0;
		try {
			if (occur.startsWith("#")) {
				occurCnt = Integer.parseInt(occur.substring(1));
			} else if (occur.contains("T") && useEndDate) {
				occurDate = CalendarDate.parse(occur);
			}
		} catch (Exception e) {
			// this is not nice, but quick
			System.out.println("Could not parse the occurence information");
		}

		// check if weekly, monthly or daily
		Frequency freq = null;
		if (part0.startsWith("D")) {
			freq = Frequency.DAILY;
		} else if (part0.startsWith("W")) {
			freq = Frequency.WEEKLY;
		} else if (part0.startsWith("MD"))
			freq = Frequency.MONTHLY;
		else if (part0.startsWith("YM")) {
			freq = Frequency.YEARLY;
		} else {
			System.out.println("New unknown frequency detected."); // This should be an exception
			return null;
		}

		// check for frequency 2
		String intervalStr = null;
		switch (freq) {
		case DAILY:
		case WEEKLY:
			intervalStr = part0.substring(1);
			break;
		case MONTHLY:
		case YEARLY:
			intervalStr = part0.substring(2);
		}

		int interval = 0;
		try {
			interval = Integer.parseInt(intervalStr);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			return null;
		}
		// TODO parse the months or days

		if (occurDate != null) {
			return new RepeatRule(freq, interval, occurDate);
		} else if (occurCnt > 0) {
			return new RepeatRule(freq, interval, occurCnt);
		} else {
			return new RepeatRule(freq, interval);
		}
	}
}
