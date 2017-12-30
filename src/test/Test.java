package test;

import java.io.BufferedReader;

import com.google.gson.Gson;

import download.DownloadHelper;
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
	    BufferedReader br = DownloadHelper.getBufferedReaderFromURL("https://api.iextrading.com/1.0/stock/camt/chart/1d");
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        Gson gson = new Gson();
        IntraDayData[] dataArray = gson.fromJson(sb.toString(), IntraDayData[].class);
        long volume = 0;
        for (IntraDayData data : dataArray) {
            volume += data.getVolume();  
        }
        System.out.println(volume);
	}
}
