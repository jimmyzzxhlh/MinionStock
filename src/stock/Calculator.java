package stock;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

import enums.StockEnum.CandleDataType;

/**
 * Static class for computing indicators. 
 * Notice that:
 * 1. Unless specified otherwise, the first period - 1 data points will not have indicator value computed,
 * because we cannot do that (for obvious reasons).
 */
public class Calculator {
    /**
     * See the following definition for exponential moving average.
     * http://www.incrediblecharts.com/indicators/exponential_moving_average.php 
     * @param candles A map of candles with key as the local date time.
     * @param period 
     * @return A map of doubles that represent the EMA value on particular dates. Notice that the first
     * period-1 dates do not have EMA.  
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, Double> getExponentialMovingAverage(TreeMap<LocalDateTime, T> candles, int period) {
        TreeMap<LocalDateTime, Double> emaMap = new TreeMap<>();
        
        if (candles.size() < period || period <= 0) {
            return emaMap;
        }
        
        // Why is this 2 / (period + 1)? Because if you think about the last data in the array,
        // if it needs to have twice the weight as the weight for the other data, then the total weight
        // will be period + 1 and the weight for the last data is 2.
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
        
        // Calculate EMA for the rest of the data points.
        while (iterator.hasNext()) {
            Entry<LocalDateTime, T> entry = iterator.next();
            double ema = entry.getValue().getClose() * emaPercent + lastEma * oneMinusEmaPercent;
            emaMap.put(entry.getKey(), ema);
            lastEma = ema;
        }
        
        return emaMap;
    }
    
    /**
     * Given a map of candles and a period, calculate the maximum profit and the maximum loss after number of periods
     * for each candle. Notice that the last #period candles cannot be calculated.
     * 
     * If any calculated candle has close price equal to 0 then an IllegalArgumentException will be thrown.
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, ProfitAndLoss> getProfitAndLoss(TreeMap<LocalDateTime, T> candles, int period) {
        TreeMap<LocalDateTime, ProfitAndLoss> profitMap = new TreeMap<>();        
        if (candles.size() <= period) {
            return profitMap;
        }
        
        // Fill the queue with # period candles. 
        Queue<T> queue = new LinkedList<>();
        Iterator<Entry<LocalDateTime, T>> iterator = candles.entrySet().iterator();
        for (int i = 0; i < period; i++) {
            queue.offer(iterator.next().getValue());
        }
        
        // For each remaining candle, calculate the profit/loss for the first candle in the queue.
        while (iterator.hasNext()) {            
            T nextCandle = iterator.next().getValue();
            T firstCandle = queue.poll();
            queue.offer(nextCandle);
            
            double max = queue.stream().mapToDouble(wc -> wc.getHigh()).max().getAsDouble();
            double min = queue.stream().mapToDouble(wc -> wc.getLow()).min().getAsDouble();
            // Beware - This means that we are calculating profit and loss based on the close price of the current
            // weekly candle. However, we can only buy or sell stock on the next week's open price!
            // But it's OK -- doesn't affect our ability to predict the price movement.
            double lastClose = firstCandle.getClose();
            if (lastClose == 0) {
                throw new IllegalArgumentException("Candle has close price equal to 0: " + firstCandle.toString());
            }
            double profit = max / lastClose - 1.0;
            double loss = min / lastClose - 1.0;
            
            profitMap.put(firstCandle.getDateTime(), new ProfitAndLoss(profit, loss));
        }
        
        return profitMap;
    }   
    
    /**
     * For each candle, get the distance between open/high/low/close and the last candle's close price, in percentage.
     * If any close price is equal to 0 then an IllegalArgumentException will be thrown.
     * 
     * @param type Type of price for the current candle to compare with the last candle's close price.
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, Double> getPriceDist(TreeMap<LocalDateTime, T> candles, CandleDataType type) { 
        TreeMap<LocalDateTime, Double> distMap = new TreeMap<>();
        if (candles.size() <= 1) {
            return distMap;
        }
        Iterator<Entry<LocalDateTime, T>> iterator = candles.entrySet().iterator();
        T lastCandle = iterator.next().getValue();
        
        while (iterator.hasNext()) {
            if (lastCandle.getClose() == 0) {
                throw new IllegalArgumentException("Candle has close price equal to 0: " + lastCandle.toString());
            }
            
            Entry<LocalDateTime, T> currentEntry = iterator.next();
            T currentCandle = currentEntry.getValue();
            
            distMap.put(
                currentEntry.getKey(),
                (currentCandle.getStockPrice(type) - lastCandle.getClose()) / lastCandle.getClose());
            
            lastCandle = currentCandle;            
        }
        
        return distMap;        
    }
    
    /**
     * Given a map of candles and their EMAs, calculate the distance between the EMA and the closing price,
     * with respect to the EMA price.
     * 
     * If the EMA price is 0, throw an IllegalArgumentException.
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, Double> getEmaDistance(
        TreeMap<LocalDateTime, T> candles, TreeMap<LocalDateTime, Double> emaMap) {
        TreeMap<LocalDateTime, Double> emaDist = new TreeMap<>();
        for (Entry<LocalDateTime, Double> entry : emaMap.entrySet()) {
            LocalDateTime dateTime = entry.getKey();
            double close = candles.get(dateTime).getClose();
            double ema = entry.getValue();
            if (ema == 0) {
                throw new IllegalArgumentException(String.format("EMA on %s has value 0.", entry.getKey()));                
            }
            double dist = (close - ema) / ema;
            emaDist.put(dateTime, dist);
        }
        return emaDist;
    }
    
    /**
     * Given a map of EMAs, calculate the slope of the EMA (distance / previous EMA).
     * 
     * If the previous EMA is 0, throw an IllegalArgumentException.
     */
    public static TreeMap<LocalDateTime, Double> getEmaSlope(TreeMap<LocalDateTime, Double> ema) {
        TreeMap<LocalDateTime, Double> emaSlope = new TreeMap<>();
        if (ema.size() <= 1) {
            return emaSlope;
        }
        
        Iterator<Entry<LocalDateTime, Double>> iterator = ema.entrySet().iterator();
        Entry<LocalDateTime, Double> previousEntry = iterator.next();
        
        while (iterator.hasNext()) {
            if (previousEntry.getValue() == 0) {
                throw new IllegalArgumentException(String.format("EMA on %s has value 0.", previousEntry.getKey()));
            }
            
            Entry<LocalDateTime, Double> entry = iterator.next();
            LocalDateTime dateTime = entry.getKey();
            double currentEma = entry.getValue();
            double previousEma = previousEntry.getValue();
            emaSlope.put(dateTime, currentEma / previousEma - 1.0);
            previousEntry = entry;
        }
        
        return emaSlope;
    }
    
