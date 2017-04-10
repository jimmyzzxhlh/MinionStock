package company;

import util.CommonUtil;

import company.CompanyEnum.Exchange;
import company.CompanyEnum.Industry;
import company.CompanyEnum.Sector;

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
    private Exchange exchange;
    
    public Company() {}
    
    public Company(Company company) {
        this.symbol = company.symbol;
        this.sector = company.sector;
        this.industry = company.industry;
        this.shares = company.shares;
        this.exchange = company.exchange;
    }
    
    public Company(Exchange exchange, String line) {
    	String[] data = CommonUtil.splitCSVLine(line);
    	this.symbol = data[0];
    	this.sector = Sector.get(data[5]);
    	this.industry = Industry.get(data[6]);
    	this.exchange = exchange;
    }
    
    public Company withSymbol(String symbol) {
    	this.symbol = symbol;
    	return this;
    }
    
    public Company withSector(String sector) {
    	this.sector = Sector.get(sector);
    	return this; 
    }
    
    public Company withIndustry(String industry) {
    	this.industry = Industry.get(industry);
    	return this;
    }
    
    public Company withShares(long shares) {
    	this.shares = shares;
    	return this;
    }
    
    public Company withExchange(Exchange exchange) {
    	this.exchange = exchange;
    	return this;
    }
    
    public String getSymbol()     { return this.symbol; }
    public Sector getSector()     { return this.sector; }
    public Industry getIndustry() { return this.industry; }
    public long getShares()       { return this.shares; }
    public Exchange getExchange() { return this.exchange; }
        
}
