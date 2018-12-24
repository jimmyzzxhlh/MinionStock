package download.iex;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class IexUrlBuilder {
  private enum DataType {
    CHART("chart"),
    STATS("stats"),
    DIVIDENDS("dividends");
    
    private String dataType;
    private DataType(String dataType) {
      this.dataType = dataType;
    }
    @Override
    public String toString() {
      return this.dataType;
    }
  }
  
  private enum TimeRange {
    FIVE_YEARS("5y"),
    TWO_YEARS("2y"),
    ONE_YEAR("1y"),
    YEAR_TO_DATE("ytd"),
    SIX_MONTHS("6m"),
    THREE_MONTHS("3m"),
    ONE_MONTH("1m"),
    ONE_DAY("1d"),
    INTRA_DAY("date"),
    DYNAMIC("dynamic");
    
    private String timeRange;
    private TimeRange(String timeRange) {
      this.timeRange = timeRange;
    }
    @Override
    public String toString() {
      return this.timeRange;
    }
  }
  
  private String symbol;   
  private DataType dataType;
  private TimeRange timeRange;
  private boolean batch;
  private List<String> symbols;  // Only work with batch mode.  
  
  public IexUrlBuilder withSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }
  
  public IexUrlBuilder withSymbols(List<String> symbols) {
    this.symbols = symbols;
    return this;
  }
  
  public IexUrlBuilder withBatch() {
    this.batch = true;
    return this;
  }
  
  public IexUrlBuilder withChart() {
    this.dataType = DataType.CHART;
    return this;
  }
  
  public IexUrlBuilder withStats() {
    this.dataType = DataType.STATS;
    return this;
  }
  
  public IexUrlBuilder withDividends() {
    this.dataType = DataType.DIVIDENDS;
    return this;
  }
  
  public IexUrlBuilder withOneDay() {
    this.timeRange = TimeRange.ONE_DAY;
    return this;
  }
  
  public IexUrlBuilder withOneMonth() {
    this.timeRange = TimeRange.ONE_MONTH;
    return this;
  }
  
  public IexUrlBuilder withTwoYears() {
    this.timeRange = TimeRange.TWO_YEARS;
    return this;
  }
  
  public IexUrlBuilder withFiveYears() {
    this.timeRange = TimeRange.FIVE_YEARS;
    return this;
  }
  
  public String build() {
    if (batch) {
      return buildBatch();
    }
    else {
      return buildSingle();
    }
  }
  
  private String buildBatch() {
    if (symbols == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder(IexConst.BASE_URL).append("market/batch?");
    sb.append("types=");
    switch (dataType) {
    case CHART:
    case DIVIDENDS:
      sb.append(dataType.toString());
      break;
    default:
      break;
    }
    sb.append("&symbols=")
      .append(StringUtils.join(symbols, ","))
      .append("&range=")
      .append(timeRange.toString());
    
    return sb.toString();    
  }
  
  private String buildSingle() {
    if (symbol == null) {
      return "";
    }
    
    StringBuilder sb = new StringBuilder(IexConst.BASE_URL);
    sb.append(symbol.toLowerCase())
      .append("/")
      .append(dataType.toString())
      .append("/");
    switch (dataType) {
    case CHART:
    case DIVIDENDS:
      sb.append(timeRange.toString());
      break;
    case STATS:
      break;      
    default:
      break;
    }
    return sb.toString();    
  }   
  
}