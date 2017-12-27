package download.iex;

public class IntraDayData {
	private String date;
	private String minute;
	private String label;
	private double high;
	private double low;
	private double average;
	private long volume;
	private long marketVolume;
	private long numberOfTrades;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public long getMarketVolume() {
		return marketVolume;
	}
	public void setMarketVolume(long marketVolume) {
		this.marketVolume = marketVolume;
	}
	public long getNumberOfTrades() {
		return numberOfTrades;
	}
	public void setNumberOfTrades(long numberOfTrades) {
		this.numberOfTrades = numberOfTrades;
	}	
}
