package dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest;

import company.Company;
import dynamodb.item.CompanyItem;
import dynamodb.item.StatusItem;
import main.job.JobEnum;

public class DynamoDBHelper {
	
    private static DynamoDBHelper instance;
    private DynamoDBHelper() {}
    
    public static DynamoDBHelper getInstance() {   
        if (instance == null) {
            instance = new DynamoDBHelper();
        }
        return instance;
    }
    
    public Map<String, Company> getCompaniesMap() {
        Map<String, Company> map = new HashMap<>();
        List<CompanyItem> items =
            DynamoDBProvider.getInstance().getMapper().scan(CompanyItem.class, new DynamoDBScanExpression());
        for (CompanyItem item : items) {
            map.put(item.getSymbol(), item.toCompany());
        }
        
        return map;
    }
    
    public Status getStatus(JobEnum state) {
        return DynamoDBProvider.getInstance().getMapper().load(StatusItem.class, state.toString()).toStatus();
    }

    /**
     * Get the item with the highest range key from DynamoDB given the hash key. 
     * @param itemWithHashKey An item with the hash key set only (e.g. symbol)
     */
    public <T> T getLastItem(T itemWithHashKey, Class<T> clazz) {
        DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>()
            .withLimit(1)
            .withScanIndexForward(false)
            .withHashKeyValues(itemWithHashKey);              
        List<T> result =
            DynamoDBProvider.getInstance().getMapper()
                .queryPage(clazz, queryExpression).getResults();
        if (result.size() == 0) {   
            return null;
        }
        return result.get(0);
    }
    
    /** Any DynamoDB specific helpers go below from here */    
    public DynamoDBCapacity getCapacity(String tableName) {
        ProvisionedThroughputDescription throughput = DynamoDBProvider
           .getInstance()
           .getDynamoDB()
           .describeTable(tableName)
           .getTable()
           .getProvisionedThroughput();
        
        DynamoDBCapacity capacity = new DynamoDBCapacity(
            throughput.getReadCapacityUnits(),
            throughput.getWriteCapacityUnits());
        return capacity;
        
    }
    
    public void updateReadCapacity(String tableName, long readCapacity) {
        DynamoDBCapacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.getRead() == readCapacity) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(readCapacity)
                .withWriteCapacityUnits(capacity.getWrite()));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);        
    }
    
    public void updateWriteCapacity(String tableName, long writeCapacity) {
        DynamoDBCapacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.getWrite() == writeCapacity) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(capacity.getRead())
                .withWriteCapacityUnits(writeCapacity));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);
    }
    
    public void updateCapacity(String tableName, DynamoDBCapacity targetCapacity) {
        DynamoDBCapacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.getRead() == targetCapacity.getRead()
            && capacity.getWrite() == targetCapacity.getWrite()) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(targetCapacity.getRead())
                .withWriteCapacityUnits(targetCapacity.getWrite()));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);
    }
     
}
