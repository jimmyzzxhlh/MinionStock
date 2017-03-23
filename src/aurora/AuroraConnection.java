package aurora;

import java.sql.Connection;
import java.sql.DriverManager;

public class AuroraConnection {
	
	private static Connection connection;
	private static final String JDBC_URL = "jdbc:mysql://minion.cluster-cemz9y8rpyxp.us-west-2.rds.amazonaws.com:3306/symbol?"
			+ "user=zzxhlh&password=smyxayg9";
	
	private AuroraConnection() {
		
	}
	
	public static Connection getConnection() {
		if (connection == null) createConnection();
		return connection;
	}
	
	private static void createConnection() {
		try {
			connection = DriverManager.getConnection(JDBC_URL);
		}
		catch (Exception e) {
		
		}
	}
}
