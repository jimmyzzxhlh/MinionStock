package dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBProvider {

    private static DynamoDBProvider instance;
    private AmazonDynamoDB ddb;
    private DynamoDBMapper mapper;

    private DynamoDBProvider() {
        this.ddb = AmazonDynamoDBClientBuilder.standard()
            .withRegion(DynamoDBConst.REGION)
            .withCredentials(new ProfileCredentialsProvider())
            .build();
        
        this.mapper = new DynamoDBMapper(ddb);
    }

    public static DynamoDBProvider getInstance() {
        if (instance == null) {
            instance = new DynamoDBProvider();
        }
        return instance;
    }
    
    public AmazonDynamoDB getDynamoDB() {
        return this.ddb;
    }    
    
    public DynamoDBMapper getMapper() {
        return mapper;
    }
}
