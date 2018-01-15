package dynamodb.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import dynamodb.DynamoDBConst;
import dynamodb.Status;
import main.job.JobEnum;
import main.job.JobStatusEnum;
import util.CommonUtil;

@DynamoDBTable(tableName=DynamoDBConst.TABLE_STATUS)
public class StatusItem implements DynamoDBItem {
    private String job;
    private String lastUpdatedSymbol;
    private String lastStartTime;
    private String lastEndTime;
    private String jobStatus;
    private boolean isTesting;
    
    @DynamoDBHashKey(attributeName="J")
    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }
    
    @DynamoDBAttribute(attributeName="LUS")    
    public String getLastUpdatedSymbol() { return lastUpdatedSymbol; }    
    public void setLastUpdatedSymbol(String lastUpdatedSymbol) { this.lastUpdatedSymbol = lastUpdatedSymbol; }
    
    @DynamoDBAttribute(attributeName="LST")
    public String getLastStartTime() { return lastStartTime; }
    public void setLastStartTime(String lastStartTime) { this.lastStartTime = lastStartTime; }
    
    @DynamoDBAttribute(attributeName="LET")
    public String getLastEndTime() { return lastEndTime; }
    public void setLastEndTime(String lastEndTime) { this.lastEndTime = lastEndTime; }
    
    @DynamoDBAttribute(attributeName="JS")
    public String getJobStatus() { return jobStatus; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }
    
    @DynamoDBAttribute(attributeName="T")
    public boolean isTesting() { return isTesting; }
    public void setTesting(boolean isTesting) { this.isTesting = isTesting; }
    
    public Status toStatus() {
        Status status = new Status();
        status.setJob(JobEnum.get(job));
        status.setLastUpdatedSymbol(lastUpdatedSymbol);
        status.setTesting(isTesting);
        if (lastStartTime != null) {
            status.setLastStartTime(CommonUtil.parseDateTime(lastStartTime));        
        }
        if (lastEndTime != null) {
            status.setLastEndTime(CommonUtil.parseDateTime(lastEndTime));
        }
        if (jobStatus != null) {
            status.setJobStatus(JobStatusEnum.get(jobStatus));
        }
        
        return status;
    }    
}
