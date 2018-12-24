package enums;

public class StockEnum {
  
  public enum CandleDataType {
    OPEN, CLOSE, HIGH, LOW, VOLUME, DATE
  }
  
  public enum Country {
    US,
    CHINA
  }
  
  public enum CandleClass {
    WHITE_LONG,
    BLACK_LONG,
    UPPER_LONGER,
    LOWER_LONGER    
  }
  
  public enum EarningsTimeType {
    AMC,   //After market close
    BTO,   //Before the open
    DMT,   //During market time
    NONE    
  }
  

}
