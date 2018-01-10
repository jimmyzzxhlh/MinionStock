package main.job;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamodb.DynamoDBHelper;
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
            if (!JobUtil.hasJobConfig(job)) {
                log.info(String.format("Job %s does not have a configuration, skipped.", job.toString()));                
                continue;
            }
            
            JobConfig config = JobUtil.getJobConfig(job);
            if (config.getCapacity() == null) {
                log.info(String.format("Job %s does not have configuration for capacity, skipped.",
                    job.toString()));
                continue;
            }
            
            LocalTime time = JobUtil.getStartTime(job);
            LocalTime now = CommonUtil.getPacificTimeNow().toLocalTime();
            log.info(String.format("Start time for job %s is %s.", job.toString(), CommonUtil.formatTime(time)));
            if (now.plusMinutes(INTERVAL_IN_MINUTES).isAfter(time) &&
                now.minusMinutes(INTERVAL_IN_MINUTES).isBefore(time)) {
                log.info(String.format("The job is going to start within %d minutes at %s. Updating capacity ...",
                    INTERVAL_IN_MINUTES, CommonUtil.formatTime(time)));
                DynamoDBHelper.getInstance().updateCapacity(config.getTableName(), config.getCapacity());
                log.info(String.format("Table %s capacity is updated to read = %d, write = %d",
                    config.getTableName(), config.getCapacity().getRead(), config.getCapacity().getWrite()));
            }
            else {
                log.info(String.format("%s job is not going to start soon, capacity not updated.",
                    job.toString()));
            }
        }
        log.info("Table capacity update is done.");
    }
}
