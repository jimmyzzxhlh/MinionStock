package aurora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuroraQueryHelper {
	
	private static final Logger log = LoggerFactory.getLogger(AuroraQueryHelper.class);
			
	public static ResultSet executeQuery(String query) {
		try {
			Statement statement = AuroraConnection.getConnection().createStatement();
			return statement.executeQuery(query);			
		}
		catch (SQLException e) {
			log.error("SQL Exception for query: " + query, e);
			return null;
		}
	}
}
