package dynamodb;

import download.iex.IntraDayData;
import dynamodb.item.IntraDayItem;

public class DynamoDBHelper {
	
    private static DynamoDBHelper instance;
    private DynamoDBHelper() {}
    
    public static DynamoDBHelper getInstance() {   
        if (instance == null) {
            instance = new DynamoDBHelper();
        }
        return instance;
    }
    
    public IntraDayItem getIntraDayItem(String symbol, IntraDayData data) {
        if (data.getNumberOfTrades() <= 0) {
            throw new IllegalArgumentException("Number of trades cannot be 0, no reason to put the data into DynamoDB.");
        }
        IntraDayItem item = new IntraDayItem();
        item.setAverage(data.getAverage());
        item.setHigh(data.getHigh());
        item.setLow(data.getLow());
        item.setNumberOfTrades(data.getNumberOfTrades());
        item.setSymbol(symbol);
        item.setTime(data.getDate() + " " + data.getMinute());
        
        return item;
    }
}
