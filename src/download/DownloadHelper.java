package download;

import java.io.BufferedReader;
import java.io.FileOutputStream;
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

    public static Map<String, Company> downloadCompanies() {
        Map<String, Company> map = new TreeMap<>();

        for (Exchange exchange : Exchange.values()) {
            String url = getCompaniesURL(exchange);
            log.info("Downloading companies from URL: " + url);
            try (BufferedReader br = getBufferedReaderFromURL(url)) {
                br.readLine(); // Skip the first title line
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = CommonUtil.splitCSVLine(line);
                    Company company = new Company().withExchange(exchange).withSymbol(data[0]).withSector(data[5])
                            .withIndustry(data[6]);
                    map.put(company.getSymbol(), company);
                }
            } catch (Exception e) {
                log.error("Unable to download companies from URL: " + url, e);
            }
        }

        log.info(map.size() + " companies have been downloaded.");
        return map;
    }

    public static boolean downloadURLToFile(String urlString, String fileName) {
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

    public static BufferedReader getBufferedReaderFromURL(String urlString) {
        URL url = getURL(urlString);
        if (url == null)
            return null;

        try {
            return new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception e) {
            log.error("Unable to get Buffered Reader from URL: " + urlString, e);
            return null;
        }
    }

    private static String getCompaniesURL(Exchange exchange) {
        return "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&render=download&exchange="
                + exchange.name().toLowerCase();
    }
}
