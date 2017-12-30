package util;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CommonUtil {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
	
	/**
	 * Get a DateTime object based on a date string
	 * @param dateString Date with format yyyyMMdd
	 */
	public static DateTime getDateTime(String dateString) {
		return dateTimeFormatter.parseDateTime(dateString);
	}
	
	/**
	 * Split a CSV line.
	 * Remove the quote as well.
	 * @param line
	 * @return
	 */
	public static String[] splitCSVLine(String line) {
		String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i].trim().replaceAll("^\"|\"$", "").trim();
		}
		return data;
	}
	
	/**
	 * Return a delimited string from a list of strings.
	 */
	public static String getDelimitedString(List<String> list, String delimiter) {
		return list.stream().collect(Collectors.joining(delimiter));
	}
	
	public static void scheduleDailyJob(Runnable runnable) {
	    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	    executorService.scheduleAtFixedRate(runnable, 24, 24, TimeUnit.HOURS);
	}
}