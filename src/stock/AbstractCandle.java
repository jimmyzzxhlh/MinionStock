package stock;

import org.joda.time.DateTime;

import enums.StockEnum.CandleDataType;

/**
 * This is an abstract class to represent a candle.  
 * @author jimmyzzxhlh-Dell
 *
 */
public abstract class AbstractCandle<T extends AbstractCandle<T>> {
    protected double open;
    protected double close;
    protected double high;
    protected double low;
    protected long volume;
    protected DateTime dateTime;  //The instant will be assumed to be UTC time. This is intentional so that it is easier and consistent to implement.
        
    protected AbstractCandle() {
    	
    }
    
    public DateTime getDateTime()   { return dateTime; }
    public double getOpen()         { return open;    }
    public double getClose()        { return close;   }
    public double getHigh()         { return high;    }
    public double getLow()          { return low;     }
    public long getVolume()         { return volume;  }
    
    public T withDateTime(DateTime dateTime) { this.dateTime = dateTime; return (T) this; }
    public T withOpen(double open)           { this.open = open; return (T) this; }
    public T withClose(double close)         { this.close = close; return (T) this; }
    public T withHigh(double high)           { this.high = high; return (T) this; }
    public T withLow(double low)             { this.low = low; return (T) this; }
    public T withVolume(long volume)         { this.volume = volume; return (T) this; }
    
    /**
     * Child class should implement a deep copy function.
     */
    public abstract T copy();
    
    /**
     * Get the specific price (open, close, high, low)
     * @param dataType
     * @return
     */
    public double getStockPrice(CandleDataType dataType) {
        switch (dataType) {
        case OPEN: return open;
        case CLOSE: return close;
        case HIGH: return high;
        case LOW: return low;
        default:
            break;
        }
        return 0;
    }
    
    /**
     * Return true if the candle is a white candle, i.e. Close price is higher or equal to the open price  
     */
    public boolean isWhiteCandle() {
        return (close >= open);
    }
    
    /**
     * Return true if the candle is a black candle, i.e. Open price is higher than close price 
     */
    public boolean isBlackCandle() {
        return (close < open);
    }
    
    /**
     * Return the body length of the candle. 
     */
    public double getBodyLength() {
        return Math.abs(close - open);
    }
    
    /**
     * Return the total length of the candle, including high and low. 
     */
    public double getTotalLength() {
        return high - low;
    }

    /**
     * Return the length of the upper shadow (which starts from high and ends at close for white candle, or ends at open for black candle).
     */
    public double getUpperShadowLength() {
        return close > open ? high - close : high - open;        
    }
    
    /**
     * Return the length of the lower shadow (which starts from low and ends at close for black candle, or ends at open for white candle).
     */
    public double getLowerShadowLength() {
        return close > open ? open - low : close - low;
    }
    
    @Override
    public String toString() {
        return String.format("dateTime=%s, open=%d, close=%d, low=%d, high=%d, volume=%d",
                               dateTime, open, close, low, high, volume);
    }
}
