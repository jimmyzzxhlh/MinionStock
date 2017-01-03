package stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import stock.StockEnum.StockCandleDataType;

/**
 * Class for a list of candles.  
 * This class shouldn't be specific to daily candles or intraday candles. Whether or not it represents daily candles
 * or intraday candles should be determined by the actual StockCandle objects inside the list. 
 */
public class CandleList<T extends AbstractCandle> {
    protected List<T> candles;
    protected Map<DateTime, T> dateTimeMap;
    protected Company company;
    protected long volume;
    protected double low = Double.MAX_VALUE;
    protected double high = Double.MIN_VALUE;
    
    public CandleList(Company company) {
        this.company = company;
        candles = new ArrayList<>();
        dateTimeMap = new HashMap<>();
    }
    
    /**
     * Deep copy a candle list.
     */
    public CandleList(CandleList<T> candleList) {
        this.candles = new ArrayList<>();
        this.dateTimeMap = new HashMap<>();
        this.company = new Company(company);
        this.volume = candleList.volume;
        this.low = candleList.low;
        this.high = candleList.high;
        for (int i = 0; i < candleList.size(); i++) {
            T candle = candleList.get(i).copy();
            this.candles.add(candle);
        }                
        for (Map.Entry<DateTime, T> entry : candleList.dateTimeMap.entrySet()) {
        	DateTime dateTime = entry.getKey();
        	T candle = entry.getValue();
        	this.dateTimeMap.put(new DateTime(dateTime), candle.copy());
        }
    }
    
    public void destroy() {
        if (candles != null) {
            candles.clear();
            candles = null;
        }
        if (dateTimeMap != null) {
        	dateTimeMap.clear();
        	dateTimeMap = null;
        }
        company = null;
    }

    public List<T> getCandles()          { return candles; }
    public Company getCompany()          { return company; }
    public String getSymbol()            { return company.getSymbol(); }
    public long getOutstandingShares()   { return company.getOutstandingShares(); }
    
    public double getHigh(int index)  { return candles.get(index).high;   }
    public double getLow(int index)   { return candles.get(index).low;    }
    public double getOpen(int index)  { return candles.get(index).open;   }
    public double getClose(int index) { return candles.get(index).close;  }
    public long getVolume(int index)  { return candles.get(index).volume; }
    public DateTime getDateTime(int index)    { return candles.get(index).getDateTime(); }
    public double getTurnoverRate(int index)  { return candles.get(index).getVolume() / getOutstandingShares(); }
    
    public double getClose(DateTime date) { return getClose(getDateIndex(date)); }
    public double getHigh()           { return high;   }
    public double getLow()            { return low;    }
    public long getVolume()           { return volume; }
    public double getOpen()  { return candles.get(0).getOpen();  }
    public double getClose() { return candles.get(size() - 1).getClose(); }
    
    
    public int size() {
        return candles.size();
    }
    
    public T get(int index) {
        return candles.get(index);
    }
    
    public void add(T candle) {
        candles.add(candle);
        volume += candle.volume;
        low = Double.min(low, candle.low);
        high = Double.max(high, candle.high);
    }
    
    public double getBodyLength() {
        return isWhite() ? getClose() - getOpen() : getOpen() - getClose();
    }
    
    /**
     * Return the length of the upper shadow (which starts from high and ends at close for white candle, or ends at open for black candle).
     */
    public double getUpperShadowLength() {
        return isWhite() ? high - getClose() : high - getOpen();        
    }
    
    public double getLowerShadowLength() {
        return isWhite() ? getOpen() - low : getClose() - low;        
    }
    
    public boolean isUpperShadowLonger() {
        return (getUpperShadowLength() >= getLowerShadowLength());
    }
    
    public boolean isWhite() {
        return (getClose() > getOpen());
    }
    
    public boolean isBlack() {
        return (getClose() < getOpen());
    }
    
    /**
     * Normalize the candles so that they are restricted to a certain range.
     * This is mainly for drawing the candles.
     * @param maxForNormalization
     */
    public void normalizeCandle(double maxForNormalization) {
        normalizeCandle(maxForNormalization, 0, candles.size() - 1);
    }
    
