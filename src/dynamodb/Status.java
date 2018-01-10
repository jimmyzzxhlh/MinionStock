package dynamodb;

import java.time.ZonedDateTime;

import dynamodb.item.StatusItem;
import main.job.JobEnum;
import main.job.JobStatusEnum;
import util.CommonUtil;

public class Status {
    private JobEnum job;
    private String lastUpdatedSymbol;
    private ZonedDateTime lastStartTime;
    private ZonedDateTime lastEndTime;
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
    public ZonedDateTime getLastStartTime() {
        return lastStartTime;
    }
    public void setLastStartTime(ZonedDateTime lastStartTime) {
        this.lastStartTime = lastStartTime;
    }    
    public ZonedDateTime getLastEndTime() {
        return lastEndTime;
    }
    public void setLastEndTime(ZonedDateTime lastEndTime) {
        this.lastEndTime = lastEndTime;
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
        if (lastStartTime != null) {
            item.setLastStartTime(CommonUtil.formatDateTime(lastStartTime));
        }
        if (lastEndTime != null) {
            item.setLastEndTime(CommonUtil.formatDateTime(lastEndTime));
        }
        if (jobStatus != null) {
            item.setJobStatus(jobStatus.toString());
        }
        return item;
    }
    
    public boolean isUpdatedToday() {
        if (lastStartTime == null) return false;
        return jobStatus == JobStatusEnum.DONE &&
            lastStartTime.toLocalDate().equals(CommonUtil.getPacificTimeNow().toLocalDate());        
    }
    
    @Override
    public String toString() {
        return String.format("job = %s, lastUpdatedSymbol = %s, lastStartTime = %s, lastEndTime = %s, jobStatus = %s",
            job == null ? "null" : job.toString(),
            lastUpdatedSymbol,
            lastStartTime == null ? "null" : CommonUtil.formatDateTime(lastStartTime),
            lastEndTime == null ? "null" : CommonUtil.formatDateTime(lastEndTime),
            jobStatus == null ? "null" : jobStatus.toString());
    }
}
