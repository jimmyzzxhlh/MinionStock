package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_DAILY)
public class DailyItem implements DynamoDBItem {
  private String symbol;
	private String date;
	private double open;
	private double close;	
	private double high;
	private double low;
	private long volume;
	
	public DailyItem() {}
	
	public DailyItem(String symbol) {
	  this.symbol = symbol;
	}
	
	public DailyItem(String symbol, String date) {
	  this.symbol = symbol;
	  this.date = date;
	}
	
	@DynamoDBHashKey(attributeName="S")
	public String getSymbol() { return this.symbol; }
	public void setSymbol(String symbol) { this.symbol = symbol; }
	
	@DynamoDBRangeKey(attributeName="D")
	public String getDate() { return this.date; }
	public void setDate(String date) { this.date = date; }
	
	@DynamoDBAttribute(attributeName="O")
	public double getOpen() { return this.open; }
	public void setOpen(double open) { this.open = open; }
	
	@DynamoDBAttribute(attributeName="C")
	public double getClose() { return this.close; }
	public void setClose(double close) { this.close = close; }
		
	@DynamoDBAttribute(attributeName="H")
	public double getHigh() { return this.high; }
	public void setHigh(double high) { this.high = high; }
	
	@DynamoDBAttribute(attributeName="L")
	public double getLow() { return this.low; }
	public void setLow(double low) { this.low = low; }
	
	@DynamoDBAttribute(attributeName="V")
	public long getVolume() { return this.volume; }
	public void setVolume(long volume) { this.volume = volume; }	
	
	@Override
	public String toString() {
	  return String.format("symbol = %s, date = %s, open = %s, close = %s, high = %s, low = %s, volume = %s, vwap = %s",
	    symbol, date, open, close, high, low, volume);	      
	}
}