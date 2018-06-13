package main.job.onetime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamodb.DynamoDBProvider;
import dynamodb.item.DividendItem;
import main.job.Job;
import util.CommonUtil;

/**
 * One-time job for backfilling dividends.
 * 
 * TODO - change the job to backfill the dividends. 
 */
public class BackfillDividendJob implements Job {

    private static final File file = new File("data/EOD_20180119_div_fixed.csv");
    private static final Logger log = LoggerFactory.getLogger(BackfillDividendJob.class);
    
    @Override
    public void startJob() {
        backfillDividends();
    }
    
    private void backfillDividends() {
        List<DividendItem> items = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {            
            String line;
            String lastSymbol = "";
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                if (!CommonUtil.isSymbolValid(symbol)) {
                    continue;
                }
                if (!lastSymbol.equals(symbol)) {
                    backfill(lastSymbol, items);
                    lastSymbol = symbol;
                    items = new ArrayList<>();
                }
                String date = CommonUtil.removeHyphen(data[1]); 
                double dividend = Double.parseDouble(data[7]);
                if (dividend > 0) {
                    DividendItem item = new DividendItem();
                    item.setSymbol(symbol);
                    item.setDate(date);
                    item.setAmount(dividend);
                    items.add(item);                    
                }
            }
            backfill(lastSymbol, items);
        }
        catch (Exception e) {
            log.error("Failed to backfill dividends: ", e);
        }        
    }
    
    private void backfill(String symbol, List<DividendItem> items) {
        if (symbol.length() == 0) {
            return;
        }
        log.info(String.format("Start backfilling %d items ...", items.size(), symbol));
        for (DividendItem item : items) {
            DynamoDBProvider.getInstance().getMapper().save(item);
        }
        log.info(String.format("Done backfilling %d items for %s.", items.size(), symbol));
    }
}
