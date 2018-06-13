package stock;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.joda.time.DateTime;

/**
 * Static class for computing indicators. 
 * Notice that:
 * 1. Unless specified otherwise, the first period - 1 data points will not have indicator value computed,
 * because we cannot do that (for obvious reasons).
 */
public class IndicatorHelper {
    /**
     * See the following definition for exponential moving average.
     * http://www.incrediblecharts.com/indicators/exponential_moving_average.php 
     * @param candles A list of 
     * @param period
     * @return An array that lists exponential moving average of each data point. 
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, Double> getExponentialMovingAverage(TreeMap<LocalDateTime, T> candles, int period) {
        TreeMap<LocalDateTime, Double> emaMap = new TreeMap<>();
        
        if (candles.size() < period) {
            return emaMap;
        }
        
        //Why is this 2 / (period + 1)? Because if you think about the last data in the array,
        //if it needs to have twice the weight as the weight for the other data, then the total weight
        //will be period + 1 and the weight for the last data is 2.
        double emaPercent = 2.0 / (period + 1);
        double oneMinusEmaPercent = 1 - emaPercent;
        double lastEma = 0.0;
        
        Iterator<Entry<LocalDateTime, T>> iterator = candles.entrySet().iterator();
        for (int i = 0; i < period; i++) {
            // Doesn't need to check if iterator has next because we already short circuit when there is not
            // enough data.
            Entry<LocalDateTime, T> entry = iterator.next();
            lastEma += entry.getValue().close;
            
            // Calculate the first EMA which is basically the SMA for the first period.
            if (i == period - 1) {
                lastEma /= period;
                emaMap.put(entry.getKey(), lastEma);                        
            }
        }
        
        while (iterator.hasNext()) {
            Entry<LocalDateTime, T> entry = iterator.next();
            double ema = entry.getValue().getClose() * emaPercent + lastEma * oneMinusEmaPercent;
            emaMap.put(entry.getKey(), ema);
            lastEma = ema;
        }
        
        return emaMap;
    }
}
