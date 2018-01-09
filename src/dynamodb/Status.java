package dynamodb;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import dynamodb.item.StatusItem;
import main.job.JobEnum;
import main.job.JobStatusEnum;
import util.CommonUtil;

public class Status {
    private JobEnum job;
    private String lastUpdatedSymbol;
    private ZonedDateTime lastUpdatedTime;
    private JobStatusEnum jobStatus;
    
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
    public JobStatusEnum getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(JobStatusEnum jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    public StatusItem toStatusItem() {
        StatusItem item = new StatusItem();
        item.setJob(job.toString());  // This cannot be null as it is the hash key.
        item.setLastUpdatedSymbol(lastUpdatedSymbol);
        if (lastUpdatedTime != null) {
            item.setLastUpdatedTime(CommonUtil.formatDateTime(lastUpdatedTime));
        }
        if (jobStatus != null) {
            item.setJobStatus(jobStatus.toString());
        }
        return item;
    }
    
    public boolean isUpdatedToday() {
        if (lastUpdatedTime == null) return false;
        return jobStatus == JobStatusEnum.DONE &&
            lastUpdatedTime.toLocalDate().equals(CommonUtil.getPacificTimeNow().toLocalDate());        
    }
    
    @Override
    public String toString() {
        return String.format("job = %s, lastUpdatedSymbol = %s, lastUpdatedTime = %s, jobStatus = %s",
            job == null ? "null" : job.toString(),
            lastUpdatedSymbol,
            lastUpdatedTime == null ? "null" : CommonUtil.formatDateTime(lastUpdatedTime),
            jobStatus == null ? "null" : jobStatus.toString());
    }
}
