package main.job.onetime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamodb.DynamoDBProvider;
import dynamodb.item.SplitItem;
import main.job.Job;
import util.CommonUtil;

public class BackfillSplitJob implements Job {

    private static final File file = new File("data/EOD_20180119.csv");
    private static final Logger log = LoggerFactory.getLogger(BackfillSplitJob.class);
    private static final int MAX_ITEMS = 25;
    
    @Override
    public void startJob() {
        backfillSplit();
    }

    private void backfillSplit() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<SplitItem> items = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                SplitItem item = getSplitItem(data);
                if (item.getFactor() == 1.0) {
                    continue;
                }
                log.info(String.format("Detect split for %s on %s with factor %f.", item.getSymbol(), item.getDate(), item.getFactor()));
                items.add(item);
                if (items.size() > MAX_ITEMS) {
                    log.info(String.format("Saving %d split items ...", MAX_ITEMS));
                    DynamoDBProvider.getInstance().getMapper().batchSave(items);
                    items = new ArrayList<>();
                }
            }
        }
        catch (Exception e) {
            log.error("Failed to backfill split: ", e);
        }
    }
    
    private SplitItem getSplitItem(String[] data) {
        SplitItem item = new SplitItem();
        item.setSymbol(data[0]);
        item.setDate(data[1]);
        item.setFactor(Double.parseDouble(data[8]));
        return item;
    }
}
