package company;

import util.CommonUtil;

import company.CompanyEnum.Exchange;
import company.CompanyEnum.Industry;
import company.CompanyEnum.Sector;
import dynamodb.item.CompanyItem;

/**
 * Class for describing the company of a stock
 * The attributes here should be pretty much static (i.e. not frequently changed by stock price movement). 
 */
public class Company {
    private String symbol;
    private Sector sector;
    private Industry industry;
    private Exchange exchange;
    
    public Company() {}
    
    public Company(Company company) {
        this.symbol = company.symbol;
        this.sector = company.sector;
        this.industry = company.industry;
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
    
    public Company withExchange(String exchange) {
        this.exchange = Exchange.get(exchange);
        return this;
    }
    
    public Company withExchange(Exchange exchange) {
    	this.exchange = exchange;
    	return this;
    }
    
    public String getSymbol()     { return this.symbol; }
    public Sector getSector()     { return this.sector; }
    public Industry getIndustry() { return this.industry; }
    public Exchange getExchange() { return this.exchange; }
    
    public CompanyItem toCompanyItem() {
        CompanyItem item = new CompanyItem();
        item.setSymbol(symbol);
//        item.setExchange(exchange.toString());
//        item.setIndustry(industry.toString());
//        item.setSector(sector.toString());
        
        return item;
    }
    
    @Override
    public String toString() {
        return String.format("symbol = %s, exchange = %s, industry = %s, sector = %s",
            symbol,
            exchange == null ? "null" : exchange.toString(),
            industry == null ? "null" : industry.toString(),
            sector == null ? "null" : sector.toString()
        );
    }
}
