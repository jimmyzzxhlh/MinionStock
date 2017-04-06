package test;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aurora.AuroraQueryHelper;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	public static void main(String[] args) {
		ResultSet resultSet = AuroraQueryHelper.executeQuery("show databases");
		try {
			while (resultSet.next()) {
				System.out.println(resultSet.getString("Database"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
