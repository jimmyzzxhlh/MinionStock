package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import stock.DailyCandle;
import stock.Dividend;
import stock.Split;
import util.CommonUtil;

public class Sanitize {
    public static void main(String[] args) throws Exception {
//        splitAdjustedData();
//        writeAdjustedPriceFile();
//        findStockWithBothSplitAndDividend();
        writeFeatures();
    }
    
    /**
     * Write the following features given an adjusted price file
     * - (Weekly Close Price / X week EMA) - 1
     *   X = 4, 12, 26, 52
     *   This shows the distance between the current close price and the corrsponding EMA, in percentage w.r.t EMA.
     * - (Current X week EMA / Previous X week EMA) - 1
     *   X = 4, 12, 26, 52
     *   This is basically the slope of the EMA line which shows the trend of EMA.
     * - Current week body length, upper length, lower length in percentage.
     * 
     * Also exclude symbols that we do not care about.
     * 
     * @throws Exception
     */
    private static void writeFeatures() throws Exception {
        File inputFile = new File("data/EOD_20180119_adjusted.csv");
        File outputFile = new File("data/EOD_20180119_features.csv");
        
        
    }
    
    private static void findStockWithBothSplitAndDividend() throws Exception {
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");
        
        Table<String, String, Dividend> dividendTable = HashBasedTable.create();
        Table<String, String, Split> splitTable = HashBasedTable.create();
        
        try (
            BufferedReader brDividend = new BufferedReader(new FileReader(dividendFile));
            BufferedReader brSplit = new BufferedReader(new FileReader(splitFile));
        ) {
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
     * Given unadjusted stock price, split and dividend information, generate an adjusted price file.
     */
    private static void writeAdjustedPriceFile() throws Exception {
        File originalFile = new File("data/EOD_20180119_original.csv");
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");
        File adjustedFile = new File("data/EOD_20180119_adjusted.csv");
        
        Table<String, String, Dividend> dividendTable = HashBasedTable.create();
        Table<String, String, Split> splitTable = HashBasedTable.create();
        
        try (
            BufferedReader brDividend = new BufferedReader(new FileReader(dividendFile));
            BufferedReader brSplit = new BufferedReader(new FileReader(splitFile));
            BufferedReader brOriginal = new BufferedReader(new FileReader(originalFile));
            BufferedWriter bwAdjusted = new BufferedWriter(new FileWriter(adjustedFile));
        ) {
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
                    TreeSet<DailyCandle> outputCandles = calculateAdjustedPrice(lastSymbol, inputCandles, dividendTable, splitTable);
                    writeAdjustedPriceForSymbol(lastSymbol, outputCandles, bwAdjusted);                    
                    inputCandles = new TreeSet<>(Collections.reverseOrder());                 
                }
                
                String date = data[1];
                double open = Double.valueOf(data[2]);
                double high = Double.valueOf(data[3]);
                double low = Double.valueOf(data[4]);
                double close = Double.valueOf(data[5]);
                long volume = Math.round(Double.valueOf(data[6]));
                
                DailyCandle candle = new DailyCandle()                        
                    .withDate(date)
                    .withOpen(open)
                    .withHigh(high)
                    .withLow(low)
                    .withClose(close)
                    .withVolume(volume);
                inputCandles.add(candle);
                
                lastSymbol = symbol;
            }
            
            System.out.println(lastSymbol);
            // Handle the last symbol.
            TreeSet<DailyCandle> outputCandles = calculateAdjustedPrice(lastSymbol, inputCandles, dividendTable, splitTable);
            writeAdjustedPriceForSymbol(lastSymbol, outputCandles, bwAdjusted);
        }
    }
    
    private static TreeSet<DailyCandle> calculateAdjustedPrice(
            String symbol,
            TreeSet<DailyCandle> inputCandles,
            Table<String, String, Dividend> dividendTable,
            Table<String, String, Split> splitTable) {
        // The output is in the normal order so that it can be written to the file.
        TreeSet<DailyCandle> outputCandles = new TreeSet<>();
        
        double priceFactor = 1.0;
        double volumeFactor = 1.0;
        
        // Backward iteration
        for (DailyCandle candle : inputCandles) {
            String date = candle.getDateString();
            
            DailyCandle adjustedCandle = new DailyCandle()
                    .withDate(date)
                    .withOpen(candle.getOpen() * priceFactor)
                    .withHigh(candle.getHigh() * priceFactor)
                    .withLow(candle.getLow() * priceFactor)
                    .withClose(candle.getClose() * priceFactor)
                    .withVolume(Math.round(candle.getVolume() * volumeFactor));       
            outputCandles.add(adjustedCandle);
            
            // If there is a dividend or a split found, apply the factor to all the previous candles.
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
    
    private static void writeAdjustedPriceForSymbol(String symbol, TreeSet<DailyCandle> candles, BufferedWriter bw) throws Exception {
        for (DailyCandle candle : candles) {
            String output = StringUtils.join(Arrays.asList(
                    symbol,
                    candle.getDateString(),
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getVolume()), ",");
            bw.write(output);
            bw.newLine();
        }                    
    }
    
    private static void splitAdjustedData() throws Exception {
        File inputFile = new File("data/EOD_20180119_div_split_fixed.csv");
        File originalFile = new File("data/EOD_20180119_original.csv");
        File dividendFile = new File("data/EOD_20180119_dividend.csv");
        File splitFile = new File("data/EOD_20180119_split.csv");
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bwOriginal = new BufferedWriter(new FileWriter(originalFile));
            BufferedWriter bwDividend = new BufferedWriter(new FileWriter(dividendFile));
            BufferedWriter bwSplit = new BufferedWriter(new FileWriter(splitFile));
        ) {
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
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ) {
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
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        ) {
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
                }
                else {
                    System.err.println("Something goes wrong for " + symbol + ": " + date + ", " + split);
                    return;
                }
            }
        }        
    }
}
