package company;

import company.CompanyEnum.Industry;
import company.CompanyEnum.Sector;
import util.CommonUtil;

/**
 * Class for describing the company of a stock
 * The attributes here should be pretty much static (i.e. not frequently changed by stock price movement). 
 * @author jimmyzzxhlh-Dell
 *
 */
public class Company {
    private String symbol;
    private Sector sector;
    private Industry industry;
    private long shares;
    
//    public Company(String symbol, long shares) {
//        this.symbol = symbol;
//        this.shares = shares;
//    }
//    
    public Company() {}
    
    public Company(Company company) {
        this.symbol = company.symbol;
        this.sector = company.sector;
        this.industry = company.industry;
        this.shares = company.shares;
    }
    
    public Company(String line) {
    	String[] data = CommonUtil.splitCSVLine(line);
    	symbol = data[0];
    	sector = Sector.get(data[5]);
    	industry = Industry.get(data[6]);
    }
    
    public String getSymbol()     { return this.symbol; }
    public Sector getSector()     { return this.sector; }
    public Industry getIndustry() { return this.industry; }
    public long getShares()       { return this.shares; }
        
}
