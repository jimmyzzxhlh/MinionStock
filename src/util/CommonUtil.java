package util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CommonUtil {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
	
	public static DateTime getDateTime(String dateTimeStr) {
		return dateTimeFormatter.parseDateTime(dateTimeStr);
	}
}