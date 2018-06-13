package main.job.daily;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamodb.DynamoDBHelper;
import dynamodb.Status;
import exceptions.JobException;
import main.job.Job;
import main.job.JobConfig;
import main.job.JobEnum;
import main.job.JobStatusEnum;
import main.job.JobUtil;
import util.CommonUtil;

public abstract class DailyJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(DailyJob.class);

    private JobEnum job;
    private JobConfig jobConfig;
    
    public DailyJob(JobEnum job) {   
        this.job = job;
        this.jobConfig = JobUtil.getJobConfig(job);        
    }
    
    protected abstract void doUpdate() throws JobException;
    
    @Override
    public void startJob() {
        doDailyUpdate();
        JobUtil.scheduleDailyJob(() -> doDailyUpdate(), job);
        log.info(String.format("Scheduled a daily job to update %s at %s.",
            jobConfig.getJobTarget(),
            CommonUtil.formatTime(jobConfig.getStartTime())));
    }
    
    private void doDailyUpdate() {
        Status status = DynamoDBHelper.getInstance().getStatus(job);       
        
        if (!shouldDoDailyUpdate(status)) {
            return;
        }
        
        log.info(String.format("Start updating  ...", jobConfig.getJobTarget()));
        JobUtil.saveStartStatus(status);
        
        try {
            doUpdate();        
            log.info(String.format("Done updating %s.", jobConfig.getJobTarget()));
            JobUtil.saveEndStatus(status, JobStatusEnum.DONE);
        }
        catch (Exception e) {
            log.error(String.format("Failed to update %s: ", jobConfig.getJobTarget()), e);
            JobUtil.saveEndStatus(status, JobStatusEnum.FAILED);
        }
    }
    
    private boolean shouldDoDailyUpdate(Status status) {
        log.info(String.format("Checking if the %s should be updated ...", jobConfig.getJobTarget()));
        if (status.isTesting()) {
            log.info(String.format("Job %s is in testing mode. Should do the update.", job.toString()));
            return true;
        }
        
        if (CommonUtil.isMarketClosedToday()) {
            log.info("Market is closed today. Should not do the update.");
            return false;
        }
        
        if (status.isUpdatedToday()) {
            log.info(String.format("%s has been updated from %s to %s. Should not do the update.",
                jobConfig.getJobTarget(),
                CommonUtil.formatDateTime(status.getLastStartTime()),
                CommonUtil.formatDateTime(status.getLastEndTime())));
            return false;
        }
        else {
            if (status.getLastEndTime() == null) {
                log.info(String.format("%s was being updated from %s but the update did not finish. Should do the update.",
                    jobConfig.getJobTarget(),
                    CommonUtil.formatDateTime(status.getLastStartTime())));                
            }
            else {
                log.info(String.format("%s was updated from %s to %s which is more than one day ago. Should do the update.",
                    jobConfig.getJobTarget(),
                    CommonUtil.formatDateTime(status.getLastStartTime()),
                    CommonUtil.formatDateTime(status.getLastEndTime())));
            }
            return true;
        }
    }
}
