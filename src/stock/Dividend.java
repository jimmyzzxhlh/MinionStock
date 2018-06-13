package stock;

import dynamodb.item.DividendItem;

/**
 * Class for dividend.
 * 
 * Notice that we separate the implementation of the dividend item from the DynamoDB item {@link DividendItem}
 * even though they are pretty much the same thing.  
 */
public class Dividend {
    private String symbol;
    private String date;    
    private double amount;
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    @Override
    public String toString() {
        return String.format("symbol=%s, date=%s, amount=%s", symbol, date, amount);
    }
}
