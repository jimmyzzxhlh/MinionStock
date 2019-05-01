package stock;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import enums.StockEnum.CandleDataType;

/**
 * This is an abstract class to represent a candle.  
 * @author jimmyzzxhlh-Dell
 *
 */
public abstract class AbstractCandle {
  protected LocalDateTime dt;
  protected double open;
  protected double close;
  protected double high;
  protected double low;
  protected long volume;
    
  protected AbstractCandle() {}
  
  public LocalDateTime getDateTime() { return dt; }
  public double getOpen()     { return open;  }
  public double getClose()    { return close;   }
  public double getHigh()     { return high;  }
  public double getLow()      { return low;   }
  public long getVolume()     { return volume;  }
  
  public <T extends AbstractCandle> T withDateTime(LocalDateTime dt)  { this.dt = dt; return (T) this; }
  public <T extends AbstractCandle> T withOpen(double open)       { this.open = open; return (T) this; }
  public <T extends AbstractCandle> T withClose(double close)     { this.close = close; return (T) this; }
  public <T extends AbstractCandle> T withHigh(double high)       { this.high = high; return (T) this; }
  public <T extends AbstractCandle> T withLow(double low)       { this.low = low; return (T) this; }
  public <T extends AbstractCandle> T withVolume(long volume)     { this.volume = volume; return (T) this; }    
  
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
  
  /**
   * Return an estimate of the volume map of a candle. Basically, the shadows are traversed twice so they should have
   * double weight of the body. 
   *  
   * Assuming:
   * x = Percentage of upper shadow length w.r.t the total length.
   * y = Percentage of lower shadow length w.r.t the total length.
   * Therefore percentage of body length is 1 - x - y.
   * Let:
   * a = the weight for the shadow length.
   * b = the weight for the body length.
   * 0.1 = the weight for the open/close price point. These tend to have many more volumes than other price. 
   * We have
   * a = 2b 
   * x * a + y * a + (1 - x - y) * b + 0.1 * 2 = 1
   * Then we can denote that
   * a = 0.8 / (x + y + 1) 
   * b = 1.6 / (x + y + 1)
   * 
   * The returned map will contain 301 entries where 100 is for each shadow length, 99 is for body length (since open and close
   * prices need to be excluded), and 2 for open and close price.
   */
  public Map<Double, Double> getVolumeEstimate() {
    Map<Double, Double> volumeMap = new TreeMap<>();
    double lowerShadowLength = getLowerShadowLength();
    double upperShadowLength = getUpperShadowLength();
    double bodyLength = getBodyLength();
    double totalLength = getTotalLength();
    
    double lowerShadowPercentage = lowerShadowLength / totalLength;
    double upperShadowPercentage = upperShadowLength / totalLength;
    double bodyPercentage = bodyLength / totalLength;
    
    double denominator = lowerShadowPercentage + upperShadowPercentage + 1.0;
    double shadowWeight = 1.6 / denominator;
    double bodyWeight = 0.8 / denominator;
    double openCloseWeight = 0.1;
    
    // Add open and close price to the map
    volumeMap.put(open, volume * openCloseWeight);
    volumeMap.put(close,  volume * openCloseWeight);
    
    // Add lower shadow
    for (double price = low; price < Math.min(open, close) - 1e-5; price += lowerShadowLength / 100.0) {
      volumeMap.put(price, volume / 100.0 * lowerShadowPercentage * shadowWeight);
    }
    
    // Add upper shadow
    for (double price = high; price > Math.max(open, close) + 1e-5; price -= upperShadowLength / 100.0) {
      volumeMap.put(price, volume / 100.0 * upperShadowPercentage * shadowWeight);
    }
    
    // Add body
    for (double price = Math.min(open, close) + bodyLength / 100.0; price < Math.max(open, close) - 1e-5; price += bodyLength / 100.0) {
      volumeMap.put(price, volume / 99.0 * bodyPercentage * bodyWeight);
    }
    
    return volumeMap;
  }
  
  @Override
  public String toString() {
    return String.format("datetime=%s, open=%f, high=%f, low=%f, close=%f, volume=%d",
      dt == null ? "null" : dt.toString(), open, high, low, close, volume);
  }
}
