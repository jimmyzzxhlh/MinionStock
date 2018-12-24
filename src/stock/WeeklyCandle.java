package stock;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.TreeMap;

import org.mockito.cglib.core.Local;

import software.amazon.ion.impl.PrivateIonSystem;
import util.CommonUtil;

public class WeeklyCandle extends AbstractCandle {
  private TreeMap<LocalDateTime, DailyCandle> dailyCandles;
  
  // See http://joda-time.sourceforge.net/field.html
  private int week;  // Week of week based year
  private int year;  // Week based year
  
  public WeeklyCandle(LocalDate startDate) {
    this.dt = LocalDateTime.of(startDate, LocalTime.of(0, 0));
    this.year = startDate.get(IsoFields.WEEK_BASED_YEAR);
    this.week = startDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    this.dailyCandles = new TreeMap<>();
    this.low = Double.MAX_VALUE;
    this.high = Double.MIN_VALUE;
  }
  
  public WeeklyCandle(String startDateStr) {
    this(CommonUtil.parseDate(startDateStr));
  }
  
  public int getYear() {
    return year;
  }
  
  public int getWeek() {
    return week;
  }
  
  /**
   * Get the end date of the weekly candle.
   * This can be used as a key for a list of weekly candles.
   */
  public LocalDate getEndDate() {
    if (this.dt == null) return null;
    return this.dt.toLocalDate();
  }
  
  public boolean isSameWeek(DailyCandle candle) {
    int year = candle.getDateTime().get(IsoFields.WEEK_BASED_YEAR);
    int week = candle.getDateTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); 
    return this.year == year && this.week == week;
  }
  
  public void addDailyCandle(DailyCandle candle) {
    // Make sure we are adding candle to the same week.
    if (!isSameWeek(candle)) {
      throw new RuntimeException(String.format(
         "DailyCandle does not have the same week based year with the weekly candle. " + 
         "DailyCandle: %s, year: %d, week %d; Weekly Candle: year: %d, week: %d",
         candle.toString(), year, week, this.year, this.week));
    }
    
    if (dailyCandles.containsKey(candle.getDateTime())) {
      throw new RuntimeException(String.format(
        "WeeklyCandle already has a daily candle on date %s. " +
        "DailyCandle in WeeklyCandle: %s; DailyCandle to be added: %s",
        candle.getDateTime(), dailyCandles.get(candle.getDateTime()).toString(), candle));
    }
    
    // Put the candle and also update the date time of the weekly candle so that the date time
    // always tracks the last daily candle.
    this.dailyCandles.put(candle.getDateTime(), candle);    
    this.dt = this.dailyCandles.lastKey();
    
    this.open = this.dailyCandles.firstEntry().getValue().getOpen();
    this.high = Math.max(this.high, candle.getHigh());
    this.low = Math.min(this.low, candle.getLow());
    this.close = this.dailyCandles.lastEntry().getValue().getClose();
    this.volume += candle.getVolume();    
  }
}
