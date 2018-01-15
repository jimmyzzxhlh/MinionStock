package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_DIVIDEND)
public class DividendItem implements DynamoDBItem {
    private String symbol;
    private String exDate;
    private String paymentDate;
    private String recordDate;
    private String declaredDate;
    private double amount;
    
    public DividendItem() {}
    
    public DividendItem(String symbol) {
        this.symbol = symbol;
    }
    
    @DynamoDBHashKey(attributeName="S")
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    @DynamoDBRangeKey(attributeName="ED")
    public String getExDate() { return exDate; }
    public void setExDate(String exDate) { this.exDate = exDate; }
    
    @DynamoDBAttribute(attributeName="PD")
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    
    @DynamoDBAttribute(attributeName="RD")
    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
    
    @DynamoDBAttribute(attributeName="DD")
    public String getDeclaredDate() { return declaredDate; }
    public void setDeclaredDate(String declaredDate) { this.declaredDate = declaredDate; }
    
    @DynamoDBAttribute(attributeName="A")
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
