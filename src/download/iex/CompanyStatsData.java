package download.iex;

import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;

import dynamodb.item.CompanyStatsItem;
import util.CommonUtil;

public class CompanyStatsData {
    private long shortInterest;
    private double shortRatio;
    private double dividendRate;
    private long sharesOutstanding;
    @SerializedName("float") private long sharesFloating;
    public long getShortInterest() {
        return shortInterest;
    }
    public void setShortInterest(long shortInterest) {
        this.shortInterest = shortInterest;
    }
    public double getShortRatio() {
        return shortRatio;
    }
    public void setShortRatio(double shortRatio) {
        this.shortRatio = shortRatio;
    }
    public double getDividendRate() {
        return dividendRate;
    }
    public void setDividendRate(double dividendRate) {
        this.dividendRate = dividendRate;
    }
    public long getSharesOutstanding() {
        return sharesOutstanding;
    }
    public void setSharesOutstanding(long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }
    public long getSharesFloating() {
        return sharesFloating;
    }
    public void setSharesFloating(long sharesFloating) {
        this.sharesFloating = sharesFloating;
    }
    
    public CompanyStatsItem getCompanyStatsItem(String symbol, LocalDate date) {
        CompanyStatsItem item = new CompanyStatsItem();
        item.setSymbol(symbol);
        item.setDate(CommonUtil.getDate(date));        
        item.setDividendRate(dividendRate);
        item.setSharesFloating(sharesFloating);
        item.setSharesOutstanding(sharesOutstanding);
        item.setShortInterest(shortInterest);
        item.setShortRatio(shortRatio);
        
        return item;
    }
}
