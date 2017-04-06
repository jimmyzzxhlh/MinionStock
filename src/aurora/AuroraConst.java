package aurora;

public class AuroraConst {
	public static final String ENDPOINT = "minion.cluster-cemz9y8rpyxp.us-west-2.rds.amazonaws.com";
	public static final int PORT = 3306;
	public static final String USERNAME = "zzxhlh";
	public static final String PASSWORD = "smyxayg9";
	public static final String DATABASE_SYMBOL = "symbol";
	
	public static final String JDBC_URL = "jdbc:mysql://" + ENDPOINT + ":" + PORT + "/" +  DATABASE_SYMBOL + "?"
			+ "user=" + USERNAME + "&password=" + PASSWORD;
	
}
