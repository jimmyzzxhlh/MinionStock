package download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import company.Company;
import company.CompanyEnum.Industry;
import company.CompanyEnum.Sector;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DownloadHelper.class)
public class DownloadTest {
	
	@Test
	public void testDownloadCompanies() throws Exception {
		PowerMockito.spy(DownloadHelper.class);
		BufferedReader br = PowerMockito.mock(BufferedReader.class);
		PowerMockito.doReturn(br).when(DownloadHelper.class, "getBufferedReaderFromURL", Mockito.anyString());
		PowerMockito.when(br.readLine()).thenReturn(
				"Symbol,Name,LastSale,MarketCap,IPOyear,Sector,industry,Summary Quote"
		).thenReturn(
				"AAPL,Apple Inc.,117.91,$628.73B,n/a,Technology,Computer Manufacturing,http://www.nasdaq.com/symbol/aapl"
		).thenReturn(null);
		Map<String, Company> map = DownloadHelper.downloadCompanies();
		assertTrue(map.containsKey("AAPL"));
		Company company = map.get("AAPL");
		assertEquals(Sector.TECHNOLOGY, company.getSector());
		assertEquals(Industry.COMPUTER_MANUFACTURING, company.getIndustry());
	}
}
