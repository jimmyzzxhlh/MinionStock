package download.iex;

import com.google.gson.annotations.SerializedName;

/**
 * Stores data for intra day from IEX.
 * 
 * Note: We get data from market* fields because the normal fields do not really return the
 * actual data for some reason. 
 * 
 * @author jimmyzzxhlh-Dell
 *
 */
public class IntraDayData {
	private String date;
	private String minute;	
	@SerializedName("marketHigh") private double high;
	@SerializedName("marketLow") private double low;
	@SerializedName("marketAverage") private double average;
	@SerializedName("marketVolume") private long volume;
	@SerializedName("marketNumberOfTrades") private long numberOfTrades;
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
	public long getNumberOfTrades() {
		return numberOfTrades;
	}
	public void setNumberOfTrades(long numberOfTrades) {
		this.numberOfTrades = numberOfTrades;
	}	
}
