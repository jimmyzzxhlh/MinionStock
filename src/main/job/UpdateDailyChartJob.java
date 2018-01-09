package main.job;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import download.DownloadHelper;
import download.iex.DailyData;
import download.iex.IexUrlBuilder;
import dynamodb.DynamoDBHelper;
import dynamodb.DynamoDBProvider;
import dynamodb.Status;
import dynamodb.item.DailyItem;
import util.CommonUtil;

public class UpdateDailyChartJob implements Job {
    
//    private static final long READ_CAPACITY = 20;
//    private static final long WRITE_CAPACITY = 20;
    private static final long MAX_BATCH_SYMBOLS = 100;  // IEX supports up to 100 symbols in batch download.
    private static final Logger log = LoggerFactory.getLogger(UpdateDailyChartJob.class);
    
    private final Gson g = new GsonBuilder().setLenient().create();
    private final Type type = new TypeToken<Map<String, Map<String, DailyData[]>>>(){}.getType(); 
    
    public void startJob() {
        updateDailyChart();
        JobUtil.scheduleDailyJob(() -> updateDailyChart(), JobEnum.UPDATE_DAILY_CHART);
        log.info(String.format("Scheduled a daily job to update daily chart at %s.",
            CommonUtil.formatTime(JobUtil.getStartTime(JobEnum.UPDATE_DAILY_CHART))));
    }
    
    public void updateDailyChart() {
//        log.info(String.format("Updating read capacity to %d and write capacity to %d ...",
//            READ_CAPACITY, WRITE_CAPACITY));
//        DynamoDBHelper.getInstance().updateCapacity(DynamoDBConst.TABLE_DAILY, READ_CAPACITY, WRITE_CAPACITY);
        
        log.info("Checking if the daily chart has been updated today ...");
        Status status = DynamoDBHelper.getInstance().getStatusItem(JobEnum.UPDATE_DAILY_CHART).toStatus();        
        if (status.isUpdatedToday()) {
            log.info("Companies have been updated at " + status.getLastUpdatedTime() + ". Skipping the update.");
            return;
        }
        
        log.info("Start updating daily chart ...");
        status.setJobStatus(JobStatusEnum.UPDATING);
        DynamoDBHelper.getInstance().saveStatus(status);
        
        try {
            List<String> symbols = new ArrayList<>(); 
        
            for (String symbol : DownloadHelper.downloadCompanies().keySet()) {
                symbols.add(symbol);
                if (symbols.size() == MAX_BATCH_SYMBOLS) {
                    batchUpdateDailyChart(symbols);
                    symbols.clear();                
                }                        
            }
            if (symbols.size() > 0) {
                batchUpdateDailyChart(symbols);
            }        
            log.info("Done updating daily chart.");
            status.setJobStatus(JobStatusEnum.DONE);
            DynamoDBHelper.getInstance().saveStatus(status);
        }
        catch (Exception e) {
            log.error("Failed to update daily chart: ", e);
            status.setJobStatus(JobStatusEnum.FAILED);
            DynamoDBHelper.getInstance().saveStatus(status);
        }
        
//        log.info(String.format("Updating read capacity to %d and write capacity to %d ...",
//            DynamoDBConst.READ_CAPACITY_DEFAULT, DynamoDBConst.WRITE_CAPACITY_DEFAULT));            
//        DynamoDBHelper.getInstance().updateCapacity(
//            DynamoDBConst.TABLE_DAILY, DynamoDBConst.READ_CAPACITY_DEFAULT, DynamoDBConst.WRITE_CAPACITY_DEFAULT);        
    }
    
    private void batchUpdateDailyChart(List<String> symbols) {
        String url = new IexUrlBuilder()
                .withBatch()
                .withSymbols(symbols)
                .withChart()
                .withOneMonth()                
                .build();
        log.info(String.format("Getting one month daily chart from %s to %s ...",
            symbols.get(0),
            symbols.get(symbols.size() - 1)
        ));        
        
        String str = DownloadHelper.downloadURLToString(url);
        if (str.length() == 0) {
            log.error("No data downloaded. This should probably not happen.");                
            return;
        }
    
        Map<String, Map<String, DailyData[]>> dataMap = g.fromJson(str, type);        
        for (String symbol : symbols) {
            log.info("Checking the last updated daily chart downloaded for " + symbol + " ...");
            DailyItem lastItem = DynamoDBHelper.getInstance().getLastDailyItem(symbol);
            List<DailyData> dataList = Arrays.asList(dataMap.get(symbol).get("chart"));
            if (lastItem == null) {
                log.info("Cannot find last updated item for " + symbol + ". Is this a new symbol?");
                if (dataList.size() == 0) {
                    log.info(String.format("No data is found for %s. Skipping ...", symbol));
                    continue;
                }
            }
            else {
                log.info("Last updated date is " + lastItem.getDate() + ".");
                dataList = dataList.stream()
                    .filter(data -> data.getDate().compareTo(lastItem.getDate()) > 0)
                    .collect(Collectors.toList());
                if (dataList.size() == 0) {    
                    log.info(String.format("No data after %s is found for %s. Skipping ...",
                        lastItem.getDate(), symbol));
                    continue;
                }
            }            
            
            log.info(String.format("Start saving %d items for %s ...", dataList.size(), symbol));
            for (DailyData data : dataList) {
                DailyItem item = data.toDailyItem(symbol);                
                DynamoDBProvider.getInstance().getMapper().save(item);
            }            
            log.info(String.format("Done saving %d items for %s.", dataList.size(), symbol));            
        }
    }
    
    private void saveStatus(Status status, String symbol) {
        status.setLastUpdatedSymbol(symbol);
        status.setLastUpdatedTime(CommonUtil.getPacificTimeNow());
        DynamoDBProvider.getInstance().getMapper().save(status.toStatusItem());
    }
}
