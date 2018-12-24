package download.iex;

import util.CommonUtil;

public class IexSplitData {
  private String exDate;
  private long toFactor;
  private long forFactor;
  
  public String getExDate() {
    return exDate == null ? null : CommonUtil.removeHyphen(exDate); 
  }
  public void setExDate(String exDate) {
    this.exDate = exDate;
  }
  public long getToFactor() {
    return toFactor;
  }
  public void setToFactor(long toFactor) {
    this.toFactor = toFactor;
  }
  public long getForFactor() {
    return forFactor;
  }
  public void setForFactor(long forFactor) {
    this.forFactor = forFactor;
  }
  
  
}
