package test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import download.DownloadHelper;
import download.iex.DailyData;
import download.iex.DividendData;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;
import util.gson.DoubleTypeAdapter;

public class Test {
	public static void main(String[] args) throws Exception {
//	    testDynamoDB();
//	    testIpxApi();
	    testGson();
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
	
	private static void testGson() throws Exception {
	    String json = "{\"A\":{\"dividends\":[{\"exDate\":\"2017-12-29\",\"paymentDate\":\"2018-01-24\",\"recordDate\":\"2018-01-02\",\"declaredDate\":\"2017-11-15\",\"amount\":0.149,\"flag\":\"FI\",\"type\":\"Dividend income\",\"qualified\":\"\",\"indicated\":\"\"},{\"exDate\":\"2017-10-02\",\"paymentDate\":\"2017-10-25\",\"recordDate\":\"2017-10-03\",\"declaredDate\":\"2017-09-20\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"\",\"indicated\":\"\"},{\"exDate\":\"2017-06-29\",\"paymentDate\":\"2017-07-26\",\"recordDate\":\"2017-07-03\",\"declaredDate\":\"2017-05-17\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2017-03-31\",\"paymentDate\":\"2017-04-26\",\"recordDate\":\"2017-04-04\",\"declaredDate\":\"2017-03-15\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2016-12-29\",\"paymentDate\":\"2017-01-25\",\"recordDate\":\"2017-01-03\",\"declaredDate\":\"2016-11-17\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2016-09-30\",\"paymentDate\":\"2016-10-26\",\"recordDate\":\"2016-10-04\",\"declaredDate\":\"2016-09-21\",\"amount\":0.115,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2016-06-30\",\"paymentDate\":\"2016-07-27\",\"recordDate\":\"2016-07-05\",\"declaredDate\":\"2016-05-18\",\"amount\":0.115,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2016-04-01\",\"paymentDate\":\"2016-04-27\",\"recordDate\":\"2016-04-05\",\"declaredDate\":\"2016-03-16\",\"amount\":0.115,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2015-12-31\",\"paymentDate\":\"2016-01-27\",\"recordDate\":\"2016-01-05\",\"declaredDate\":\"2015-11-19\",\"amount\":0.115,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2015-09-25\",\"paymentDate\":\"2015-10-21\",\"recordDate\":\"2015-09-29\",\"declaredDate\":\"2015-09-16\",\"amount\":0.1,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2015-06-26\",\"paymentDate\":\"2015-07-22\",\"recordDate\":\"2015-06-30\",\"declaredDate\":\"2015-05-20\",\"amount\":0.1,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2015-03-27\",\"paymentDate\":\"2015-04-22\",\"recordDate\":\"2015-03-31\",\"declaredDate\":\"2015-03-18\",\"amount\":0.1,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2015-01-02\",\"paymentDate\":\"2015-01-28\",\"recordDate\":\"2015-01-06\",\"declaredDate\":\"2014-11-20\",\"amount\":0.1,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2014-11-03\",\"paymentDate\":\"\",\"recordDate\":\"\",\"declaredDate\":\"\",\"amount\":15.75,\"flag\":\"\",\"type\":\"Stock dividend\",\"qualified\":\"\",\"indicated\":\"\"},{\"exDate\":\"2014-09-26\",\"paymentDate\":\"2014-10-22\",\"recordDate\":\"2014-09-30\",\"declaredDate\":\"2014-09-17\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2014-06-27\",\"paymentDate\":\"2014-07-23\",\"recordDate\":\"2014-07-01\",\"declaredDate\":\"2014-05-22\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2014-04-09\",\"paymentDate\":\"2014-04-23\",\"recordDate\":\"2014-04-11\",\"declaredDate\":\"2014-04-01\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2013-12-27\",\"paymentDate\":\"2014-01-22\",\"recordDate\":\"2013-12-31\",\"declaredDate\":\"2013-11-22\",\"amount\":0.132,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2013-09-27\",\"paymentDate\":\"2013-10-23\",\"recordDate\":\"2013-10-01\",\"declaredDate\":\"2013-09-18\",\"amount\":0.12,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2013-06-28\",\"paymentDate\":\"2013-07-24\",\"recordDate\":\"2013-07-02\",\"declaredDate\":\"2013-05-23\",\"amount\":0.12,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2013-03-28\",\"paymentDate\":\"2013-04-24\",\"recordDate\":\"2013-04-02\",\"declaredDate\":\"2013-01-17\",\"amount\":0.12,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"}]},\"AA\":{\"dividends\":[{\"exDate\":\"2016-11-08\",\"paymentDate\":\"2016-11-25\",\"recordDate\":\"2016-11-11\",\"declaredDate\":\"2016-09-29\",\"amount\":0.09,\"flag\":\"\",\"type\":\"Dividend income\",\"qualified\":\"Q\",\"indicated\":\"\"},{\"exDate\":\"2016-11-01\",\"paymentDate\":\"2016-11-01\",\"recordDate\":\"2016-10-20\",\"declaredDate\":\"2016-10-13\",\"amount\":\"\",\"flag\":\"\",\"type\":\"Stock dividend\",\"qualified\":\"\",\"indicated\":\"\"}]}}";
//	    Gson g = new Gson();
	    Gson g = new GsonBuilder()
            .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
            .setLenient().create();
	    Type type = new TypeToken<Map<String, Map<String, DividendData[]>>>(){}.getType();
	    Map<String, Map<String, DividendData[]>> dataMap = g.fromJson(json, type);
	    for (DividendData dividend : dataMap.get("A").get("dividends")) {
	        System.out.println(dividend);
	    }
	    
	    
	    
	}
}
