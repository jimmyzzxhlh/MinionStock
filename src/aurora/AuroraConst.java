package aurora;

public class AuroraConst {
	public static final String ENDPOINT = "minion.cluster-cemz9y8rpyxp.us-west-2.rds.amazonaws.com";
	public static final int PORT = 3306;
	public static final String USERNAME = "zzxhlh";
	public static final String PASSWORD = "temp";
	public static final String DATABASE_SYMBOL = "stock";
	
	public static final String JDBC_URL = "jdbc:mysql://" + ENDPOINT + ":" + PORT + "/" +  DATABASE_SYMBOL + "?"
			+ "user=" + USERNAME + "&password=" + PASSWORD;
	
	//Table definition
	public static final String TABLE_COMPANY = "company";
	
	//Column definition
	public static final String COLUMN_SYMBOL = "symbol";
	public static final String COLUMN_SECTOR = "sector";
	public static final String COLUMN_INDUSTRY = "industry";
	public static final String COLUMN_SHARES = "shares";
	public static final String COLUMN_EXCHANGE = "exchange";
	
	public static String getCompanyTable() {
		return DATABASE_SYMBOL + "." + TABLE_COMPANY;
	}
	
	
}
