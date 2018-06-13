package download.tiingo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.mysql.cj.core.util.StringUtils;

import dynamodb.item.DailyItem;
import util.CommonUtil;

public class TiingoDailyData {
    private String date;
    private double open;
    private double close;
    private double high;
    private double low;
    private long volume;
    private double divCash;
    private double splitFactor;
    
    /**
     * Format the date. The date was originally like
     * 2012-01-01T00:00:00.000Z 
     */
    public String getDate() {
        return date == null
            ? null
            : CommonUtil.formatDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME));    
    }
    public void setDate(String date) {
        this.date = date;
    }
    public LocalDate getLocalDate() {
        return StringUtils.isEmptyOrWhitespaceOnly(getDate())
                ? null
                : CommonUtil.parseDate(getDate());
    }
    public double getOpen() {
        return open;
    }
    public void setOpen(double open) {
        this.open = open;
    }
    public double getClose() {
        return close;
    }
    public void setClose(double close) {
        this.close = close;
    }
    public double getHigh() {
        return high;
    }
    public void setHigh(double high) {
        this.high = high;
    }
    public double getLow() {
        return low;
    }
    public void setLow(double low) {
        this.low = low;
    }   
    public long getVolume() {
        return volume;
    }
    public void setVolume(long volume) {
        this.volume = volume;
    }   
    public double getDivCash() {
        return divCash;
    }
    public void setDivCash(double divCash) {
        this.divCash = divCash;
    }
    public double getSplitFactor() {
        return splitFactor;
    }
    public void setSplitFactor(double splitFactor) {
        this.splitFactor = splitFactor;
    }
    
    /**
     * Get a daily item from IEX daily data.
     * 
     * Notice that there might be data where only close price is present. We then set
     * all the other prices to be the same as close price. 
     */
    public DailyItem toDailyItem(String symbol) { 
        DailyItem item = new DailyItem();
        item.setSymbol(symbol);
        item.setDate(getDate());
        item.setOpen(open > 0 ? open : close);
        item.setClose(close);
        item.setHigh(high > 0 ? high : close);
        item.setLow(low > 0 ? low : close);
        item.setVolume(volume);        
        
        return item;
    }
    
    @Override
    public String toString() {
        return String.format("date = %s, open = %f, high = %f, low = %f, close = %f, volume = %d, divCash=%f, splitFactor=%f",
            getDate(), open, high, low, close, volume, divCash, splitFactor);
    }    
}
