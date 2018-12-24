package main.job.daily;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamodb.DynamoDBCapacity;
import dynamodb.DynamoDBHelper;
import dynamodb.Status;
import main.job.Job;
import main.job.JobConfig;
import main.job.JobEnum;
import main.job.JobUtil;
import util.CommonUtil;

/**
 * This job does several things related to job configuration and status.
 * - Checks whether the other jobs are about to start and adjust the capacity that 
 * they need. 
 */
public class UpdateCapacityJob implements Job {
  private static final int INTERVAL_IN_MINUTES = 10;
  
  private static final Logger log = LoggerFactory.getLogger(UpdateCapacityJob.class);
  
  @Override
  public void startJob() {
    JobUtil.scheduleFixRateJob(() -> updateCapacity(), INTERVAL_IN_MINUTES, TimeUnit.MINUTES);
    log.info(String.format("Scheduled a job to update job configuration and status every %d minutes.",
      INTERVAL_IN_MINUTES));
  }
  
  private void updateCapacity() {
    log.info("Start updating table capacity ...");
    for (JobEnum job : JobEnum.values()) {
      // Skip if there is no configuration.
      if (!JobUtil.hasJobConfig(job)) {
        continue;
      
      }
      // Skip if there is no configuration for capacity.
      JobConfig config = JobUtil.getJobConfig(job);
      if (config.getWorkCapacity() == null) {
        continue;
      }
      
      // Check the job status. If the job is in testing mode, no update will be done.
      Status status = DynamoDBHelper.getInstance().getStatus(job);
      if (status.isTesting()) {
        log.info(String.format("Job %s is in testing mode. No capacity update is done.", job.toString()));
        continue;
      }
      
      String tableName = config.getTableName();
      DynamoDBCapacity currentCapacity = DynamoDBHelper.getInstance().getCapacity(tableName);      
      // If the job is finished today, downgrade the capacity to default.      
      if (status.isUpdatedToday()) {
        log.info(String.format("Job %s is finished at %s today.",
          job.toString(), CommonUtil.formatDateTime(status.getLastEndTime())));
        if (currentCapacity.equals(config.getIdleCapacity())) {
          log.info(String.format("Capacity for table %s has already been set to %s. No capacity update is done.",
            tableName, currentCapacity.toString()));
        }
        else {          
          updateCapacityForTable(config.getTableName(), config.getIdleCapacity());
        }
        continue;
      }
      
      // Check if the job is about to start. If yes, upgrade the capacity.
      if (isJobGoingToStart(job)) {
        if (currentCapacity.equals(config.getWorkCapacity())) {
          log.info(String.format("Capacity for table %s has already been set to %s. No capacity update is done.",
            tableName, currentCapacity.toString()));
        }
        updateCapacityForTable(config.getTableName(), config.getWorkCapacity());        
      }
    }
    log.info("Table capacity update job is done.");    
  }
  
  private boolean isJobGoingToStart(JobEnum job) {
    LocalTime startTime = JobUtil.getStartTime(job);
    LocalTime now = CommonUtil.getPacificTimeNow().toLocalTime();
    log.info(String.format("Start time for job %s is %s.", job.toString(), CommonUtil.formatTime(startTime)));
    if (now.plusMinutes(INTERVAL_IN_MINUTES).isAfter(startTime) &&
      now.minusMinutes(INTERVAL_IN_MINUTES).isBefore(startTime)) {
      log.info(String.format("Job %s is going to start within %d minutes at %s.",
        job.toString(), INTERVAL_IN_MINUTES, CommonUtil.formatTime(now)));
      return true;
    }
    else {
      log.info(String.format("%s job is not going to start soon.",
        job.toString()));
      return false;
    }
  }
  
  private void updateCapacityForTable(String tableName, DynamoDBCapacity capacity) {
    log.info(String.format("Updating capacity for table %s ...", tableName));
    DynamoDBHelper.getInstance().updateCapacity(tableName, capacity);
    log.info(String.format("Table %s capacity is updated to %s.", tableName, capacity.toString()));
  }
}
