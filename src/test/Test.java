package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import download.DownloadHelper;
import download.tiingo.TiingoConst;
import download.tiingo.TiingoDailyData;
import download.tiingo.TiingoUrlBuilder;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;
import stock.DailyCandle;
import stock.Calculator;
import util.CommonUtil;

public class Test {
    private static final DecimalFormat df = new DecimalFormat("#.00");
    
	public static void main(String[] args) throws Exception {
//	    testDynamoDB();
//	    testIpxApi();
//	    testGson();
//	    fixData();
//	    backfillData();
//	    deleteSymbols();
//	    checkStockSplitsHaveDecimals();
//	    checkStockHasConsecutiveDividends();
//	    testTiiingo();
//	    crossVerifyStockPrice();
//	    filterAnalyzedData();	    
//	    analyzeDividendAgainstTiingo();
	    testEma();
	    
	}
	
	// see here as an example:
	// http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
	private static void testEma() { 
	    TreeMap<LocalDateTime, DailyCandle> candles = new TreeMap<>();
	    candles.put(
	            LocalDateTime.of(LocalDate.of(2010, 03, 24), LocalTime.of(0, 0)),
	            new DailyCandle().withClose(22.27));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 03, 25), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.19));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 03, 26), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.08));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 03, 29), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.17));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 03, 30), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.18));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 03, 31), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.13));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 1), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.23));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 5), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.43));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 6), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.24));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 7), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.29));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 8), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.15));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 9), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.39));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 12), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.38));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 13), LocalTime.of(0, 0)),
                new DailyCandle().withClose(22.61));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 14), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.36));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 15), LocalTime.of(0, 0)),
                new DailyCandle().withClose(24.05));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 16), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.75));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 19), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.83));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 20), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.95));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 21), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.63));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 22), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.82));
	    candles.put(
                LocalDateTime.of(LocalDate.of(2010, 4, 23), LocalTime.of(0, 0)),
                new DailyCandle().withClose(23.87));
	    
        TreeMap<LocalDateTime, Double> outputMap = Calculator.getExponentialMovingAverage(candles, 10);
        for (Entry<LocalDateTime, Double> entry : outputMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
	}
	
	private static void deleteSymbols() throws Exception {
	    Set<String> symbols = getSymbolsFromQuantDL();
	    for (String symbol : symbols) {
	        if (symbol.compareTo("AIII") > 0) break;
	        if (!symbol.matches("[a-zA-Z]*")) {
	            DynamoDBQueryExpression<DailyItem> queryExpression = new DynamoDBQueryExpression<DailyItem>()
                    .withHashKeyValues(new DailyItem(symbol));
                List<DailyItem> items = DynamoDBProvider.getInstance().getMapper().query(DailyItem.class, queryExpression);
                System.out.println("Deleting " + items.size() + " items for " + symbol + " ...");
                DynamoDBProvider.getInstance().getMapper().batchDelete(items);                
	        }
	    }
	}
	
	private static Set<String> getSymbolsFromQuantDL() { 
	    Set<String> symbols = new HashSet<>();
	    File file = new File("data/EOD_20180113.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                symbols.add(data[0]);
                if (symbol.compareTo("AIII") > 0) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return symbols;
	}
	
	private static void deleteData() throws Exception {
	    Condition condition = new Condition();
	    DynamoDBQueryExpression<DailyItem> queryExpression = new DynamoDBQueryExpression<DailyItem>()
            .withKeyConditionExpression("S = :v_symbol AND D <= :v_date")
            .withExpressionAttributeValues(ImmutableMap.of(
                ":v_symbol", new AttributeValue().withS("AAME"),
                ":v_date", new AttributeValue().withS("20121231")));
	    List<DailyItem> items = DynamoDBProvider.getInstance().getMapper().query(DailyItem.class, queryExpression);
	    DynamoDBProvider.getInstance().getMapper().batchDelete(items);	    
	}
	
	private static void backfillData() throws Exception {
	    File file = new File("data/EOD_20180113.csv");
	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<DailyItem> items = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                DailyItem item = getDailyItem(line);
                if (item.getSymbol().compareTo("AIMC") < 0) {
                    continue;
                }
                else if (item.getSymbol().equals("AIMC")){
                    items.add(item);
                }
                else {
                    backfill(items);
                    break;
                }                
            }            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    private static DailyItem getDailyItem(String line) {
        DailyItem item = new DailyItem();
        String[] data = CommonUtil.splitCSVLine(line);
        item.setSymbol(data[0]);
        item.setDate(CommonUtil.removeHyphen(data[1]));
        item.setOpen(Double.parseDouble(data[2]));
        item.setHigh(Double.parseDouble(data[3]));
        item.setLow(Double.parseDouble(data[4]));
        item.setClose(Double.parseDouble(data[5]));
        item.setVolume(Math.round(Double.parseDouble(data[6])));
        
        return item;
    }
    
    private static void backfill(List<DailyItem> items) {
        System.out.println(String.format("Start backfilling %d items ...", items.size()));
        for (DailyItem item : items) {
            DynamoDBProvider.getInstance().getMapper().save(item);
        }
        System.out.println(String.format("Done backfilling %d items.", items.size()));
    }
    
    private static void checkStockHasConsecutiveDividends() {
        File inputFile = new File("data/EOD_20180119.csv");
        File dividendFile = new File("data/duplicated_dividend.csv");
        File splitFile = new File("data/duplicated_split.csv");
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bwDividend = new BufferedWriter(new FileWriter(dividendFile));
            BufferedWriter bwSplit = new BufferedWriter(new FileWriter(splitFile));
        ) {
            String line;
            double lastDividend = 0;
            double lastSplit = 0;
            String lastSymbol = "";
            String lastDate = "";
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                String date = data[1];
                if (!CommonUtil.isSymbolValid(symbol)) continue;
                
                DailyItem dailyItem = getDailyItem(line);
                Double dividend = Double.parseDouble(data[7]);
                Double split = Double.parseDouble(data[8]);
                
                if (lastSymbol.equals(symbol)) {
                    if (dividend > 0 && lastDividend == dividend) {
                        bwDividend.write(
                            StringUtils.join(
                                Arrays.asList(symbol, lastDate, date, dividend), ","));
                        bwDividend.newLine();
                        bwDividend.flush();
                    }
                    if (split != 1.0 && lastSplit == split) {
                        bwSplit.write(
                            StringUtils.join(
                                Arrays.asList(symbol, lastDate, date, split), ","));
                        bwSplit.newLine();
                        bwSplit.flush();
                    }
                }
                
                lastSymbol = symbol;
                lastDividend = dividend;
                lastSplit = split;
                lastDate = date;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void analyzeDividendAgainstTiingo() throws Exception {
        File inputFile = new File("data/duplicated_dividend.csv");
        File outputFile = new File("data/duplicated_dividend_analyzed.csv");
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));            
        ) {
            String line;
            bw.write("Symbol,FirstDate,SecondDate,Dividend,Comment,FirstDividendTiingo,SecondDividendTiingo");
            bw.newLine();
            while ((line = br.readLine()) != null) {
                String[] strs = CommonUtil.splitCSVLine(line);
                String symbol = strs[0];
                LocalDate startDate = CommonUtil.parseDate(strs[1]);
                LocalDate endDate = CommonUtil.parseDate(strs[2]);                
                String dividendOld = strs[3];
                if (!CommonUtil.isSymbolValid(symbol)) continue;
                
                TreeMap<LocalDate, TiingoDailyData> dataMap = DownloadHelper.downloadTiingoDailyDataMap(symbol, startDate, endDate);
                
                if (!dataMap.containsKey(startDate) || !dataMap.containsKey(endDate)) {
                    // Cannot determine whether the dividend is wrong because Tiingo contains data starting from the end date.
                    String output = StringUtils.join(Arrays.asList(
                        symbol,
                        CommonUtil.formatDate(startDate),
                        CommonUtil.formatDate(endDate),
                        dividendOld,
                        "NA",
                        "",
                        ""),
                            ",");
                    bw.write(output);
                    bw.newLine();
                    System.out.println(output);
                    continue;
                }            
                TiingoDailyData startDateData = dataMap.get(startDate);
                TiingoDailyData endDateData = dataMap.get(endDate);
                String output = "";
                if (endDateData.getDivCash() == 0 && startDateData.getDivCash() > 0) {
                    output = StringUtils.join(Arrays.asList(
                        symbol,
                        CommonUtil.formatDate(startDate),
                        CommonUtil.formatDate(endDate),
                        dividendOld,
                        CommonUtil.formatDate(startDate),
                        "",
                        ""),
                            ",");                                        
                }
                else if (endDateData.getDivCash() > 0 && startDateData.getDivCash() == 0) {
                    output = StringUtils.join(Arrays.asList(
                        symbol,
                        CommonUtil.formatDate(startDate),
                        CommonUtil.formatDate(endDate),
                        dividendOld,
                        CommonUtil.formatDate(endDate),
                        "",
                        ""),
                            ",");                    
                }
                else if (endDateData.getDivCash() > 0 && endDateData.getDivCash() == startDateData.getDivCash()) {
                    output = StringUtils.join(Arrays.asList(
                        symbol,
                        CommonUtil.formatDate(startDate),
                        CommonUtil.formatDate(endDate),
                        dividendOld,
                        "Duplicatedtoo",
                        "",
                        ""),
                            ",");
                }
                else {
                    output = StringUtils.join(Arrays.asList(
                        symbol,
                        CommonUtil.formatDate(startDate),
                        CommonUtil.formatDate(endDate),
                        dividendOld,
                        "Conflict",
                        startDateData.getDivCash(),
                        endDateData.getDivCash()),
                            ",");
                }
                bw.write(output);
                bw.newLine();
                System.out.println(output);
            }
        }
    }
    
    private static void testTiiingo() {
        String symbol = "AAPL";
        LocalDate startDate = LocalDate.of(2012, 1, 1);
        LocalDate endDate = LocalDate.of(2017, 1, 1);
        String url = new TiingoUrlBuilder()
            .withSymbol(symbol)
            .withStartDate(startDate)
            .withEndDate(endDate)
            .build();

        String str = DownloadHelper.downloadURLToString(
            url,
            ImmutableMap.of("Content-Type", "application/json",
                            "Authorization", "Token " + TiingoConst.AUTH_TOKEN));
        Gson g = new Gson();
        List<TiingoDailyData> dataList =
            Arrays.asList(g.fromJson(str, TiingoDailyData[].class));
        for (TiingoDailyData dailyData : dataList) {
            if (dailyData.getSplitFactor() > 1.0) {
                System.out.println(dailyData);
            }            
        }
    }
    
    /**
     * Cross verify the stock price between Tiingo and QuanDL
     */
    private static void crossVerifyStockPrice() {
        File inputFile = new File("data/EOD_20180119.csv");
        File outputFile = new File("data/analyze_20180119.csv");
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            String lastSymbol = null;
            TreeMap<String, DailyItem> map = new TreeMap<>();
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                if (!CommonUtil.isSymbolValid(symbol)) continue;
//                if (symbol.compareTo("CAMP") < 0) continue;
                DailyItem dailyItem = getDailyItem(line);
                
                if (lastSymbol == null || lastSymbol.equals(symbol)) {
                    map.put(dailyItem.getDate(), dailyItem);                    
                }
                else {
                    System.out.println(lastSymbol);
                    analyzeSymbol(bw, lastSymbol, map);
                    map = new TreeMap<>();                    
                }
                lastSymbol = symbol;                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This analyzes the data between Tiingo and QuanDL.
     * 
     * If any of the open/close/low/high price has a delta > 0.05 between the two data sets, then
     * we need to look into it
     * 
     * Also Filter out total amount of trading values that are < $100000 per day, which we probably
     * won't care anyway. 
     * 
     * It seems like most of the time the data from QuanDL has a better quality. However there might be
     * caveats and we need to take closer look.
     * 
     * @param bw
     * @param symbol
     * @param map
     * @throws Exception
     */
    private static void analyzeSymbol(BufferedWriter bw, String symbol, TreeMap<String, DailyItem> map) throws Exception {
        if (map.size() == 0) {
            return;
        }
        LocalDate startDate = CommonUtil.parseDate(map.firstKey());
        LocalDate endDate = CommonUtil.parseDate(map.lastKey());
        
        List<TiingoDailyData> dataList = DownloadHelper.downloadTiingoDailyData(symbol, startDate, endDate);
        
        for (TiingoDailyData dailyData : dataList) {
            String date = dailyData.getDate();
            if (!map.containsKey(date)) {
                continue;
            }
            DailyItem item = map.get(date);
            if (item.getVolume() * item.getClose() < 1e6) {
                continue;
            }
            if (isDeltaTooLarge(item.getOpen(), dailyData.getOpen()) ||
                    isDeltaTooLarge(item.getLow(), dailyData.getLow()) ||
                    isDeltaTooLarge(item.getHigh(), dailyData.getHigh()) ||
                    isDeltaTooLarge(item.getClose(), dailyData.getClose())) {
                bw.write(String.format("%s,%s,%s,%s",
                    symbol,
                    date,
                    getDataString(item.getOpen(), item.getHigh(), item.getLow(), item.getClose()),
                    getDataString(dailyData.getOpen(), dailyData.getHigh(), dailyData.getLow(), dailyData.getClose())));                
                
                if (dailyData.getOpen() == dailyData.getClose() &&
                    dailyData.getHigh() == dailyData.getLow()) {
                    bw.write(",N");
                }
                bw.newLine();
                bw.flush();
            }
        }
    }
    
    private static boolean isDeltaTooLarge(double d1, double d2) {
        return Math.abs(d1 - d2) / d1 > 0.05;                
    }
    
    private static String getDataString(double open, double high, double low, double close) {
        StringJoiner sj = new StringJoiner("|");
        sj.add(Double.toString(open))
          .add(Double.toString(high))
          .add(Double.toString(low))
          .add(Double.toString(close));
        return sj.toString();
    }
    
    public static void filterAnalyzedData() {
        File symbolFile = new File("data/supported_tickers.csv");
        Set<String> symbols = new HashSet<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(symbolFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                if (!CommonUtil.isSymbolValid(symbol)) {
                    continue;
                }
                String exchange = data[1];
                if (!exchange.equals("NASDAQ") && !exchange.equals("NYSE")) {
                    continue;
                }
                String assetType = data[2];
                if (!assetType.equals("Stock")) {
                    continue;
                }
                symbols.add(symbol);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        File inputFile = new File("data/analyze_20180119.csv");
        File outputFile = new File("data/analyze_20180119_filtered.csv");
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String data[] = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                if (!symbols.contains(symbol)) {
                    continue;
                }
                bw.write(line);
                bw.newLine();                
            }
        }
        catch (Exception e) {
            e.printStackTrace();            
        }
    }
}
