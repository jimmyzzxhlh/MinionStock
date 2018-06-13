package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.mysql.cj.core.util.StringUtils;

import download.DownloadHelper;

public class TestDividend {    
    public static void main(String[] args) throws Exception {
//        downloadDividendsFromDividendInformation();
//        compareDividendInformation();
        fixDividend();
    }
    
    private static void downloadDividendsFromDividendInformation() throws Exception {
        File inputFile = new File("data/duplicated_dividend_analyzed.csv");
        File outputFile = new File("data/duplicated_dividend_information.csv");
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));            
        ) {
            String line = br.readLine();
            TreeSet<String> symbols = new TreeSet<>(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                symbols.add(symbol);
//                break;
            }
            for (String symbol : symbols) {
                System.out.println(symbol);
                String url = "http://www.dividendinformation.com/" + symbol + "_dividends/";
                String downloadStr = DownloadHelper.downloadURLToString(url);
                
                String[] downloadData = downloadStr.split("\n");
                int index = 0;
                while (index < downloadData.length) {
                    String downloadLine = downloadData[index].trim();
                    index++;
                    if (downloadLine.equals("['Date', 'DPS'],")) {
                        break;
                    }                    
                }
                
                while (index < downloadData.length) {
                    String dividendData = downloadData[index].trim();
                    if (dividendData.equals("]);")) {
                        break;
                    }
                    dividendData = dividendData.replaceAll("[\\['\\-\\]]", "").replace(" ", "").trim();                                      
                    if (StringUtils.isEmptyOrWhitespaceOnly(dividendData) || dividendData.equals(",")) {
                        index++;
                        continue;
                    }
                    
                    String date = dividendData.split(",")[0];
                    String dividend = dividendData.split(",")[1];
                    bw.write(symbol + "," + date + "," + dividend);
                    bw.newLine();
                    index++;
                }
                
                Thread.sleep(1000);
                bw.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void compareDividendInformation() throws Exception {
        File inputFile = new File("data/duplicated_dividend_analyzed.csv");
        File inputDiFile = new File("data/duplicated_dividend_information.csv");
        File outputFile = new File("data/duplicated_dividend_analyzed_with_di.csv");
        Map<String, Map<String, Double>> map = new TreeMap<>();
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputDiFile));
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                Double dividend = Double.valueOf(data[2]);
                Map<String, Double> dividendMap = map.getOrDefault(symbol, new TreeMap<>());
                map.putIfAbsent(symbol, dividendMap);
                dividendMap.put(date, dividend);                
            }
        }
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));            
        ) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                bw.write(line + ",");
                String[] data = line.split(",");
                String symbol = data[0];
                String firstDate = data[1];
                String secondDate = data[2];
                if (!map.containsKey(symbol)) {
                    bw.write("NA");
                    bw.newLine();
                    continue;
                }                
                Map<String, Double> dividendForSymbol = map.get(symbol);
                if (dividendForSymbol.containsKey(firstDate)) {
                    bw.write(firstDate + "," + String.valueOf(dividendForSymbol.get(firstDate)));
                }
                else if (dividendForSymbol.containsKey(secondDate)) {
                    bw.write(secondDate + "," + String.valueOf(dividendForSymbol.get(secondDate)));
                }
                else {
                    bw.write("NA");
                }
                bw.newLine();                   
            }
        }
    }
    
    private static void fixDividend() throws Exception {
        File inputFile = new File("data/duplicated_dividend_to_fix.csv");
//        File inputDiFile = new File("data/duplicated_dividend_information.csv");
//        File outputFile = new File("data/duplicated_dividend_analyzed_with_di.csv");
        Map<String, Map<String, Double>> map = new TreeMap<>();
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {        
                String[] data = line.split(",");
                String symbol = data[0];
                String date = data[1];
                Double dividend = Double.valueOf(data[2]);
                Map<String, Double> dividendMap = map.getOrDefault(symbol, new TreeMap<>());
                map.putIfAbsent(symbol, dividendMap);
                dividendMap.put(date, dividend);
            }
        }
        
        inputFile = new File("data/EOD_20180119.csv");
        File outputFile = new File("data/EOD_20180119_div_fixed.csv");
        
        int dividendFixed = 0;
        
        try (
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        ) {
            String line;
            String lastSymbol = "";
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String symbol = data[0];
                if (!lastSymbol.equals(symbol)) {
                    System.out.println(symbol);
                    lastSymbol = symbol;
                }
                if (!map.containsKey(symbol)) {
                    bw.write(line);
                    bw.newLine();
                    continue;                    
                }
                String date = data[1].replace("-", "");
                if (!map.get(symbol).containsKey(date)) {
                    bw.write(line);
                    bw.newLine();                    
                    continue;
                }
                Double dividend = Double.valueOf(data[7]);
                if (map.get(symbol).get(date).equals(dividend)) {
                    for (int i = 0; i < 7; i++) {
                        bw.write(data[i] + ",");
                    }
                    bw.write("0.0");
                    for (int i = 8; i < data.length; i++) {
                        bw.write("," + data[i]);
                    }
                    bw.newLine();
                    dividendFixed++;
                }
                else {
                    System.err.println("Something goes wrong for " + symbol + ": " + date + ", " + dividend);
                    return;
                }
            }
        }
        System.out.println("Dividend fixed: " + dividendFixed);
    }
}
