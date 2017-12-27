package download.iex;

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
}
