package stock;

/**
 * Class for describing the company of a stock
 * The attributes here should be pretty much static (i.e. not frequently changed by stock price movement). 
 * @author jimmyzzxhlh-Dell
 *
 */
public class Company {
    private String symbol;
    private long outstandingShares;
    
    public Company(String symbol, long outstandingShares) {
        this.symbol = symbol;
        this.outstandingShares = outstandingShares;
    }
    
    public Company(Company company) {
        this.symbol = company.symbol;
        this.outstandingShares = company.outstandingShares;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public long getOutstandingShares() {
        return this.outstandingShares;
    }
    
}
