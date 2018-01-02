package util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommonUtil {
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss Z");
	private static final ZoneId pacificZoneId = ZoneId.of("America/Los_Angeles");
	
	/**
	 * Get a DateTime object based on a date string
	 * @param dateString Date with format yyyyMMdd
	 */
	public static LocalDate getDate(String dateString) {
		return LocalDate.parse(dateString, dateFormatter);
	}
	
	public static String getDate(LocalDate date) {
	    return date.format(dateFormatter);
	}
	
	/**
	 * Get a time string based on hour and minute.
	 * e.g. hour = 9, minute = 5 -> "09:05"
	 */
	public static String getTime(int hour, int minute) {
	    LocalTime time = LocalTime.of(hour, minute);
	    return time.format(timeFormatter);
	}
	
	public static String getDateTime(ZonedDateTime dateTime) {
	    return dateTime.format(dateTimeFormatter);
	}
	
	public static ZonedDateTime getDateTime(String dateTimeString) {
	    return ZonedDateTime.parse(dateTimeString, dateTimeFormatter);
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
	
	public static ZonedDateTime getPacificTime(LocalDateTime dateTime) {
	    return dateTime.atZone(pacificZoneId);
	}
	
	public static ZonedDateTime getPacificTimeNow() {
	    return ZonedDateTime.now(pacificZoneId);
	}
		
	/**
	 * Helper method to schedule a daily job.
	 * The hour and minute is assumed to be in PST time. 
	 */
	public static void scheduleDailyJob(Runnable runnable, int hour, int minute) {
	    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	    ZonedDateTime now = CommonUtil.getPacificTime(LocalDateTime.now());
	    ZonedDateTime next = ZonedDateTime.of(
	        now.getYear(),
	        now.getMonth().getValue(),
	        now.getDayOfMonth(),
	        hour,
	        minute,
	        0,
	        0,
	        pacificZoneId);	    
	    if (next.isBefore(now)) {
            next = next.plusDays(1);
        }
	    Duration delay = Duration.between(now, next);	    
	    executorService.scheduleAtFixedRate(runnable, delay.getSeconds(), 86400, TimeUnit.SECONDS);
	}
}