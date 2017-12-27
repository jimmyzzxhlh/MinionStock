package dynamodb;

public class DynamoDBHelper {
	
    private static DynamoDBHelper instance;
    private DynamoDBHelper() {}
    
    public static DynamoDBHelper getInstance() {   
        if (instance == null) {
            instance = new DynamoDBHelper();
        }
        return instance;
    }
}
