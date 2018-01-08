package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import company.Company;
import dynamodb.DynamoDBConst;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_COMPANY)
public class CompanyItem implements DynamoDBItem {
    private String symbol;
    private String sector;
    private String industry;
    private String exchange;
    
    @DynamoDBHashKey(attributeName="S")
    public String getSymbol() { return this.symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    @DynamoDBAttribute(attributeName="SE")
    public String getSector() { return this.sector; }
    public void setSector(String sector) { this.sector = sector; }
    
    @DynamoDBAttribute(attributeName="I")
    public String getIndustry() { return this.industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    @DynamoDBAttribute(attributeName="E")
    public String getExchange() { return this.exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    
    public Company toCompany() {
        Company company = new Company()
            .withSymbol(symbol)
            .withExchange(exchange)
            .withIndustry(industry)
            .withSector(sector);
        
        return company;
    }    
}