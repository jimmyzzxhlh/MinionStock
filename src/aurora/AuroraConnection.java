package aurora;

import java.sql.Connection;
import java.sql.DriverManager;

public class AuroraConnection {
	
	private static Connection connection;

	private AuroraConnection() {
		
	}
	
	public static Connection getConnection() {
		if (connection == null) createConnection();
		return connection;
	}
	
	private static void createConnection() {
		try {
			connection = DriverManager.getConnection(AuroraConst.JDBC_URL);
		}
		catch (Exception e) {
		
		}
	}
}
