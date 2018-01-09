package main.job;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import dynamodb.DynamoDBCapacity;
import util.CommonUtil;

public class JobUtil {
    private static final Logger log = LoggerFactory.getLogger(JobUtil.class);
    
    private static final ImmutableMap<JobEnum, LocalTime> jobStartTimeMap =
        ImmutableMap.of(
            JobEnum.UPDATE_COMPANY, LocalTime.of(16, 0),
            JobEnum.UPDATE_DAILY_CHART, LocalTime.of(19, 0)  // IEX doesn't have daily data before 5 pm.
        );

    private static final ImmutableMap<JobEnum, DynamoDBCapacity> jobCapacityMap = 
        ImmutableMap.of(
            JobEnum.UPDATE_DAILY_CHART, new DynamoDBCapacity(30, 20)
        );
    
    public static LocalTime getStartTime(JobEnum job) {
        return jobStartTimeMap.get(job);
    }
    
    public static ImmutableMap<JobEnum, DynamoDBCapacity> getCapacityMap() {
        return jobCapacityMap;
    }
    
    
    /**
     * Helper method to schedule a daily job.
     * The hour and minute is assumed to be in PST time. 
     */
    public static void scheduleDailyJob(Runnable runnable, JobEnum job) {
        LocalTime time = getStartTime(job);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        ZonedDateTime now = CommonUtil.getPacificTimeNow();
        ZonedDateTime next = ZonedDateTime.of(
            now.getYear(),
            now.getMonth().getValue(),
            now.getDayOfMonth(),
            time.getHour(),
            time.getMinute(),
            0,
            0,
            CommonUtil.PACIFIC_ZONE_ID);     
        if (next.isBefore(now)) {
            next = next.plusDays(1);
        }
        Duration delay = Duration.between(now, next);       
        executorService.scheduleAtFixedRate(runnable, delay.getSeconds(), 86400, TimeUnit.SECONDS);
        log.info(String.format("Job scheduled. Next job will be run at %s.", CommonUtil.formatDateTime(next)));
    }
    
    public static void scheduleFixRateJob(Runnable runnable, long period, TimeUnit timeUnit) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, period, timeUnit);        
    }
}
