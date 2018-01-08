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
import dynamodb.item.DailyItem;
import dynamodb.item.StatusItem;
import enums.JobEnum;
import enums.JobStatusEnum;
import util.CommonUtil;

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
    
    public StatusItem getStatusItem(JobEnum state) {
        return DynamoDBProvider.getInstance().getMapper().load(StatusItem.class, state.toString());
    }

    public DailyItem getLastDailyItem(String symbol) {
        DynamoDBQueryExpression<DailyItem> queryExpression = new DynamoDBQueryExpression<DailyItem>()
            .withLimit(1)
            .withScanIndexForward(false)
            .withHashKeyValues(new DailyItem(symbol));              
        List<DailyItem> result =
            DynamoDBProvider.getInstance().getMapper()
                .queryPage(DailyItem.class, queryExpression).getResults();
        if (result.size() == 0) {   
            return null;
        }
        return result.get(0);
    }
    
    public void saveStatus(Status status) {
        status.setLastUpdatedTime(CommonUtil.getPacificTimeNow());
        DynamoDBProvider.getInstance().getMapper().save(status.toStatusItem());        
    }
    
    /** Any DynamoDB specific helpers go below from here */
    private class Capacity {
        long read;
        long write;
    }
    
    public Capacity getCapacity(String tableName) {
        ProvisionedThroughputDescription throughput = DynamoDBProvider
           .getInstance()
           .getDynamoDB()
           .describeTable(tableName)
           .getTable()
           .getProvisionedThroughput();
        
        Capacity capacity = new Capacity();
        capacity.read = throughput.getReadCapacityUnits();
        capacity.write = throughput.getWriteCapacityUnits();
        return capacity;
        
    }
    
    public void updateReadCapacity(String tableName, long readCapacity) {
        Capacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.read == readCapacity) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(readCapacity)
                .withWriteCapacityUnits(capacity.write));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);        
    }
    
    public void updateWriteCapacity(String tableName, long writeCapacity) {
        Capacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.write == writeCapacity) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(capacity.read)
                .withWriteCapacityUnits(writeCapacity));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);
    }
    
    public void updateCapacity(String tableName, long readCapacity, long writeCapacity) {
        Capacity capacity = getCapacity(tableName);
        // DynamoDB throws exception if capacity is not actually changed, which is pretty dumb...
        if (capacity.read == readCapacity && capacity.write == writeCapacity) {
            return;
        }
        
        UpdateTableRequest request = new UpdateTableRequest()
            .withTableName(tableName)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(readCapacity)
                .withWriteCapacityUnits(writeCapacity));                
        DynamoDBProvider.getInstance().getDynamoDB().updateTable(request);
    }
     
}
