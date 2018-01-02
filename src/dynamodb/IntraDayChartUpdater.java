package dynamodb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import download.DownloadHelper;
import download.iex.IexUrlBuilder;
import download.iex.IntraDayData;
import dynamodb.item.IntraDayItem;
import util.CommonUtil;

public class IntraDayChartUpdater implements ChartUpdater {
    private static final Logger log = LoggerFactory.getLogger(IntraDayChartUpdater.class);
    private static final int HOUR = 17;
    private static final int MINUTE = 0;
    
    public void startJob() {
        downloadChart();
        CommonUtil.scheduleDailyJob(() -> downloadChart(), HOUR, MINUTE);
    }
    
    private void downloadChart() {
        log.info("Start downloading intra day chart ...");
        Gson g = new Gson();
        for (String symbol : DownloadHelper.downloadCompanies().keySet()) {
            log.info("Getting intra day chart for " + symbol + " ...");
            String url = new IexUrlBuilder()
                .withChart()
                .withSymbol(symbol)
                .withOneDay()
                .build();
            String str = DownloadHelper.downloadURLToString(url);
            if (str.length() == 0) {
                log.warn("Nothing downloaded for " + symbol + ". Skipping ...");
                continue;
            }
            List<IntraDayData> dataList =
                Arrays.asList(g.fromJson(str, IntraDayData[].class))
                      .stream()
                      .filter(data -> data.getNumberOfTrades() > 0)
                      .collect(Collectors.toList());
            
            log.info("Start saving " + dataList.size() + " items ...");
            for (IntraDayData data : dataList) {
                IntraDayItem item = data.toIntraDayItem(symbol);                
                DynamoDBProvider.getInstance().getMapper().save(item);
            }
            log.info("Done saving " + dataList.size() + " items.");            
        }
        log.info("Done downloading intra day chart. Next update will be at " + CommonUtil.getTime(HOUR, MINUTE) + ".");        
    }
}
