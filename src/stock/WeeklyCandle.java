package stock;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.TreeMap;

public class WeeklyCandle extends AbstractCandle {
    private TreeMap<LocalDateTime, DailyCandle> dailyCandles;
    
    // See http://joda-time.sourceforge.net/field.html
    private int week;  // Week of week based year
    private int year;  // Week based year
    
    public WeeklyCandle(int year, int week) {
        this.year = year;
        this.week = week;
        this.dailyCandles = new TreeMap<>();
    }
    
    public void addDailyCandle(DailyCandle candle) {
        int year = candle.getDateTime().get(IsoFields.WEEK_BASED_YEAR);
        int week = candle.getDateTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        
        // Make sure we are adding candle to the same week.
        if (this.year != year && this.week != week) {
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
        
        this.dailyCandles.put(candle.getDateTime(), candle);
        this.open = this.dailyCandles.firstEntry().getValue().getOpen();
        this.high = Math.max(this.high, candle.getHigh());
        this.low = Math.min(this.low, candle.getLow());
        this.close = this.dailyCandles.lastEntry().getValue().getClose();
        this.volume += candle.getVolume();        
    }
}
