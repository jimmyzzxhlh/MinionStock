package dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class DynamoDBClient {
	
	private static DynamoDBClient instance;
	private static AmazonDynamoDBClient localClient;
	private static final String LOCAL_END_POINT = "https://localhost:8000";
	
	private DynamoDBClient() {
		
	}
	
	public static DynamoDBClient getInstance() {
		if (instance == null) {
			instance = new DynamoDBClient();
		}
		return instance;
	}
	
	public static AmazonDynamoDBClient getLocalClient() {
		if (localClient == null) {
			localClient = new AmazonDynamoDBClient()
					.withEndpoint(LOCAL_END_POINT);	
		}
		return localClient;
	}
}
