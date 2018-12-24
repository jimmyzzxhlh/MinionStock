package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_COMPANY_STATS)
public class CompanyStatsItem implements DynamoDBItem {
  private String symbol;
  private String date;
  private long sharesOutstanding;
  private long sharesFloating;
  private long shortInterest;
  private double shortRatio;
  private double dividendRate;  
  
  @DynamoDBHashKey(attributeName="S")
  public String getSymbol() { return this.symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }
  
  @DynamoDBRangeKey(attributeName="D")
  public String getDate() { return this.date; }
  public void setDate(String date) { this.date = date; }
  
  @DynamoDBAttribute(attributeName="SO")
  public long getSharesOutstanding() { return this.sharesOutstanding; }
  public void setSharesOutstanding(long sharesOutstanding) { this.sharesOutstanding = sharesOutstanding; }
  
  @DynamoDBAttribute(attributeName="SF")
  public long getSharesFloating() { return sharesFloating; }
  public void setSharesFloating(long sharesFloating) { this.sharesFloating = sharesFloating; }
  
  @DynamoDBAttribute(attributeName="SI")
  public long getShortInterest() { return shortInterest; }
  public void setShortInterest(long shortInterest) { this.shortInterest = shortInterest; }
  
  @DynamoDBAttribute(attributeName="SR")
  public double getShortRatio() { return shortRatio; }
  public void setShortRatio(double shortRatio) { this.shortRatio = shortRatio; }
  
  @DynamoDBAttribute(attributeName="DR")
  public double getDividendRate() { return dividendRate; }
  public void setDividendRate(double dividendRate) { this.dividendRate = dividendRate; }
}