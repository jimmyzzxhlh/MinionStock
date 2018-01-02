package test;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import download.DownloadHelper;
import download.iex.DailyData;
import download.iex.IntraDayData;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;

public class Test {
	public static void main(String[] args) throws Exception {
	    testIpxApi();
	}
	
	private static void testDynamoDB() throws Exception {
	    DailyItem item = new DailyItem();
	    item.setSymbol("CAMT");
	    item.setDate("2017-12-12");
	    item.setOpen(1.0);
	    item.setClose(2.0);
	    DynamoDBProvider.getInstance().getMapper().save(item);
	}
	
	private static void testIpxApi() throws Exception {
	    String url = "https://api.iextrading.com/1.0/stock/thg/chart/2y";
//	    BufferedReader br = DownloadHelper.getBufferedReaderFromURL(url);
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//        br.close();
	    String str = DownloadHelper.downloadURLToString(url);
        Gson g = new Gson();
        List<DailyData> dataList =
                Arrays.asList(g.fromJson(str, DailyData[].class));        
	}
}
