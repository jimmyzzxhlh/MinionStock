package enums;

public class OrderEnum {
    public enum OrderType {
        BUY,        //Buy at market price
        SELL,       //Sell at market price
        BUY_LIMIT,  //Place a pending buy order below the market price and wait for triggering
        SELL_LIMIT,
        BUY_STOP,
        SELL_STOP
    }
    
    public enum OrderStatus {
        OPENED,
        PENDING,
        CLOSED
    }
}
