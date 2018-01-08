package util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss Z");
	private static final ZoneId pacificZoneId = ZoneId.of("America/Los_Angeles");
	
	private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);
	
	/**
	 * Get a DateTime object based on a date string
	 * @param dateString Date with format yyyyMMdd
	 */
	public static LocalDate parseDate(String dateString) {
	    return LocalDate.parse(dateString, dateFormatter);		
	}
	
	public static String formatDate(LocalDate date) {
	    return date.format(dateFormatter);
	}
	
	/**
	 * Get a time string based on hour and minute.
	 * e.g. hour = 9, minute = 5 -> "09:05"
	 */
	public static String formatHourMinute(int hour, int minute) {
	    LocalTime time = LocalTime.of(hour, minute);
	    return time.format(timeFormatter);
	}
	
	public static String formatDateTime(ZonedDateTime dateTime) {
	    return dateTime.format(dateTimeFormatter);
	}
	
	public static ZonedDateTime parseDateTime(String dateTimeString) {
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
	
	public static ZonedDateTime getPacificTimeNow() {
	    return ZonedDateTime.now(pacificZoneId);
	}
		
	/**
	 * Helper method to schedule a daily job.
	 * The hour and minute is assumed to be in PST time. 
	 */
	public static void scheduleDailyJob(Runnable runnable, int hour, int minute) {
	    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	    ZonedDateTime now = getPacificTimeNow();
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
	    log.info(String.format("Job scheduled. Next job will be run at %s.", formatDateTime(next)));
	}
	
	public static <T> void requireNonNull(T obj, String objName) {
	    Objects.requireNonNull(obj, objName + " cannot be null.");
	}
	
	public static <T> void requireEqual(T firstObj, T secondObj) {
	    if (firstObj == null || secondObj == null) {
	        throw new IllegalArgumentException("Cannot pass null objects to check equality.");
	    }
	    if (firstObj.equals(secondObj)) {
	        return;
	    }
	    String message = new StringBuilder()
	        .append("Objects are not equal.")
	        .append(System.lineSeparator())
	        .append("First object : ")
	        .append(firstObj.toString())
	        .append(System.lineSeparator())
	        .append("Second object: ")
	        .append(secondObj.toString())
	        .toString();	    
	    throw new IllegalArgumentException(message);
	}
}