package download;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.Company;
import company.CompanyEnum.Exchange;
import util.CommonUtil;

public class DownloadHelper {

    private static final Logger log = LoggerFactory.getLogger(DownloadHelper.class);

    public static Map<String, Company> downloadCompanies() throws IOException {
        Map<String, Company> map = new TreeMap<>();

        for (Exchange exchange : Exchange.values()) {
            String url = getCompaniesURL(exchange);
            log.info("Downloading companies from URL: " + url);
            try (BufferedReader br = getBufferedReaderFromURL(url)) {
                br.readLine(); // Skip the first title line
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = CommonUtil.splitCSVLine(line);
                    String symbol = data[0];
                    // Only consider stocks that are pure alphabets 
                    if (symbol.length() == 0 || !symbol.matches("[a-zA-Z]*")) {
                        continue;
                    }
                    Company company = new Company().withExchange(exchange).withSymbol(symbol).withSector(data[5])
                            .withIndustry(data[6]);
                    map.put(symbol, company);
                }
            } catch (IOException e) {
                log.error("Unable to download companies from URL: " + url);
                throw e;
            }
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

    private static String getCompaniesURL(Exchange exchange) {
        return "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&render=download&exchange="
                + exchange.name().toLowerCase();
    }
}
