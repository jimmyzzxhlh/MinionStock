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
public class CandleList<T extends AbstractCandle<T>> {
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
    
    public double getClose(DateTime dateTime) { return getCandle(dateTime).getClose(); }
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
        dateTimeMap.put(candle.getDateTime(), candle);
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
    
    /**
     * Return true if the candle list as a whole is a white candle, i.e. Close price of the last candle
     * is higher or equal to the open price of the first candle.  
     */
    public boolean isWhite() {
        return (getClose() > getOpen());
    }
    
    /**
     * Return true if the candle list as a whole is a black candle, i.e. Close price of the last candle
     * is lower than the open price of the first candle.  
     */
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
    
    /**
     * Normalize the candles so that the highest price is equal to maxForNormalization.
     */
    public void normalizeCandle(double maxForNormalization, int start, int end) {
        T candle;
        double max = 0;
        double min = Double.MAX_VALUE;
        double scale = 1;
        if ((start < 0) || (end >= candles.size())) return;
        for (int i = start; i <= end; i++) {
            candle = candles.get(i);
            min = Double.min(min, candle.low);
            max = Double.max(max, candle.high);
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
     * Get the maximum stock price during a time range.
     * @param start The start index
     * @param end The end index
     * @param dataType Type of data to look at (open, close, etc.).
     * @return See description. If nothing can be returned, then return 0.
     */
    public double getMaxPrice(int start, int end, StockCandleDataType dataType) {
        if (start < 0 || end >= candles.size()) return 0;  //TODO - Should log an error.
        
        double max = 0;
        for (int i = start; i <= end; i++) {
            max = Double.max(max, candles.get(i).getStockPrice(dataType));
        }
        return max;        
    }
    
    /**
     * Get the minimum stock price during a time range, defined by the current index
     * and the number of candles to look forward.
     * @param index The index of the first candle.
     * @param count Number of candles to look forward.
     * @param dataType Type of data to look at (open, close, etc.).
     * @return See description. If nothing can be returned, then return 0.
     */
    public double getMinPrice(int start, int end, StockCandleDataType dataType) {
    	if (start < 0 || end >= candles.size()) return 0;  //TODO - Should log an error.
        
    	double min = 0;
        for (int i = start; i <= end; i++) {
        	double price = candles.get(i).getStockPrice(dataType);
        	min = (min == 0 ? price : Double.min(min, price));
        }
        return min;        
    }
    
    /**
     * Given a datetime object, get the corresponding candle. If there is no such datetime, return null.
     */
    public T getCandle(DateTime dateTime) {
        return dateTimeMap.getOrDefault(dateTime, null);
    }
    
    /**
     * Return true if the datetime can be found in the candle list, false if not.
     */
    public boolean hasCandle(DateTime dateTime) {
        return dateTimeMap.containsKey(dateTime);
    }
    
}
            
        
    