    public void normalizeCandle(double maxForNormalization, int start, int end) {
        T candle;
        double max = 0;
        double min = Double.MAX_VALUE;
        double scale = 1;
        if ((start < 0) || (end >= candles.size())) return;
        for (int i = start; i <= end; i++) {
            candle = candles.get(i);
            if (candle.low < min) {
                min = candle.low;
            }
            if (candle.high > max) {
                max = candle.high;
            }
        }
        scale = maxForNormalization / (max - min);
        for (int i = 0; i < candles.size(); i++) {
            candle = candles.get(i);
            candle.open = (candle.open - min) * scale;
            candle.close = (candle.close - min) * scale;
            candle.high = (candle.high - min) * scale;
            candle.low = (candle.low - min) * scale;            
        }        
    }
    
    public void normalizeCandle(double maxForNormalization, double min, double max, int start, int end) {
        double scale = maxForNormalization / (max - min);
        for (int i = 0; i < candles.size(); i++) {
            AbstractCandle candle = candles.get(i);
            candle.open = (candle.open - min) * scale;
            candle.close = (candle.close - min) * scale;
            candle.high = (candle.high - min) * scale;
            candle.low = (candle.low - min) * scale;            
        }    
    }
    
    /**
     * Sort the candles by their dates.
     */
    public void sortByDate() {
        Collections.sort(candles, (a, b) ->
        {
            if (a.dateTime.isBefore(b.dateTime)) return -1;
            return 1;
        }
        );        
    }
    
    /**
     * Get the maximum stock price during a time range, defined by the current day (index)
     * and the number of days to look forward.
     * @param index The subscript in the stock candle array that represents the current day.
     * @param days Number of days to look forward.
     * @param dataType Type of data to look at (open, close, etc.).
     * @return See description. If nothing can be returned, then return 0.
     */
    public double getMaxPrice(int index, int days, StockCandleDataType dataType) {
        if (index + days - 1 >= candles.size()) return 0;
        double result = 0;
        for (int i = index; i < index + days; i++) {
            double currentPrice = candles.get(i).getStockPrice(dataType);
            if (currentPrice > result) result = currentPrice;
        }
        return result;        
    }

    
    /**
     * Get the minimum stock price during a time range, defined by the current day (index)
     * and the number of days to look forward.
     * @param index The subscript in the stock candle array that represents the current day.
     * @param days Number of days to look forward.
     * @param dataType Type of data to look at (open, close, etc.).
     * @return See description. If nothing can be returned, then return 0.
     */
    public double getMinPrice(int index, int days, StockCandleDataType dataType) {
        if (index + days - 1 >= candles.size()) return 0;
        double result = 0;
        for (int i = index; i < index + days; i++) {
            double currentPrice = candles.get(i).getStockPrice(dataType);
            if ((result == 0) || (currentPrice < result)) result = currentPrice;
        }
        return result;        
    }
    
    /**
     * Use binary search to get the index in the stock candle array given a date.
     * TODO: Can we use map instead of binary search?
     * @param date Input date
     * @param getNearestIndex True if we either return the exact date index or return the nearest date index (so we will always
     *        return a value > 0). False if we can return -1. 
     * @return
     */
    public int getDateIndex(DateTime date, boolean getNearestIndex) {
        int start = 0;
        int maxEnd = candles.size() - 1;
        int end = maxEnd;
        int mid = -1;
        boolean found = false;
        while ((!found) && (start <= end)) {
            mid = (start + end) / 2;
            DateTime midDate = candles.get(mid).getDateTime();
            if (date.isBefore(midDate)) {
                end = mid - 1;
            }
            else if (date.isAfter(midDate)) {
                start = mid + 1;
            }
            else {
                found = true;
            }
        }
        if (!found) {
            int dateIndex = -1;
            //If we need to get the nearest index from the given date
            if (getNearestIndex) {
                dateIndex = mid;
                if (dateIndex < 0) dateIndex = 0;
                if (dateIndex > maxEnd) dateIndex = maxEnd;                
            }
            //Should not happen if the date passed in is a valid date.
            return dateIndex;
        }
        return mid;
    }
    
    public int getDateIndex(DateTime date) {
        return getDateIndex(date, false);
    }
    
    public boolean hasDate(DateTime date) {
        return (getDateIndex(date, false) >= 0);
    }
    
}
            
        
    