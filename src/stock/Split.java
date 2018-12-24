package stock;

import dynamodb.item.SplitItem;

/**
 * Class for Split
 * 
 * Notice that we separate the implementation of the dividend item from the DynamoDB item {@link SplitItem}
 * even though they are pretty much the same thing.  
 */
public class Split {
  private String symbol;
  private String date;
  private double factor;
  
  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }
  
  public String getDate() { return date; }
  public void setDate(String date) { this.date = date; }
  
  public double getFactor() { return factor; }
  public void setFactor(double factor) { this.factor = factor; }
  
  @Override
  public String toString() {
    return String.format("symbol=%s, date=%s, factor=%s", symbol, date, factor);
  }
}
