package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;

public class CommonUtil {
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss Z");
	private static final HolidayManager holidayManager = HolidayManager.getInstance(HolidayCalendar.UNITED_STATES);
	public static final ZoneId PACIFIC_ZONE_ID = ZoneId.of("America/Los_Angeles");
	
	
//	private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);
	
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
	
	public static boolean isMarketClosedToday() {
	  LocalDate date = getPacificTimeNow().toLocalDate();
	  return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
      date.getDayOfWeek() == DayOfWeek.SUNDAY ||
      holidayManager.isHoliday(date);
	}
	
	public static String removeHyphen(String dateString) {
	  return dateString == null ? null : dateString.replace("-", "");
	}
	
	/**
	 * Get a time string based on hour and minute.
	 * e.g. hour = 9, minute = 5 -> "09:05"
	 */
	public static String formatTime(LocalTime time) {
	  return time.format(timeFormatter);
	}
	
	public static String formatDateTime(ZonedDateTime dateTime) {
	  return dateTime.format(dateTimeFormatter);
	}
	
	public static ZonedDateTime parseDateTime(String dateTimeString) {
	  return ZonedDateTime.parse(dateTimeString, dateTimeFormatter);
	}
	
	public static LocalDateTime getDateTime(LocalDate date) {
	  return LocalDateTime.of(date, LocalTime.of(0, 0));
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
	  return ZonedDateTime.now(PACIFIC_ZONE_ID);
	}
	
	public static String getPacificDateNow() {
	  return formatDate(getPacificTimeNow().toLocalDate());
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
	
	public static boolean isSymbolValid(String symbol) {
	  if (StringUtils.isEmpty(symbol)) return false;
	  return symbol.matches("[a-zA-Z]*");
	}
  
}