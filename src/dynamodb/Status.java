package dynamodb;

import java.time.ZonedDateTime;

import dynamodb.item.StatusItem;
import enums.JobEnum;
import util.CommonUtil;

public class Status {
    private JobEnum job;
    private String lastUpdatedSymbol;
    private ZonedDateTime lastUpdatedTime;
    
    public JobEnum getJob() {
        return job;
    }
    public void setJob(JobEnum job) {
        this.job = job;
    }
    public String getLastUpdatedSymbol() {
        return lastUpdatedSymbol;
    }
    public void setLastUpdatedSymbol(String lastUpdatedSymbol) {
        this.lastUpdatedSymbol = lastUpdatedSymbol;
    }
    public ZonedDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }
    public void setLastUpdatedTime(ZonedDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
    
    public StatusItem toStatusItem() {
        StatusItem item = new StatusItem();
        item.setJob(job.toString());  // This cannot be null.
        item.setLastUpdatedSymbol(lastUpdatedSymbol);
        if (lastUpdatedTime != null) {
            item.setLastUpdatedTime(CommonUtil.formatDateTime(lastUpdatedTime));
        }
        
        return item;
    }
    
    @Override
    public String toString() {
        return String.format("job = %s, lastUpdatedSymbol = %s, lastUpdatedTime = %s",
            job.toString(),
            lastUpdatedSymbol,
            lastUpdatedTime == null ? "null" : CommonUtil.formatDateTime(lastUpdatedTime));
    }
}
