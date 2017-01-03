package stock;

import org.joda.time.DateTime;

/**
 * Class for a daily candle from Yahoo's data.
 * @author jimmyzzxhlh-Dell
 *
 */
public class DailyCandle extends AbstractCandle {
    
    /**
     * Constructor given the adjusted close. This is for stock splitting. A stock can have multiple
     * splits. See AAPL for an example.
     */
    public DailyCandle(DateTime instant, double open, double close, double high, double low, long volume, double adjClose) {
        super(instant, open, close, high, low, volume);
        //Use the adjusted close price to calculate the rest.
        double ratio = adjClose / close;
        open *= ratio;
        close = adjClose;
        high *= ratio;
        low *= ratio;        
    }
    
    public DailyCandle(DateTime instant, double open, double close, double high, double low, long volume) {
        super(instant, open, close, high, low, volume);
    }
    
    @Override
    public DailyCandle copy() {
        DailyCandle candle = new DailyCandle(
                this.dateTime,
                this.open,
                this.close,
                this.high, 
                this.low, 
                this.volume);
        return candle;
    }
        
}


