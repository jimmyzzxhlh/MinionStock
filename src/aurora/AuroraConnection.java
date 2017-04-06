package aurora;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuroraConnection {
	
	private static final Logger log = LoggerFactory.getLogger(AuroraConnection.class);
	
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
			log.error("Cannot create Aurora connection.");
		}
	}	
}
