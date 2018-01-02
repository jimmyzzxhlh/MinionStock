package download.iex;

import dynamodb.item.DailyItem;

public class DailyData {
	private String date;
	private double open;
	private double close;
	private double high;
	private double low;
	private long volume;
	private double vwap;  //Volume weighted average price 

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public double getVwap() {
		return vwap;
	}
	public void setVwap(double vwap) {
		this.vwap = vwap;
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
        item.setDate(date.replace("-", ""));
        item.setOpen(open > 0 ? open : close);
        item.setClose(close);
        item.setHigh(high > 0 ? high : close);
        item.setLow(low > 0 ? low : close);
        item.setVwap(vwap > 0 ? vwap : close);
        item.setVolume(volume);        
        
        return item;
    }
}
