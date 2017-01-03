package stock;

import org.joda.time.DateTime;

import stock.StockEnum.StockCandleDataType;

/**
 * This is an abstract class to represent a candle.  
 * @author jimmyzzxhlh-Dell
 *
 */
public abstract class AbstractCandle {
    protected double open;
    protected double close;
    protected double high;
    protected double low;
    protected long volume;
    protected DateTime dateTime;  //The instant will be assumed to be UTC time. This is intentional so that it is easier and consistent to implement.
        
    protected AbstractCandle(DateTime dateTime, double open, double close, double high, double low, long volume) {
        this.dateTime = dateTime;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }
    
    public DateTime getDateTime()   { return dateTime; }
    public double getOpen()         { return open;    }
    public double getClose()        { return close;   }
    public double getHigh()         { return high;    }
    public double getLow()          { return low;     }
    public long getVolume()         { return volume;  }
    
    /**
     * Child class should implement a deep copy function.
     */
    public abstract <T extends AbstractCandle> T copy();
    
    /**
     * Get the specific price (open, close, high, low)
     * @param dataType
     * @return
     */
    public double getStockPrice(StockCandleDataType dataType) {
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
