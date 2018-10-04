package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.DocFlavor.STRING;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.MatcherAssert;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import download.DownloadHelper;
import download.tiingo.TiingoDailyData;
import enums.StockEnum.CandleDataType;
import stock.Calculator;
import stock.DailyCandle;
import stock.Dividend;
import stock.ProfitAndLoss;
import stock.Split;
import stock.WeeklyCandle;
import util.CommonUtil;

public class Sanitize {
    public static void main(String[] args) throws Exception {
        // splitAdjustedData();
        // writeAdjustedPriceFile();
        // findStockWithBothSplitAndDividend();
        // filterStock();
        // writeFeatures();
        // verifyFeatures();
        // filterFeatures();
        // fixOriginalPrice();
        // fixDecimalsForOriginalPrice();
        // checkDailyGap();
        // checkNormalGap();
        // verifyDailyGap();
        fixDividendAndSplitBasedOnTiingo();
    }

    private static Table<String, String, Double> getSplits() throws Exception {
        Table<String, String, Double> splitsTable = HashBasedTable.create();
        File splitsFile = new File("data/EOD_20180119_split.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(splitsFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                String date = data[1];
                double amount = Double.valueOf(data[2]);
                splitsTable.put(symbol, date, amount);
            }
        }

        return splitsTable;
    }

    private static Table<String, String, Double> getDividends() throws Exception {
        Table<String, String, Double> dividendsTable = HashBasedTable.create();
        File dividendsFile = new File("data/EOD_20180119_dividend.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(dividendsFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                String date = data[1];
                double amount = Double.valueOf(data[2]);
                dividendsTable.put(symbol, date, amount);
            }
        }

        return dividendsTable;
    }

    private static void fixDividendAndSplitBasedOnTiingo() throws Exception {
        Table<String, String, Double> dividendsTable = getDividends();
        Table<String, String, Double> splitsTable = getSplits();
        File gapFile = new File("data/EOD_20180119_adjusted_gap_output.csv");
        File dividendFixFile = new File("data/EOD_20180119_adjusted_gap_dividend_fix.csv");
        File splitFixFile = new File("data/EOD_20180119_adjusted_gap_split_fix.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(gapFile));
                BufferedWriter bwd = new BufferedWriter(new FileWriter(dividendFixFile));
                BufferedWriter bws = new BufferedWriter(new FileWriter(splitFixFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] data = CommonUtil.splitCSVLine(line);
                
                String matched = data[3];
                if (matched.equals("Y") || matched.equals("NA")) {
                    continue;
                }
                
                if (data.length >= 6) {
                    String solution = data[5].toLowerCase();
                    if (solution.contains("fixed") || solution.contains("no fix")) {
                        continue;
                    }
                }
                
                String symbol = data[0];
                String dateStr = data[1];

                if (!dividendsTable.contains(symbol, dateStr) && !splitsTable.contains(symbol, dateStr)) {
                    continue;
                }

                LocalDate date = CommonUtil.parseDate(dateStr);
                List<TiingoDailyData> tiingoDailyDatas = DownloadHelper.downloadTiingoDailyData(symbol, date, date);
                if (tiingoDailyDatas.size() == 0) {                    
                    continue;
                }
                TiingoDailyData tiingoDailyData = tiingoDailyDatas.get(0);
                if (dividendsTable.contains(symbol, dateStr)) {
                    double dividend = dividendsTable.get(symbol, dateStr);
                    if (Math.abs(tiingoDailyData.getDivCash() - dividend) > 0.01) {
                       bwd.write(StringUtils.join(Arrays.asList(symbol, dateStr, dividend, tiingoDailyData.getDivCash()), ","));
                       bwd.newLine();
                    }
                }
                if (splitsTable.contains(symbol, dateStr)) {
                    double split = splitsTable.get(symbol, dateStr);
                    if (Math.abs(tiingoDailyData.getSplitFactor() - split) > 0.01) {
                        bws.write(StringUtils.join(Arrays.asList(symbol, dateStr, split, tiingoDailyData.getSplitFactor()), ","));
                        bws.newLine();
                    }    
                }                               
            }
        }
    }

    static class SymbolName implements Comparable {
        String symbol;
        String lastDate;
        String symbolWithLastMonth;
        String path;

        @Override
        public int compareTo(Object o) {
            SymbolName sn2 = (SymbolName) o;
            return this.lastDate.compareTo(sn2.lastDate);
        }
    }

    private static void verifyDailyGap() throws Exception {
        File gapFile = new File("data/EOD_20180119_adjusted_gap.csv");
        File normalGapFile = new File("data/normal_gap.csv");
        File namesFile = new File("D:\\Trading Data Converted\\names.txt");
        File outputFile = new File("data/EOD_20180119_adjusted_gap_output.csv");

        Map<String, TreeSet<SymbolName>> namesMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(namesFile));) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbolWithLastMonth = data[0];
                String path = data[2];
                int index = symbolWithLastMonth.indexOf("-");
                String symbol = "";
                String lastDate = "";
                if (index == -1) {
                    symbol = symbolWithLastMonth;
                    lastDate = "20180131";
                } else {
                    symbol = symbolWithLastMonth.substring(0, index);
                    lastDate = symbolWithLastMonth.substring(index + 1) + "01";
                }
                SymbolName symbolName = new SymbolName();
                symbolName.symbol = symbol;
                symbolName.lastDate = lastDate;
                symbolName.path = path;
                symbolName.symbolWithLastMonth = symbolWithLastMonth;
                TreeSet<SymbolName> names = namesMap.getOrDefault(symbol, new TreeSet<>());
                namesMap.putIfAbsent(symbol, names);
                names.add(symbolName);
            }
        }

        System.out.println(namesMap.size() + " symbol names found.");

        Set<String> normalGapSet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(normalGapFile));) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                String date = data[1];
                normalGapSet.add(symbol + date);
            }
        }

        System.out.println(normalGapSet.size() + " normal gaps found.");

        try (BufferedReader brGap = new BufferedReader(new FileReader(gapFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            String line;
            while ((line = brGap.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                String date = data[1];
                double gap = Double.valueOf(data[2]);
                if (normalGapSet.contains(symbol + date)) {
                    bw.write(line + ",Y");
                    bw.newLine();
                    continue;
                }
                // Try to find the path in the names.txt
                if (!namesMap.containsKey(symbol)) {
                    bw.write(line + ",NA,symbol cannot be found in names");
                    bw.newLine();
                    continue;
                }

                // Try to find the matched symbol.
                String path = "";
                String symbolWithLastMonth = "";
                TreeSet<SymbolName> names = namesMap.get(symbol);
                for (SymbolName name : names) {
                    if (name.lastDate.compareTo(date) >= 0) {
                        path = name.path;
                        symbolWithLastMonth = name.symbolWithLastMonth;
                        break;
                    }
                }
                if (StringUtils.isEmpty(path)) {
                    bw.write(line + ",,all end dates are too early in names");
                    bw.newLine();
                    continue;
                }
                // Now we have the path. Try to read that file and see if we
                // have the correct gap.
                StringBuilder fullPathSb = new StringBuilder();
                String fullPath = fullPathSb.append("D:\\Trading Data Converted\\").append(path).append("\\")
                        .append(symbolWithLastMonth).append(".csv").toString();
                System.out.println(String.format("Checking %s on %s from %s ...", symbol, date, fullPath));
                try (BufferedReader brCompare = new BufferedReader(new FileReader(new File(fullPath)));) {
                    String lineCompare = brCompare.readLine();
                    double lastClose = Double.MIN_VALUE;
                    while ((lineCompare = brCompare.readLine()) != null) {
                        String[] dataCompare = CommonUtil.splitCSVLine(lineCompare);
                        String dateCompare = dataCompare[0];
                        double open = Double.valueOf(dataCompare[1]);
                        double close = Double.valueOf(dataCompare[4]);
                        if (date.equals(dateCompare)) {
                            if (lastClose == Double.MIN_VALUE) {
                                bw.write(line + ",NA,Date is the first date");
                                bw.newLine();
                                break;
                            }
                            double gapCompare = (open - lastClose) / lastClose;
                            if (Math.abs((gapCompare - gap) / gap) < 0.05) {
                                bw.write(line + ",Y");
                                bw.newLine();
                            } else {
                                bw.write(line + ",N," + gapCompare);
                                bw.newLine();
                            }
                            break;
                        }
                        lastClose = close;
                    }
                }
            }
        }
    }

    private static void checkDailyGap() throws Exception {
        File inputFile = new File("data/EOD_20180119_adjusted.csv");
        File outputFile = new File("data/EOD_20180119_adjusted_gap.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            String line;
            String lastSymbol = "";
            double lastClose = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                double open = Double.valueOf(data[2]);
                double close = Double.valueOf(data[5]);
                if (lastSymbol.equals(symbol) && lastClose > 2) {
                    double gap = (open - lastClose) / lastClose;
                    if (Math.abs(gap) >= 0.2) {
                        bw.write(symbol + "," + date + "," + gap);
                        bw.newLine();
                    }
                }
                lastClose = close;
                lastSymbol = symbol;
            }
        }
    }

    private static void fixDecimalsForOriginalPrice() throws Exception {
        File inputFile = new File("data/EOD_20180119_original.csv");
        File outputFile = new File("data/EOD_20180119_original_v2.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            String line;
            String lastSymbol = "";
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                String openStr = data[2];
                String highStr = data[3];
                String lowStr = data[4];
                String closeStr = data[5];
                String volume = data[6];
                bw.write(StringUtils.join(
                        Arrays.asList(symbol, date, getDecimalFixedString(openStr), getDecimalFixedString(highStr),
                                getDecimalFixedString(lowStr), getDecimalFixedString(closeStr), volume),
                        ","));
                bw.newLine();
            }
        }
    }

    private static String getDecimalFixedString(String str) {
        double value = Double.valueOf(str);
        if (value < 10) {
            if (str.length() > 6) {
                return String.format("%.4f", value);
            }
            return str;
        } else {
            if (str.length() > 4) {
                return String.format("%.2f", value);
            }
            return str;
        }
    }

    private static void fixOriginalPrice() throws Exception {
        File inputFile = new File("data/EOD_20180119_original.csv");
        File fixFile = new File("data/data_to_be_fixed.csv");
        File outputFile = new File("data/EOD_20180119_original_v2.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedReader brFix = new BufferedReader(new FileReader(fixFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            Map<String, String> fixMap = new HashMap<>();
            int totalFixCount = 0;
            String line;
            while ((line = brFix.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                fixMap.put(symbol + date, line);
            }

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                String key = symbol + date;
                if (fixMap.containsKey(key)) {
                    bw.write(fixMap.get(key));
                    totalFixCount++;
                    System.out.println(fixMap.get(key));
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
            System.out.println(totalFixCount);
        }
    }

    private static void verifyFeatures() throws Exception {
        File inputFile = new File("data/EOD_20180119_features_filtered.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                double open = Double.valueOf(data[2]);
                if (open > 0.5) {
                    System.out.println(line);
                }
            }
        }
    }

    /**
     * Filter features that do not have a sufficient amount of turnover. The
     * standard is: 1. Symbol should be valid (Only letters). 2. Current week's
     * close price should be > 1.0 3. Current week's volume * close price should
     * be > 1 million. 4. EMA4 > EMA12 > EMA26 > EMA52 (> Current week's close?)
     * Notice that the filter should not do anything with predicting the price,
     * otherwise we would get a system that performs super well on the training
     * data and very bad on the real data.
     * 
     * @throws Exception
     */
    private static void filterFeatures() throws Exception {
        File inputFile = new File("data/EOD_20180119_features.csv");
        File outputFile = new File("data/EOD_20180119_features_filtered_v2.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            String line = br.readLine();
            bw.write(line);
            bw.newLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];

                if (!CommonUtil.isSymbolValid(symbol)) {
                    continue;
                }

                double close = Double.valueOf(data[24]);
                if (close < 1.0) {
                    continue;
                }

                long volume = Long.valueOf(data[25]);
                if (close * volume < 1e6) {
                    continue;
                }

                double emaFourWeeks = Double.valueOf(data[26]);
                double emaTwelveWeeks = Double.valueOf(data[27]);
                double emaTwentySixWeeks = Double.valueOf(data[28]);
                double emaFiftyTwoWeeks = Double.valueOf(data[29]);
                // TODO - Consider relaxing the requirement by checking if the
                // diff is > -x percent?
                if (!(emaFourWeeks >= emaTwelveWeeks && emaTwelveWeeks >= emaTwentySixWeeks
                        && emaTwentySixWeeks >= emaFiftyTwoWeeks)) {
                    continue;
                }

                bw.write(line);
                bw.newLine();
            }
        }
    }

    /**
     * Write the following features given an adjusted price file - (Weekly Close
     * Price / X week EMA) - 1 X = 4, 12, 26, 52 This shows the distance between
     * the current close price and the corrsponding EMA, in percentage w.r.t
     * EMA. - (Current X week EMA / Previous X week EMA) - 1 X = 4, 12, 26, 52
     * This is basically the slope of the EMA line which shows the trend of EMA.
     * - Current week body length, upper length, lower length in percentage.
     * 
     * Also exclude symbols that we do not care about.
     * 
     * @throws Exception
     */
    private static void writeFeatures() throws Exception {
        File inputFile = new File("data/EOD_20180119_adjusted.csv");
        File outputFile = new File("data/EOD_20180119_features.csv");
        File errorFile = new File("data/EOD_20180119_weekskipped.csv");

        TreeMap<LocalDateTime, WeeklyCandle> weeklyCandles = new TreeMap<>();
        WeeklyCandle weeklyCandle = null;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
                BufferedWriter bwError = new BufferedWriter(new FileWriter(errorFile));) {
            // todo - calculate target labels profit and volume
            bw.write(
                    // The price here is the relevant price movement in
                    // percentage against the last week's close price.
                    "Symbol,Date,Open,High,Low,Close," +
                    // How far is the current close price from the EMA
                            "Dist4,Dist12,Dist26,Dist52," +
                            // Slope for the EMA line
                            "Slope4,Slope12,Slope26,Slope52," +
                            // Current volume / previous X weeks average volume
                            // (excluding current week)
                            "Volume4,Volume12,Volume26,Volume52," +
                            // Maximum profit in the next X weeks (i.e. compare
                            // to high price)
                            "Profit4,Profit12,Profit26," +
                            // Maximum loss in the next X weeks (i.e. compare to
                            // low price)
                            "Loss4,Loss12,Loss26," +
                            // Raw data, for filter before training.
                            // Close price and volume of the current candle.
                            "CurrentClose,CurrentVolume,EMA4,EMA12,EMA26,EMA52");
            bw.newLine();
            String line;
            String lastSymbol = "";
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];

                if (!CommonUtil.isSymbolValid(symbol)) {
                    continue;
                }

                if (lastSymbol.length() > 0 && !lastSymbol.equals(symbol)) {
                    System.out.println(lastSymbol);
                    writeWeeklyEmaToFile(lastSymbol, weeklyCandles, bw);
                    weeklyCandles = new TreeMap<>();
                    weeklyCandle = null;
                    // break;
                }

                lastSymbol = symbol;
                LocalDate date = CommonUtil.parseDate(data[1]);
                double open = Double.valueOf(data[2]);
                double high = Double.valueOf(data[3]);
                double low = Double.valueOf(data[4]);
                double close = Double.valueOf(data[5]);
                long volume = Long.valueOf(data[6]);

                DailyCandle candle = new DailyCandle().withDate(date).withOpen(open).withHigh(high).withLow(low)
                        .withClose(close).withVolume(volume);

                // The very first candle in the whole file.
                if (weeklyCandle == null) {
                    weeklyCandle = new WeeklyCandle(date);
                    weeklyCandle.addDailyCandle(candle);
                } else {
                    // Check if the week is changed. If yes, make sure we are
                    // not skipping any weeks.
                    int currentWeek = weeklyCandle.getWeek();
                    int currentYear = weeklyCandle.getYear();
                    int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int year = date.get(IsoFields.WEEK_BASED_YEAR);
                    if (week != currentWeek) {
                        if (week != currentWeek + 1 && !(week == 1 && year == currentYear + 1)) {
                            // If the week is skipped, flush the current weekly
                            // candles.
                            bwError.write(
                                    String.format("%s,%d,%d,%d,%d", symbol, currentWeek, currentYear, week, year));
                            bwError.newLine();
                            writeWeeklyEmaToFile(symbol, weeklyCandles, bw);
                            weeklyCandles = new TreeMap<>();
                            weeklyCandle = new WeeklyCandle(date);
                            weeklyCandle.addDailyCandle(candle);
                        } else {
                            // Add the weekly candle to map and create a new
                            // weekly candle because the week is changed.
                            weeklyCandles.put(CommonUtil.getDateTime(weeklyCandle.getEndDate()), weeklyCandle);
                            weeklyCandle = new WeeklyCandle(date);
                            weeklyCandle.addDailyCandle(candle);
                        }
                    } else if (currentYear == year) {
                        weeklyCandle.addDailyCandle(candle);
                    } else {
                        // Same as above, flush the current weekly candles.
                        bwError.write(String.format("%s,%d,%d,%d,%d", symbol, currentWeek, currentYear, week, year));
                        bwError.newLine();
                        writeWeeklyEmaToFile(symbol, weeklyCandles, bw);
                        weeklyCandles = new TreeMap<>();
                        weeklyCandle = new WeeklyCandle(date);
                        weeklyCandle.addDailyCandle(candle);
                    }
                }
            }
            System.out.println(lastSymbol);
            writeWeeklyEmaToFile(lastSymbol, weeklyCandles, bw);
        }
    }

    public static void writeWeeklyEmaToFile(String symbol, TreeMap<LocalDateTime, WeeklyCandle> weeklyCandles,
            BufferedWriter bw) throws Exception {

        TreeMap<LocalDateTime, Double> openDist = Calculator.getPriceDist(weeklyCandles, CandleDataType.OPEN);
        TreeMap<LocalDateTime, Double> highDist = Calculator.getPriceDist(weeklyCandles, CandleDataType.HIGH);
        TreeMap<LocalDateTime, Double> lowDist = Calculator.getPriceDist(weeklyCandles, CandleDataType.LOW);
        TreeMap<LocalDateTime, Double> closeDist = Calculator.getPriceDist(weeklyCandles, CandleDataType.CLOSE);

        TreeMap<LocalDateTime, Double> emaFourWeeks = Calculator.getExponentialMovingAverage(weeklyCandles, 4);
        TreeMap<LocalDateTime, Double> emaTwelveWeeks = Calculator.getExponentialMovingAverage(weeklyCandles, 12);
        TreeMap<LocalDateTime, Double> emaTwentySixWeeks = Calculator.getExponentialMovingAverage(weeklyCandles, 26);
        TreeMap<LocalDateTime, Double> emaFiftyTwoWeeks = Calculator.getExponentialMovingAverage(weeklyCandles, 52);

        TreeMap<LocalDateTime, Double> emaDistFourWeeks = Calculator.getEmaDistance(weeklyCandles, emaFourWeeks);
        TreeMap<LocalDateTime, Double> emaDistTwelveWeeks = Calculator.getEmaDistance(weeklyCandles, emaTwelveWeeks);
        TreeMap<LocalDateTime, Double> emaDistTwentySixWeeks = Calculator.getEmaDistance(weeklyCandles,
                emaTwentySixWeeks);
        TreeMap<LocalDateTime, Double> emaDistFiftyTwoWeeks = Calculator.getEmaDistance(weeklyCandles,
                emaFiftyTwoWeeks);

        TreeMap<LocalDateTime, Double> emaSlopeFourWeeks = Calculator.getEmaSlope(emaFourWeeks);
        TreeMap<LocalDateTime, Double> emaSlopeTwelveWeeks = Calculator.getEmaSlope(emaTwelveWeeks);
        TreeMap<LocalDateTime, Double> emaSlopeTwentySixWeeks = Calculator.getEmaSlope(emaTwentySixWeeks);
        TreeMap<LocalDateTime, Double> emaSlopeFiftyTwoWeeks = Calculator.getEmaSlope(emaFiftyTwoWeeks);

        TreeMap<LocalDateTime, Double> volumeFourWeeks = Calculator.getRelativeVolume(weeklyCandles, 4);
        TreeMap<LocalDateTime, Double> volumeTwelveWeeks = Calculator.getRelativeVolume(weeklyCandles, 12);
        TreeMap<LocalDateTime, Double> volumeTwentySixWeeks = Calculator.getRelativeVolume(weeklyCandles, 26);
        TreeMap<LocalDateTime, Double> volumeFiftyTwoWeeks = Calculator.getRelativeVolume(weeklyCandles, 52);

        TreeMap<LocalDateTime, ProfitAndLoss> profitAndLossFourWeeks = Calculator.getProfitAndLoss(weeklyCandles, 4);
        TreeMap<LocalDateTime, ProfitAndLoss> profitAndLossTwelveWeeks = Calculator.getProfitAndLoss(weeklyCandles, 12);
        TreeMap<LocalDateTime, ProfitAndLoss> profitAndLossTwentySixWeeks = Calculator.getProfitAndLoss(weeklyCandles,
                26);

        // Print out the time range for which we have all the features and
        // targets.
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        // Choose ema slope fifty two weeks feature since it needs 53 weeks in
        // the past for calculation.
        if (!emaSlopeFiftyTwoWeeks.isEmpty()) {
            startDateTime = emaSlopeFiftyTwoWeeks.firstKey();
        }
        // Choose profit and loss twenty six weeks target since it needs 26
        // weeks in the future for calculation.
        if (!profitAndLossTwentySixWeeks.isEmpty()) {
            endDateTime = profitAndLossTwentySixWeeks.lastKey();
        }
        System.out.println(startDateTime + " - " + endDateTime);

        for (Entry<LocalDateTime, Double> entry : emaSlopeFiftyTwoWeeks.entrySet()) {
            LocalDateTime dateTime = entry.getKey();
            // We do not know the so we can't calculate the target.
            // Notice that for volume we only need to check four weeks because
            // if 8 weeks volume is 0 then 4 week volume is also 0.
            if (!profitAndLossTwentySixWeeks.containsKey(dateTime) || !volumeFourWeeks.containsKey(dateTime)) {
                break;
            }

            bw.write(StringUtils.join(Arrays.asList(symbol, CommonUtil.formatDate(dateTime.toLocalDate()),
                    openDist.get(dateTime), highDist.get(dateTime), lowDist.get(dateTime), closeDist.get(dateTime),
                    // emaFourWeeks.get(dateTime),
                    // emaTwelveWeeks.get(dateTime),
                    // emaTwentySixWeeks.get(dateTime),
                    // emaFiftyTwoWeeks.get(dateTime),
                    emaDistFourWeeks.get(dateTime), emaDistTwelveWeeks.get(dateTime),
                    emaDistTwentySixWeeks.get(dateTime), emaDistFiftyTwoWeeks.get(dateTime),
                    emaSlopeFourWeeks.get(dateTime), emaSlopeTwelveWeeks.get(dateTime),
                    emaSlopeTwentySixWeeks.get(dateTime), emaSlopeFiftyTwoWeeks.get(dateTime),
                    volumeFourWeeks.get(dateTime), volumeTwelveWeeks.get(dateTime), volumeTwentySixWeeks.get(dateTime),
                    volumeFiftyTwoWeeks.get(dateTime), profitAndLossFourWeeks.get(dateTime).getProfit(),
                    profitAndLossTwelveWeeks.get(dateTime).getProfit(),
                    profitAndLossTwentySixWeeks.get(dateTime).getProfit(),
                    profitAndLossFourWeeks.get(dateTime).getLoss(), profitAndLossTwelveWeeks.get(dateTime).getLoss(),
                    profitAndLossTwentySixWeeks.get(dateTime).getLoss(), weeklyCandles.get(dateTime).getClose(),
                    weeklyCandles.get(dateTime).getVolume(), emaFourWeeks.get(dateTime), emaTwelveWeeks.get(dateTime),
                    emaTwentySixWeeks.get(dateTime), emaFiftyTwoWeeks.get(dateTime)), ","));
            bw.newLine();
        }
    }

    private static void findStockWithBothSplitAndDividend() throws Exception {
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");

        Table<String, String, Dividend> dividendTable = HashBasedTable.create();
        Table<String, String, Split> splitTable = HashBasedTable.create();

        try (BufferedReader brDividend = new BufferedReader(new FileReader(dividendFile));
                BufferedReader brSplit = new BufferedReader(new FileReader(splitFile));) {
            String line = brDividend.readLine();
            while ((line = brDividend.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = CommonUtil.removeHyphen(data[1]);
                double amount = Double.valueOf(data[2]);
                Dividend dividend = new Dividend();
                dividend.setSymbol(symbol);
                dividend.setDate(date);
                dividend.setAmount(amount);
                dividendTable.put(symbol, date, dividend);
            }

            line = brSplit.readLine();
            while ((line = brSplit.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = CommonUtil.removeHyphen(data[1]);
                double factor = Double.valueOf(data[2]);
                Split split = new Split();
                split.setSymbol(symbol);
                split.setDate(date);
                split.setFactor(factor);
                splitTable.put(symbol, date, split);
            }

            for (Cell<String, String, Dividend> cell : dividendTable.cellSet()) {
                String symbol = cell.getRowKey();
                String date = cell.getColumnKey();
                if (splitTable.contains(symbol, date)) {
                    System.out.println(cell.getValue() + " " + splitTable.get(symbol, date));
                }
            }
        }

    }

    /**
     * Given unadjusted stock price, split and dividend information, generate an
     * adjusted price file.
     */
    private static void writeAdjustedPriceFile() throws Exception {
        File originalFile = new File("data/EOD_20180119_original.csv");
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");
        File adjustedFile = new File("data/EOD_20180119_adjusted.csv");

        Table<String, String, Dividend> dividendTable = HashBasedTable.create();
        Table<String, String, Split> splitTable = HashBasedTable.create();

        try (BufferedReader brDividend = new BufferedReader(new FileReader(dividendFile));
                BufferedReader brSplit = new BufferedReader(new FileReader(splitFile));
                BufferedReader brOriginal = new BufferedReader(new FileReader(originalFile));
                BufferedWriter bwAdjusted = new BufferedWriter(new FileWriter(adjustedFile));) {
            String line = brDividend.readLine();
            while ((line = brDividend.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = CommonUtil.removeHyphen(data[1]);
                double amount = Double.valueOf(data[2]);
                Dividend dividend = new Dividend();
                dividend.setSymbol(symbol);
                dividend.setDate(date);
                dividend.setAmount(amount);
                dividendTable.put(symbol, date, dividend);
            }

            line = brSplit.readLine();
            while ((line = brSplit.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = CommonUtil.removeHyphen(data[1]);
                double factor = Double.valueOf(data[2]);
                Split split = new Split();
                split.setSymbol(symbol);
                split.setDate(date);
                split.setFactor(factor);
                splitTable.put(symbol, date, split);
            }

            String lastSymbol = "";

            // Key is the date string.
            TreeSet<DailyCandle> inputCandles = new TreeSet<>(Collections.reverseOrder());
            while ((line = brOriginal.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];

                if (lastSymbol.length() > 0 && !lastSymbol.equals(symbol)) {
                    System.out.println(lastSymbol);
                    // Calculate the adjusted price and write it to the file.
                    TreeSet<DailyCandle> outputCandles = calculateAdjustedPrice(lastSymbol, inputCandles, dividendTable,
                            splitTable);
                    writeAdjustedPriceForSymbol(lastSymbol, outputCandles, bwAdjusted);
                    inputCandles = new TreeSet<>(Collections.reverseOrder());
                }

                String date = data[1];
                double open = Double.valueOf(data[2]);
                double high = Double.valueOf(data[3]);
                double low = Double.valueOf(data[4]);
                double close = Double.valueOf(data[5]);
                long volume = Math.round(Double.valueOf(data[6]));

                DailyCandle candle = new DailyCandle().withDate(date).withOpen(open).withHigh(high).withLow(low)
                        .withClose(close).withVolume(volume);
                inputCandles.add(candle);

                lastSymbol = symbol;
            }

            System.out.println(lastSymbol);
            // Handle the last symbol.
            TreeSet<DailyCandle> outputCandles = calculateAdjustedPrice(lastSymbol, inputCandles, dividendTable,
                    splitTable);
            writeAdjustedPriceForSymbol(lastSymbol, outputCandles, bwAdjusted);
        }
    }

    private static TreeSet<DailyCandle> calculateAdjustedPrice(String symbol, TreeSet<DailyCandle> inputCandles,
            Table<String, String, Dividend> dividendTable, Table<String, String, Split> splitTable) {
        // The output is in the normal order so that it can be written to the
        // file.
        TreeSet<DailyCandle> outputCandles = new TreeSet<>();

        double priceFactor = 1.0;
        double volumeFactor = 1.0;

        // Backward iteration
        for (DailyCandle candle : inputCandles) {
            String date = candle.getDateString();

            DailyCandle adjustedCandle = new DailyCandle().withDate(date).withOpen(candle.getOpen() * priceFactor)
                    .withHigh(candle.getHigh() * priceFactor).withLow(candle.getLow() * priceFactor)
                    .withClose(candle.getClose() * priceFactor)
                    .withVolume(Math.round(candle.getVolume() * volumeFactor));
            outputCandles.add(adjustedCandle);

            // If there is a dividend or a split found, apply the factor to all
            // the previous candles.
            if (dividendTable.contains(symbol, date)) {
                double dividend = dividendTable.get(symbol, date).getAmount();
                priceFactor = priceFactor * candle.getClose() / (candle.getClose() + dividend);
            }
            if (splitTable.contains(symbol, date)) {
                double split = splitTable.get(symbol, date).getFactor();
                priceFactor = priceFactor / split;
                volumeFactor = volumeFactor * split;
            }
        }

        return outputCandles;
    }

    private static void writeAdjustedPriceForSymbol(String symbol, TreeSet<DailyCandle> candles, BufferedWriter bw)
            throws Exception {
        for (DailyCandle candle : candles) {
            String output = StringUtils.join(Arrays.asList(symbol, candle.getDateString(), candle.getOpen(),
                    candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume()), ",");
            bw.write(output);
            bw.newLine();
        }
    }

    private static void splitAdjustedData() throws Exception {
        File inputFile = new File("data/EOD_20180119_div_split_fixed.csv");
        File originalFile = new File("data/EOD_20180119_original.csv");
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bwOriginal = new BufferedWriter(new FileWriter(originalFile));
                BufferedWriter bwDividend = new BufferedWriter(new FileWriter(dividendFile));
                BufferedWriter bwSplit = new BufferedWriter(new FileWriter(splitFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                List<String> originalList = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    originalList.add(data[i]);
                }
                bwOriginal.write(StringUtils.join(originalList, ","));
                bwOriginal.newLine();

                if (Double.valueOf(data[7]) > 0) {
                    List<String> dividendList = new ArrayList<>();
                    dividendList.addAll(Arrays.asList(data[0], data[1], data[7]));
                    bwDividend.write(StringUtils.join(dividendList, ","));
                    bwDividend.newLine();
                }

                if (Double.valueOf(data[8]) != 1.0) {
                    List<String> splitList = new ArrayList<>();
                    splitList.addAll(Arrays.asList(data[0], data[1], data[8]));
                    bwSplit.write(StringUtils.join(splitList, ","));
                    bwSplit.newLine();
                }
            }
        }
    }

    private static void fixSplit() throws Exception {
        File inputFile = new File("data/duplicated_split_to_fix.csv");
        Map<String, Map<String, Double>> map = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                Double split = Double.valueOf(data[2]);
                Map<String, Double> splitMap = map.getOrDefault(symbol, new TreeMap<>());
                map.putIfAbsent(symbol, splitMap);
                splitMap.put(date, split);
            }
        }

        inputFile = new File("data/EOD_20180119_div_fixed.csv");
        File outputFile = new File("data/EOD_20180119_div_split_fixed.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                if (!map.containsKey(symbol)) {
                    bw.write(line);
                    bw.newLine();
                    continue;
                }
                String date = data[1];
                if (!map.get(symbol).containsKey(date)) {
                    bw.write(line);
                    bw.newLine();
                    continue;
                }
                System.out.println("Fixing " + symbol + " ...");
                Double split = Double.valueOf(data[8]);
                if (Math.abs(map.get(symbol).get(date) - split) < 1e5) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 8; i++) {
                        sb.append(data[i]);
                        sb.append(",");
                    }
                    sb.append("1.0");
                    for (int i = 9; i < data.length; i++) {
                        sb.append(",");
                        sb.append(data[i]);
                    }
                    bw.write(sb.toString());
                    bw.newLine();
                    System.out.println("Old: " + line);
                    System.out.println("New: " + sb.toString());
                } else {
                    System.err.println("Something goes wrong for " + symbol + ": " + date + ", " + split);
                    return;
                }
            }
        }
    }
}
