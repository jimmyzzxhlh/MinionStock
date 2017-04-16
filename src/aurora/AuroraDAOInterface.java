package aurora;

import java.util.Map;

import company.Company;

/**
 * Table creation query:
 * - company
 * CREATE TABLE company (symbol VARCHAR(10), sector VARCHAR(50), industry VARCHAR(100),
 * shares BIGINT, exchange VARCHAR(10));
 * @author zzxhlh
 *
 */
public interface AuroraDAOInterface {
	public Map<String, Company> getCompanies();
	public void updateCompanies(Map<String, Company> map);
	public boolean companyExists(String symbol);
}
