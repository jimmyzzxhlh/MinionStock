package aurora;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.Company;
import company.CompanyEnum.Exchange;

public class AuroraDAO implements AuroraDAOInterface {
	
	private static final Logger log = LoggerFactory.getLogger(AuroraDAO.class);

	@Override
	public Map<String, Company> getCompanies() {
		Map<String, Company> map = new HashMap<>();
		String query = String.format("SELECT * FROM %s", AuroraConst.getCompanyTable());
		ResultSet resultSet = AuroraQueryHelper.executeQuery(query);
		try {
			while (resultSet.next()) {
				String symbol = resultSet.getString(AuroraConst.COLUMN_SYMBOL);
				String sector = resultSet.getString(AuroraConst.COLUMN_SECTOR);
				String industry = resultSet.getString(AuroraConst.COLUMN_INDUSTRY);
				long shares = resultSet.getLong(AuroraConst.COLUMN_SHARES);
				Exchange exchange = Exchange.valueOf(resultSet.getString(AuroraConst.COLUMN_EXCHANGE));
				Company company = new Company()
					.withSymbol(symbol)
					.withSector(sector)
					.withIndustry(industry)
					.withShares(shares)
					.withExchange(exchange);
				map.put(symbol, company);
			}
		} catch (SQLException e) {
			log.error("SQL Exception when running query: " + query, e);			
		}
		return map;
	}

}
