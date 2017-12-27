package dynamodb;

import com.google.gson.Gson;

import company.CompanyManager;
import download.DownloadHelper;
import download.iex.IEXUrlBuilder;

public class DailyChartUpdater {
    
    public static void startJob() {
//        ScheduledExecutorService executorService = 
//            Executors.newScheduledThreadPool(1);
//        executorService.scheduleAtFixedRate(() -> init(), 0, 24, TimeUnit.HOURS);
        System.out.println("Backfilling old data...");
        backfillOldData();
    }
    
    /**
     * This assumes that the DynamoDb table is empty. Therefore this should only be run once,
     * unless there is something going wrong with the DynamoDB...hopefully not.
     */
    public static void backfillOldData() {
        Gson g = new Gson();
        for (String symbol : CompanyManager.getInstance().getCompaniesMap().keySet()) {
            String url = new IEXUrlBuilder()
                .withSymbol(symbol)
                .withChart()
                .withTwoYears()
                .build();
            System.out.println("Downloading " + url);
            String data = DownloadHelper.downloadURLToString(url);            
            System.out.println(data);
            break;
        }
        
    }    
}
