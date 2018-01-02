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
        item.setJob(job.toString());
        item.setLastUpdatedSymbol(lastUpdatedSymbol);
        item.setLastUpdatedTime(CommonUtil.getDateTime(lastUpdatedTime));
        
        return item;
    }
}
