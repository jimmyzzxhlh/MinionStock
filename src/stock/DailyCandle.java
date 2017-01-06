package stock;

/**
 * Class for a daily candle from Yahoo's data.
 * @author jimmyzzxhlh-Dell
 *
 */
public class DailyCandle extends AbstractCandle<DailyCandle> {
    
	public DailyCandle() {
		
	}
	
	/**
	 * Set adjusted close. Adjusted close can be different from close price if the stock has been splited before. 
	 * This function MUST be called after open, high, low, close are set. 
	 */
	public void setAdjClose(double adjClose) {
		double ratio = adjClose / close;
        open *= ratio;
        high *= ratio;
        low *= ratio;
        close = adjClose;        
	}
	
    @Override
    public DailyCandle copy() {
    	DailyCandle candle = new DailyCandle()
    			.withDateTime(this.dateTime)
    			.withOpen(this.open)
    			.withHigh(this.high)
    			.withLow(this.low)
    			.withClose(this.close)
    			.withVolume(this.volume);
    	return candle;
    }
        
}


