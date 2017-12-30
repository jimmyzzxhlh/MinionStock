package dynamodb;

import com.google.gson.Gson;

import company.CompanyManager;
import download.DownloadHelper;
import download.iex.IexUrlBuilder;
import download.iex.IntraDayData;
import dynamodb.item.IntraDayItem;

public class IntraDayChartUpdater implements ChartUpdater {
    public void startJob() {
        Gson g = new Gson();
        for (String symbol : CompanyManager.getInstance().getCompaniesMap().keySet()) {
            String url = new IexUrlBuilder()
                .withChart()
                .withSymbol(symbol)
                .withOneDay()
                .build();
            String str = DownloadHelper.downloadURLToString(url);
            IntraDayData[] dataArray = g.fromJson(str, IntraDayData[].class);
            for (IntraDayData data : dataArray) {
                if (data.getNumberOfTrades() <= 0) {
                    continue;
                }
                IntraDayItem item = DynamoDBHelper.getInstance().getIntraDayItem(symbol, data);
                DynamoDBProvider.getInstance().getMapper().save(item);
            }
            break;
        }
    }
}
