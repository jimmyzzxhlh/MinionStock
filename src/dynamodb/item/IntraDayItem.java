package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_INTRA_DAY)
public class IntraDayItem implements DynamoDBItem {
    private String symbol;
	private String time;
	private double high;
	private double low;
	private double average;
	private long numberOfTrades;
	
	@DynamoDBHashKey(attributeName="S")
	public String getSymbol() { return this.symbol; }
	public void setSymbol(String symbol) { this.symbol = symbol; }
	
	@DynamoDBRangeKey(attributeName="T")
	public String getTime() { return this.time; }
	public void setTime(String time) { this.time = time; }
	
	@DynamoDBAttribute(attributeName="H")
	public double getHigh() { return this.high; }
	public void setHigh(double high) { this.high = high; }
	
	@DynamoDBAttribute(attributeName="L")
	public double getLow() { return this.low; }
	public void setLow(double low) { this.low = low; }
	
	@DynamoDBAttribute(attributeName="A")
	public double getAverage() { return this.average; }
	public void setAverage(double average) { this.average = average; }
	
	@DynamoDBAttribute(attributeName="N")
	public long getNumberOfTrades() { return this.numberOfTrades; }
	public void setNumberOfTrades(long numberOfTrades) { this.numberOfTrades = numberOfTrades; }	
}
