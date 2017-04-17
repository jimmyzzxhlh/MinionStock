package aurora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CommonUtil;

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

	/**
	 * Insert a record if it does not exist. If it exists, update the record.
	 * e.g.
	 * INSERT INTO company (symbol, sector, industry, shares, exchange, last_updated)
	   VALUES ('camt', 'a', 'b', 1, 'c', now())
	   ON DUPLICATE KEY UPDATE sector = VALUES(sector),
	                           industry = VALUES(industry),
	                           shares = VALUES(shares),
	                           exchange = VALUES(exchange),
	                           last_updated = VALUES(last_updated)
	 * @param table
	 * @param columns
	 * @param values
	 */
	public static void insertOrUpdateRecord(String table, List<String> columns, List<String> values) {
		String columnStr = CommonUtil.getDelimitedString(columns, ",");
		String valueStr = CommonUtil.getDelimitedString(values, ",");
		String updateStr = columns.stream()
				                  .map(s -> (s + " = VALUES(" + s + ")"))
				                  .collect(Collectors.joining(", "));
		String query = String.format("INSERT INTO %s (%s) VALUES (%s) "
								   + "ON DUPLICATE KEY UPDATE %s", table, columnStr, valueStr, updateStr);
		executeQuery(query);		
	}
	

}
