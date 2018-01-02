package dynamodb;

import com.amazonaws.regions.Regions;

public class DynamoDBConst {
	public static final Regions REGION = Regions.US_WEST_2;
	public static final String TABLE_COMPANY = "Company";
	public static final String TABLE_COMPANY_STATS = "CompanyStats";
	public static final String TABLE_INTRA_DAY = "IntraDay";
	public static final String TABLE_DAILY = "Daily";
	public static final String TABLE_STATUS = "Status";
	public static final long WRITE_CAPACITY_DEFAULT = 1;	
}
