package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;
import dynamodb.Status;
import enums.JobEnum;
import util.CommonUtil;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_STATUS)
public class StatusItem {    
    private String job;
    private String lastUpdatedSymbol;
    private String lastUpdatedTime;
    
    @DynamoDBHashKey(attributeName="J")
    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }
    
    @DynamoDBAttribute(attributeName="LUS")    
    public String getLastUpdatedSymbol() { return lastUpdatedSymbol; }    
    public void setLastUpdatedSymbol(String lastUpdatedSymbol) { this.lastUpdatedSymbol = lastUpdatedSymbol; }
    
    @DynamoDBAttribute(attributeName="LUT")
    public String getLastUpdatedTime() { return lastUpdatedTime; }
    public void setLastUpdatedTime(String lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }
    
    public Status toStatus() {
        Status status = new Status();
        status.setJob(JobEnum.get(job));
        status.setLastUpdatedSymbol(lastUpdatedSymbol);
        if (lastUpdatedTime != null) {
            status.setLastUpdatedTime(CommonUtil.getDateTime(lastUpdatedTime));        
        }
        
        return status;
    }
    
}