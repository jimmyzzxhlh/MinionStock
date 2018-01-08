package test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import download.DownloadHelper;
import download.iex.DailyData;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;

public class Test {
	public static void main(String[] args) throws Exception {
//	    testDynamoDB();
	    testIpxApi();
	}
	
	private static void testDynamoDB() throws Exception {
	    DynamoDBQueryExpression<DailyItem> queryExpression = new DynamoDBQueryExpression<DailyItem>()
	            .withLimit(1)
	            .withScanIndexForward(false)
	            .withHashKeyValues(new DailyItem("CAMT"));	            
	    List<DailyItem> result = DynamoDBProvider.getInstance().getMapper().queryPage(DailyItem.class, queryExpression).getResults();
	    for (DailyItem item : result) {
	        System.out.println(item);
	    }
	}
	
	private static void testIpxApi() throws Exception {
//	    String url = "https://api.iextrading.com/1.0/stock/thg/chart/2y";
	    String url = "https://api.iextrading.com/1.0/stock/market/batch?symbols=aapl,fb&types=chart&range=1m";
//	    BufferedReader br = DownloadHelper.getBufferedReaderFromURL(url);
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//        br.close();
	    String str = DownloadHelper.downloadURLToString(url);
        Gson g = new Gson();
//        List<DailyData> dataList =
//                Arrays.asList(g.fromJson(str, DailyData[].class));
        Type type = new TypeToken<Map<String, Map<String, DailyData[]>>>(){}.getType();
        Map<String, Map<String, DailyData[]>> dataMap = g.fromJson(str, type);
        System.out.println(dataMap);
	}
}
