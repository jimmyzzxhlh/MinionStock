package download.iex;

import dynamodb.item.DividendItem;
import util.CommonUtil;

public class DividendData {
    private String exDate;
    private String paymentDate;
    private String recordDate;
    private String declaredDate;
    private Double amount;
    
    public String getExDate() {
        return exDate == null ? null : CommonUtil.removeHyphen(exDate); 
    }
    public void setExDate(String exDate) {
        this.exDate = exDate;
    }
    public String getPaymentDate() {
        return paymentDate == null ? null : CommonUtil.removeHyphen(paymentDate);
    }
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public String getRecordDate() {
        return recordDate == null ? null : CommonUtil.removeHyphen(recordDate);
    }
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
    public String getDeclaredDate() {
        return declaredDate == null ? null : CommonUtil.removeHyphen(declaredDate);
    }
    public void setDeclaredDate(String declaredDate) {
        this.declaredDate = declaredDate;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public DividendItem toDividendItem(String symbol) {
        DividendItem item = new DividendItem();
        item.setSymbol(symbol);
        item.setAmount(amount);
        item.setDeclaredDate(this.getDeclaredDate());
        item.setExDate(this.getExDate());
        item.setPaymentDate(this.getPaymentDate());
        item.setRecordDate(this.getRecordDate());
        
        return item;
    }
    
    @Override
    public String toString() {
        return String.format("declaredDate = %s, exDate = %s, recordDate = %s, paymentDate = %s, amount = %f",
            this.getDeclaredDate(),
            this.getExDate(),
            this.getRecordDate(),
            this.getPaymentDate(),
            amount);
    }
}
