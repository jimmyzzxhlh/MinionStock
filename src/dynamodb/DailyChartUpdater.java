package dynamodb;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import download.DownloadHelper;
import download.iex.DailyData;
import download.iex.IexUrlBuilder;
import dynamodb.item.DailyItem;
import enums.JobEnum;
import util.CommonUtil;

public class DailyChartUpdater implements ChartUpdater {
    
    private static final long WRITE_CAPACITY = 250;
    
    private static final Logger log = LoggerFactory.getLogger(DailyChartUpdater.class);
//    private static final int HOUR = 17;
//    private static final int MINUTE = 0;
    
    public void startJob() {
        try {            
            backfillOldData();
        }
        catch (Exception e) {
            log.error("Failed to backfill old data: ", e);
        }
    }
    
    /**
     * This assumes that the DynamoDb table is empty. Therefore this should only be run once,
     * unless there is something going wrong with the DynamoDB...hopefully not.
     */
    public void backfillOldData() {
        log.info("Updating write capacity to " + WRITE_CAPACITY + " ...");
        DynamoDBHelper.getInstance().updateWriteCapacity(DynamoDBConst.TABLE_DAILY, WRITE_CAPACITY);
        
        log.info("Checking current backfill status ...");
        Status status = DynamoDBHelper.getInstance().getStatusItem(JobEnum.BACKFILL_DAILY_CHART).toStatus();
        log.info("Last updated symbol: " + status.getLastUpdatedSymbol());
        
        log.info("Start backfilling daily chart ...");        
        Gson g = new GsonBuilder().setLenient().create();
        
        for (String symbol : DownloadHelper.downloadCompanies().keySet()) {
            // TODO - Probably need to use try/catch to wrap the whole block here
            // so that we don't stop downloading if one of the symbol is broken.
            // How do we output the error to a different log?            
            if (symbol.compareTo(status.getLastUpdatedSymbol()) < 0) {
                continue;
            }
            String url = new IexUrlBuilder()
                .withSymbol(symbol)
                .withChart()
                .withTwoYears()
                .build();
            log.info("Getting daily chart for " + symbol);
            String str = DownloadHelper.downloadURLToString(url);
            if (str.length() == 0) {
                log.warn("No data downloaded for " + symbol + ". Skipping ...");
                saveStatus(status, symbol);
                continue;
            }
            List<DailyData> dataList =
                Arrays.asList(g.fromJson(str, DailyData[].class));
                
            log.info("Start saving " + dataList.size() + " items ...");
            for (DailyData data : dataList) {
                DailyItem item = data.toDailyItem(symbol);                
                DynamoDBProvider.getInstance().getMapper().save(item);
            }            
            log.info("Done saving " + dataList.size() + " items.");
            saveStatus(status, symbol);                        
        }
        log.info("Done backfilling daily chart.");
        
        log.info("Updating write capacity to " + DynamoDBConst.WRITE_CAPACITY_DEFAULT + " ...");
        DynamoDBHelper.getInstance().updateWriteCapacity(DynamoDBConst.TABLE_DAILY, DynamoDBConst.WRITE_CAPACITY_DEFAULT);
    }
    
    private void saveStatus(Status status, String symbol) {
        status.setLastUpdatedSymbol(symbol);
        status.setLastUpdatedTime(CommonUtil.getPacificTimeNow());
        DynamoDBProvider.getInstance().getMapper().save(status.toStatusItem());
    }
}
