package stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import util.CommonUtil;

/**
 * Class for a daily candle.
 */
public class DailyCandle extends AbstractCandle implements Comparable<DailyCandle> {
    public LocalDate getDate() {        
        if (this.getDateTime() == null) {
            return null;
        }
        return this.getDateTime().toLocalDate();
    }
    
	public String getDateString() {
	    LocalDate date = getDate();
	    return date == null ? "null" : CommonUtil.formatDate(date);
	}
	
	public DailyCandle withDate(LocalDate date) {	    
	    this.dt = LocalDateTime.of(date, LocalTime.of(0, 0));	    
	    return this;
	}
	
	public DailyCandle withDate(String dateString) {
	    return withDate(CommonUtil.parseDate(CommonUtil.removeHyphen(dateString)));	    
	}

    @Override
    public int compareTo(DailyCandle o) {        
        if (this.dt == null || o.dt == null) {
            throw new RuntimeException(
                "Cannot compare daily candle because one of the dates is null. this: " + this.toString() + "; that: " + o.toString());            
        }
        return this.getDate().compareTo(o.getDate());
    }
    
    @Override
    public String toString() {
        return String.format("date=%s, %s" + getDateString(), super.toString());
    }
}
