package download;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import company.Company;
import download.tiingo.TiingoConst;
import download.tiingo.TiingoDailyData;
import download.tiingo.TiingoUrlBuilder;
import util.CommonUtil;

public class DownloadHelper {

    private static final Logger log = LoggerFactory.getLogger(DownloadHelper.class);
    private static final String TICKER_LIST_URL = "http://static.quandl.com/end_of_day_us_stocks/ticker_list.csv";

    public static Map<String, Company> downloadCompanies() throws IOException {
        Map<String, Company> map = new TreeMap<>();

        log.info("Downloading companies from URL: " + TICKER_LIST_URL);
        try (BufferedReader br = getBufferedReaderFromURL(TICKER_LIST_URL)) {
            br.readLine(); // Skip the first title line
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = CommonUtil.splitCSVLine(line);
                String symbol = data[0];
                // Only consider stocks that are pure alphabets 
                if (symbol.length() == 0 || !symbol.matches("[a-zA-Z]*")) {
                    continue;
                }
                // TODO - Get industry and sector information from IEX.
                Company company = new Company().withSymbol(symbol);
                map.put(symbol, company);
            }
        } catch (IOException e) {
            log.error("Unable to download companies from URL: " + TICKER_LIST_URL);
            throw e;
        }
    
        log.info(map.size() + " companies have been downloaded.");
        return map;
    }

    public static boolean downloadURLToFile(String urlString, String fileName) {
        log.info("Downloading URL " + urlString + " to file " + fileName + " ...");
        URL url = getURL(urlString);
        if (url == null)
            return false;

        try (FileOutputStream fos = new FileOutputStream(fileName);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
            return true;
        } catch (Exception e) {
            log.error(String.format("Failed to download from URL %s to file %s: ", urlString, fileName), e);
            return false;
        }
    }

    public static String downloadURLToString(String urlString) {
        log.info("Downloading URL " + urlString + " to string ...");
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[64000];
        try (BufferedReader br = getBufferedReaderFromURL(urlString)) {
            int len = 0;
            while ((len = br.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("Failed to download from URL " + urlString + " to string: ", e);            
        }
        return sb.toString();
    }

    private static URL getURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            log.error("Failed to get URL object: " + urlString, e);
            return null;
        }
    }

    public static BufferedReader getBufferedReaderFromURL(String urlString) throws IOException {
        URL url = getURL(urlString);
        return new BufferedReader(new InputStreamReader(url.openStream()));        
    }
    
    public static String downloadURLToString(String urlString, Map<String, String> headers) {
        log.info("Downloading URL " + urlString + " to string ...");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(urlString);
        
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
        
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            log.error(String.format("Failed to download from URL %s to string: ", urlString), e);
            return "";
        }
        
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            log.error(String.format("Failed to download from URL %s to string. Code: %s, Reason: %s",
                urlString, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[64000];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            int len = 0;
            while ((len = br.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("Failed to download from URL " + urlString + " to string: ", e);            
        }
        return sb.toString();
    }
    
    public static List<TiingoDailyData> downloadTiingoDailyData(String symbol, LocalDate startDate, LocalDate endDate) {
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
        
        TiingoDailyData[] dataArray = g.fromJson(str, TiingoDailyData[].class);
        if (dataArray == null || dataArray.length == 0) {
            return ImmutableList.of();
        }
        
        return Arrays.asList(dataArray);
    }
    
    public static TreeMap<LocalDate, TiingoDailyData> downloadTiingoDailyDataMap(String symbol, LocalDate startDate, LocalDate endDate) {
        List<TiingoDailyData> dataList = downloadTiingoDailyData(symbol, startDate, endDate);
        
        TreeMap<LocalDate, TiingoDailyData> map = new TreeMap<>();
        for (TiingoDailyData data : dataList) {
            map.put(data.getLocalDate(), data);
        }
        
        return map;        
    }
    
//    private static String getCompaniesURL() {
//        return "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&render=download&exchange="
//                + exchange.name().toLowerCase();
//    }
}
