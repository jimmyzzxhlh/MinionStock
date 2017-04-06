package download;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.Company;
import company.CompanyEnum.Exchange;

public class DownloadHelper {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadHelper.class);
	
	public static Map<String, Company> downloadCompanies() {
		Map<String, Company> map = new HashMap<>();

		for (Exchange exchange : Exchange.values()) {
			String url = getCompaniesURL(exchange);
			try (BufferedReader br = getBufferedReaderFromURL(url)) {
				br.readLine();   //Skip the first title line
				String line;
				while ((line = br.readLine()) != null) {
					Company company = new Company(line);
					map.put(company.getSymbol(), company);
				}
			}
			catch (Exception e) {
				log.error("Unable to download companies from URL: " + url, e);
			}
		}
		
		return map;
	}
	
	public static boolean downloadURLToFile(String urlString, String filename) {
		URL url = getURL(urlString);
		if (url == null) return false;
		
		try (
			FileOutputStream fos = new FileOutputStream(filename);
			ReadableByteChannel	rbc = Channels.newChannel(url.openStream());
		) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.flush();
			return true;
		}
		catch (Exception e) {
			log.error("Unable to download from URL " + url + " to file " + filename, e);
			return false;
		}		
	}

	private static URL getURL(String urlString) {
		try {
			return new URL(urlString);
		}
		catch (Exception e) {
			log.error("Unable to get URL object: " + urlString, e);
			return null;
		}
	}

	private static BufferedReader getBufferedReaderFromURL(String urlString) {
		URL url = getURL(urlString);
		if (url == null) return null;
		
		try {
			return new BufferedReader(new InputStreamReader(url.openStream()));
		}
		catch (Exception e) {
			log.error("Unable to get Buffered Reader from URL: " + urlString, e);
			return null;
		}
	}
	
	private static String getCompaniesURL(Exchange exchange) {
		return "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&render=download&exchange="
				+ exchange.name().toLowerCase();
	}
}
