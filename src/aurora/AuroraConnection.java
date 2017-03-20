package aurora;

import com.mysql.jdbc.Connection;

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
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
		
		}
	}
}
