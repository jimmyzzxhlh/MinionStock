package download.iex;

public class IexUrlBuilder {
    private static final String BASE_URL = "https://api.iextrading.com/1.0/stock/";
    
    private enum DataType {
        CHART("chart"),
        STATS("stats");
        
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
    
    public IexUrlBuilder withSymbol(String symbol) {
        this.symbol = symbol;
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
    
    public IexUrlBuilder withOneDay() {
        this.timeRange = TimeRange.ONE_DAY;
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
        if (symbol == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(BASE_URL);        
        switch (dataType) {
        case CHART:
            sb.append(symbol.toLowerCase())
              .append("/")
              .append(dataType.toString())
              .append("/")
              .append(timeRange.toString());
            break;
        case STATS:
            sb.append(symbol.toLowerCase())
              .append("/stats");              
        default:
            break;
        }
        return sb.toString();        
    }
    
}