package aurora;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

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
	
	public static boolean recordExists(String table, String whereClause) {
		String query = String.format("SELECT 1 FROM %s WHERE %s LIMIT 1", table, whereClause);
		ResultSet resultSet = executeQuery(query);
		try {
			return resultSet.getInt("1") > 0;
		}
		catch (SQLException e) {
			log.error("An exception is thrown when checking record exists. Query: " + query, e);
			return false;
		}
	}
}
