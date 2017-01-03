package stock;

public class StockEnum {
    
    public enum StockCandleDataType {
        OPEN, CLOSE, HIGH, LOW, VOLUME, DATE
    }
    
    public enum Exchange {
        NASDAQ,
        NYSE,
        AMEX,
        SSE,
        SZSE
    }
    
    public enum Country {
        US,
        CHINA
    }
    
    public enum StockCandleClass {
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
    
    public enum StockOrderType {
        BUY,        //Buy at market price
        SELL,       //Sell at market price
        BUY_LIMIT,  //Place a pending buy order below the market price and wait for triggering
        SELL_LIMIT,
        BUY_STOP,
        SELL_STOP
    }
    
    public enum StockOrderStatus {
        OPENED,
        PENDING,
        CLOSED
    }
}