    /**
     * Given a map of candles, calculate the current volume / previous X weeks average volume (excluding current week).
     * 
     * If the previous X weeks average volume is 0, then ignore the date time and do not put a value into the returned map.
     * Why don't we throw an exception? Because unlike the price, zero volume is valid.
     */
    public static <T extends AbstractCandle> TreeMap<LocalDateTime, Double> getRelativeVolume(
        TreeMap<LocalDateTime, T> candles, int period) { 
        TreeMap<LocalDateTime, Double> relativeVolumeMap = new TreeMap<>();
        if (candles.size() <= period) {
            return relativeVolumeMap;
        }
        
        Queue<T> queue = new LinkedList<>();
        Iterator<Entry<LocalDateTime, T>> iterator = candles.entrySet().iterator();
        // Fill the queue and calculate the sum of the volume.
        long pastVolumeSum = 0;        
        for (int i = 0; i < period; i++) {
            T candle = iterator.next().getValue();
            pastVolumeSum += candle.getVolume();
            queue.offer(candle);
        }
        
        // For each of the remaining candles, calculate the value and update the sum of volume.
        while (iterator.hasNext()) {
            Entry<LocalDateTime, T> entry = iterator.next();
            LocalDateTime dateTime = entry.getKey();
            T candle = entry.getValue();
            long currentVolume = candle.getVolume();
            
            // Only add an entry if we are able to calculate the relative volume.
            if (pastVolumeSum > 0) {
                relativeVolumeMap.put(dateTime, currentVolume / (pastVolumeSum * 1.0 / period));
            }
            
            pastVolumeSum = pastVolumeSum - queue.poll().getVolume() + currentVolume;
            queue.offer(candle);
        }
        
        return relativeVolumeMap;
    }
}
