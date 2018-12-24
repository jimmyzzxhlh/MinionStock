package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import stock.DailyCandle;
import stock.WeeklyCandle;
import util.CommonUtil;

public class SpyAnalysis {
  private static String INPUT_FILE_NAME = "data/spy.csv";
  private static String ADJUSTED_PRICE_FILE_NAME = "data/spy_adjusted.csv";
  private static String ADJUSTED_PRICE_WEEKLY_FILE_NAME = "data/spy_adjusted_weekly.csv";
  private static String DIVIDEND_FILE_NAME = "data/spy_dividend.csv";
  private static String VOLATILITY_INTRAWEEK_FILE_NAME = "data/spy_volatility_intraweek.csv";
  private static String VOLATILITY_CROSSWEEK_FILE_NAME = "data/spy_volatility_crossweek.csv";

  public static void main(String[] args) {
    writeVolatilityCrossWeekFile();
  }

  private static void writeWeeklyCandleFile() {
    TreeMap<String, DailyCandle> dailyCandlesMap = getSpyAdjustedDailyCandles();

    TreeMap<String, WeeklyCandle> weeklyCandlesMap = new TreeMap<>();
    WeeklyCandle currentWeeklyCandle = null;
    for (Map.Entry<String, DailyCandle> entry : dailyCandlesMap.entrySet()) {
      DailyCandle dailyCandle = entry.getValue();
      if (currentWeeklyCandle == null) {
        currentWeeklyCandle = new WeeklyCandle(dailyCandle.getDate());
      } else if (!currentWeeklyCandle.isSameWeek(dailyCandle)) {
        weeklyCandlesMap.put(CommonUtil.formatDate(currentWeeklyCandle.getDateTime().toLocalDate()),
            currentWeeklyCandle);
        currentWeeklyCandle = new WeeklyCandle(dailyCandle.getDate());
      }
      currentWeeklyCandle.addDailyCandle(dailyCandle);
    }

    weeklyCandlesMap.put(CommonUtil.formatDate(currentWeeklyCandle.getDateTime().toLocalDate()), currentWeeklyCandle);

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(ADJUSTED_PRICE_WEEKLY_FILE_NAME)))) {
      for (Map.Entry<String, WeeklyCandle> entry : weeklyCandlesMap.entrySet()) {
        WeeklyCandle candle = entry.getValue();
        StringBuilder sb = new StringBuilder();
        sb.append("SPY,");
        sb.append(entry.getKey());
        sb.append(",");
        sb.append(candle.getOpen());
        sb.append(",");
        sb.append(candle.getHigh());
        sb.append(",");
        sb.append(candle.getLow());
        sb.append(",");
        sb.append(candle.getClose());
        sb.append(",");
        sb.append(candle.getVolume());
        bw.write(sb.toString());
        bw.newLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void writeVolatilityIntraWeekFile() {
    TreeMap<String, WeeklyCandle> candlesMap = getSpyWeeklyCandles();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(VOLATILITY_INTRAWEEK_FILE_NAME)))) {
      bw.write("VolatilityIntraWeek");
      bw.newLine();
      for (WeeklyCandle candle : candlesMap.values()) {
        double volatility = Math.max(candle.getHigh() - candle.getOpen(), candle.getOpen() - candle.getClose()) / candle.getOpen() * 100;
        bw.write(String.valueOf(volatility));
        bw.newLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private static void writeVolatilityCrossWeekFile() {
    TreeMap<String, WeeklyCandle> candlesMap = getSpyWeeklyCandles();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(VOLATILITY_CROSSWEEK_FILE_NAME)))) {
      bw.write("VolatilityCrossWeek");
      bw.newLine();
      WeeklyCandle previousCandle = null;
      for (WeeklyCandle candle : candlesMap.values()) {
        if (previousCandle == null) {
          previousCandle = candle;
          continue;
        }
        double volatility = Math.max(
            Math.abs(candle.getHigh() - previousCandle.getClose()),
            Math.abs(candle.getLow() - previousCandle.getClose())) / previousCandle.getClose() * 100;
        bw.write(String.valueOf(volatility));
        bw.newLine();
        previousCandle = candle;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static TreeMap<String, DailyCandle> getSpyAdjustedDailyCandles() {
    return getSpyDailyCandles(ADJUSTED_PRICE_FILE_NAME, false);
  }

  private static void writeAdjustedPriceFile() {
    Map<String, Double> dividendMap = getSpyDividend();
    // Need to use reverse order in order to calculate adjusted price correctly.
    TreeMap<String, DailyCandle> candlesMap = getSpyDailyCandles(INPUT_FILE_NAME, true);
    TreeMap<String, DailyCandle> outputCandlesMap = calculateAdjustedPrice(candlesMap, dividendMap);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(ADJUSTED_PRICE_FILE_NAME)))) {
      for (Map.Entry<String, DailyCandle> entry : outputCandlesMap.entrySet()) {
        DailyCandle candle = entry.getValue();
        StringBuilder sb = new StringBuilder();
        sb.append("SPY,");
        sb.append(candle.getDateString());
        sb.append(",");
        sb.append(candle.getOpen());
        sb.append(",");
        sb.append(candle.getHigh());
        sb.append(",");
        sb.append(candle.getLow());
        sb.append(",");
        sb.append(candle.getClose());
        sb.append(",");
        sb.append(candle.getVolume());
        bw.write(sb.toString());
        bw.newLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Map<String, Double> getSpyDividend() {
    Map<String, Double> dividendMap = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(new File(DIVIDEND_FILE_NAME)))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = CommonUtil.splitCSVLine(line);
        String date = CommonUtil.removeHyphen(data[1]);
        double dividend = Double.valueOf(data[2]);
        dividendMap.put(date, dividend);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return dividendMap;
  }

  private static TreeMap<String, DailyCandle> getSpyDailyCandles(String inputFileName, boolean isReverseOrder) {
    TreeMap<String, DailyCandle> candlesMap;
    if (isReverseOrder) {
      candlesMap = new TreeMap<>(Collections.reverseOrder());
    } else {
      candlesMap = new TreeMap<>();
    }
    try (BufferedReader br = new BufferedReader(new FileReader(new File(inputFileName)))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = CommonUtil.splitCSVLine(line);
        String date = CommonUtil.removeHyphen(data[1]);
        double open = Double.valueOf(data[2]);
        double high = Double.valueOf(data[3]);
        double low = Double.valueOf(data[4]);
        double close = Double.valueOf(data[5]);
        long volume = Double.valueOf(data[6]).longValue();
        DailyCandle dailyCandle = new DailyCandle().withDate(date).withOpen(open).withHigh(high).withLow(low)
            .withClose(close).withVolume(volume);
        candlesMap.put(date, dailyCandle);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return candlesMap;
  }

  private static TreeMap<String, WeeklyCandle> getSpyWeeklyCandles() {
	  TreeMap<String, WeeklyCandle> candlesMap = new TreeMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(new File(ADJUSTED_PRICE_WEEKLY_FILE_NAME)))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = CommonUtil.splitCSVLine(line);
        String date = CommonUtil.removeHyphen(data[1]);
        double open = Double.valueOf(data[2]);
        double high = Double.valueOf(data[3]);
        double low = Double.valueOf(data[4]);
        double close = Double.valueOf(data[5]);
        long volume = Double.valueOf(data[6]).longValue();
        WeeklyCandle weeklyCandle = new WeeklyCandle(date)
          .withOpen(open)
          .withHigh(high)
          .withLow(low)
          .withClose(close)
          .withVolume(volume);
        candlesMap.put(date, weeklyCandle);
      }      
    }
    catch (Exception e) {
      e.printStackTrace();      
    }
    return candlesMap;
	}

  private static TreeMap<String, DailyCandle> calculateAdjustedPrice(TreeMap<String, DailyCandle> inputCandles,
      Map<String, Double> dividendMap) {
    // The output is in the normal order so that it can be written to the
    // file.
    TreeMap<String, DailyCandle> outputCandlesMap = new TreeMap<>();

    double priceFactor = 1.0;
    double volumeFactor = 1.0;

    // Backward iteration
    for (Map.Entry<String, DailyCandle> entry : inputCandles.entrySet()) {
      String date = entry.getKey();
      DailyCandle inputCandle = entry.getValue();

      DailyCandle adjustedCandle = new DailyCandle().withDate(date).withOpen(inputCandle.getOpen() * priceFactor)
          .withHigh(inputCandle.getHigh() * priceFactor).withLow(inputCandle.getLow() * priceFactor)
          .withClose(inputCandle.getClose() * priceFactor)
          .withVolume(Math.round(inputCandle.getVolume() * volumeFactor));
      outputCandlesMap.put(date, adjustedCandle);

      // If there is a dividend or a split found, apply the factor to all
      // the previous candles.
      if (dividendMap.containsKey(date)) {
        double dividend = dividendMap.get(date);
        priceFactor = priceFactor * inputCandle.getClose() / (inputCandle.getClose() + dividend);
      }
    }

    return outputCandlesMap;
  }
}
