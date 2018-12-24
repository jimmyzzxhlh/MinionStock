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
  private boolean isTesting;
  
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
  public boolean isTesting() {
    return isTesting;
  }
  public void setTesting(boolean isTesting) {
    this.isTesting = isTesting;
  }
  
  public StatusItem toStatusItem() {
    StatusItem item = new StatusItem();
    item.setJob(job.toString());  // This cannot be null as it is the hash key.
    item.setLastUpdatedSymbol(lastUpdatedSymbol);
    item.setLastStartTime(lastStartTime == null ? "" : CommonUtil.formatDateTime(lastStartTime));
    item.setLastEndTime(lastEndTime == null ? "" : CommonUtil.formatDateTime(lastEndTime));
    item.setJobStatus(jobStatus == null ? "" : jobStatus.toString());
    item.setTesting(isTesting);
    
    return item;
  }
  
  public boolean isUpdatedToday() {
    if (lastStartTime == null || lastEndTime == null) return false;
    return jobStatus == JobStatusEnum.DONE &&
      lastStartTime.toLocalDate().equals(CommonUtil.getPacificTimeNow().toLocalDate());    
  }
  
  @Override
  public String toString() {
    return String.format("job = %s, lastUpdatedSymbol = %s, lastStartTime = %s, lastEndTime = %s, " +
      "jobStatus = %s, isTesting = %s",
      job == null ? "null" : job.toString(),
      lastUpdatedSymbol,
      lastStartTime == null ? "null" : CommonUtil.formatDateTime(lastStartTime),
      lastEndTime == null ? "null" : CommonUtil.formatDateTime(lastEndTime),
      jobStatus == null ? "null" : jobStatus.toString(),
      isTesting);
  }
}
