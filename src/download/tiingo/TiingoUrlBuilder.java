package download.tiingo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TiingoUrlBuilder {
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
  private String symbol;   
  private LocalDate startDate;
  private LocalDate endDate;
  
  public TiingoUrlBuilder withSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }
  
  public TiingoUrlBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }
  
  public TiingoUrlBuilder withEndDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }
  
  public String build() {
    if (symbol == null) {
      return "";
    }
    
    StringBuilder sb = new StringBuilder(TiingoConst.BASE_URL);
    sb.append(symbol.toLowerCase())
      .append("/prices?")
      .append("startDate=")
      .append(startDate.format(dateFormatter))
      .append("&")
      .append("endDate=")
      .append(endDate.format(dateFormatter));
    
    return sb.toString();    
  }   
  
}