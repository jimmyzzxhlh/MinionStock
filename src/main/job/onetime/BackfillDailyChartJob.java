package main.job.onetime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.CollectionUtils;

import dynamodb.DynamoDBHelper;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;
import main.job.Job;
import util.CommonUtil;

/**
 * One-time backfill for daily charts.
 * https://www.quandl.com/data/EOD-End-of-Day-US-Stock-Prices/documentation/bulk-download
 */
public class BackfillDailyChartJob implements Job {

    private static final File file = new File("data/EOD_20180119.csv");
    private static final Logger log = LoggerFactory.getLogger(BackfillDailyChartJob.class);
    
    @Override
    public void startJob() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            TreeMap<String, DailyItem> map = new TreeMap<>();
            String symbol = null;
            String line;
            while ((line = br.readLine()) != null) {
                DailyItem item = getDailyItem(line);
                if (!CommonUtil.isSymbolValid(item.getSymbol())) {
                    continue;
                }
                if (symbol != null && !item.getSymbol().equals(symbol)) {                    
                    backfill(symbol, map);
                    map = new TreeMap<>();
                }                
                map.put(item.getDate(), item);
                symbol = item.getSymbol();
            }
            backfill(symbol, map);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void backfill(String symbol, Map<String, DailyItem> map) {        
        if (MapUtils.isEmpty(map)) {
            log.info(String.format("Did not find any dates to backfill, this should not happen."));
            return;
        }
        log.info(String.format("Start backfilling %d items for %s ...", map.size(), symbol));
        for (DailyItem item : map.values()) {
            DynamoDBProvider.getInstance().getMapper().save(item);
        }
        log.info(String.format("Done backfilling %d items for %s.", map.size(), symbol));
    }
    
//    private LocalDate getDateToBackfill(String symbol, TreeMap<String, DailyItem> map) {
//        DailyItem firstItemFromDB = DynamoDBHelper.getInstance().getFirstItem(new DailyItem(symbol), DailyItem.class);
//        DailyItem lastItemFromDB = DynamoDBHelper.getInstance().getLastItem(new DailyItem(symbol), DailyItem.class);
//        
//        if (firstItemFromDB == null) {
//            return LocalDate.now();
//        }
//        
//        LocalDate leftDate = CommonUtil.parseDate(firstItemFromDB.getDate());
//        LocalDate rightDate = CommonUtil.parseDate(lastItemFromDB.getDate());
//        
//        while (!leftDate.isAfter(rightDate)) {            
//            LocalDate midDate = getMidDate(leftDate, rightDate);
//            DailyItem itemFromDB = DynamoDBHelper.getInstance().getItem(new DailyItem(symbol, CommonUtil.formatDate(midDate)));
//            int daysAdded = 0;
//            while (itemFromDB == null && midDate.isBefore(rightDate)) {
//                daysAdded++;
//                midDate = midDate.plusDays(1);
//                itemFromDB = DynamoDBHelper.getInstance().getItem(new DailyItem(symbol, CommonUtil.formatDate(midDate)));
//            }
//            if (itemFromDB == null) {
//                rightDate = midDate.minusDays(daysAdded + 1);
//                continue;
//            }
//            DailyItem itemFromMap = map.get(CommonUtil.formatDate(midDate));
//            if (itemFromMap != null &&
//                Double.compare(itemFromMap.getOpen(), itemFromDB.getOpen()) == 0) {
//                rightDate = midDate.minusDays(1);
//            }
//            else {
//                leftDate = midDate.plusDays(1); 
//            }
//        }
//        
//        return leftDate;
//    }
//    
//    private LocalDate getMidDate(LocalDate leftDate, LocalDate rightDate) {
//        long daysBetween = ChronoUnit.DAYS.between(leftDate, rightDate);
//        return leftDate.plusDays(daysBetween / 2);        
//    }
    
    private DailyItem getDailyItem(String line) {
        DailyItem item = new DailyItem();
        String[] data = CommonUtil.splitCSVLine(line);
        item.setSymbol(data[0]);
        item.setDate(CommonUtil.removeHyphen(data[1]));
        item.setOpen(Double.parseDouble(data[2]));
        item.setHigh(Double.parseDouble(data[3]));
        item.setLow(Double.parseDouble(data[4]));
        item.setClose(Double.parseDouble(data[5]));
        item.setVolume(Math.round(Double.parseDouble(data[6])));
        
        return item;
    }
}
