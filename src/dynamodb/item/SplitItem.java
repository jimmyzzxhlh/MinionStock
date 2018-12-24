package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_SPLIT)
public class SplitItem {
  private String symbol;
  private String date;
  private double factor;
  
  @DynamoDBHashKey(attributeName="S")
  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }
  
  @DynamoDBRangeKey(attributeName="D")
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  
  @DynamoDBAttribute(attributeName="F")
  public double getFactor() { return factor; }
  public void setFactor(double factor) { this.factor = factor; }  
}
