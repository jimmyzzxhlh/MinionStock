package download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import company.Company;
import company.CompanyEnum.Industry;
import company.CompanyEnum.Sector;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DownloadHelper.class)
@PowerMockIgnore( {"javax.management.*"}) 
public class DownloadTest {
	
	/**
	 * Test downloading company list from nasdaq.com
	 * Mock the buffered reader to simulate the company list. 
	 * @throws Exception
	 */
	@Test
	public void testDownloadCompanies() throws Exception {
		PowerMockito.spy(DownloadHelper.class);
		BufferedReader br = PowerMockito.mock(BufferedReader.class);
		PowerMockito.doReturn(br).when(DownloadHelper.class, "getBufferedReaderFromURL", Mockito.anyString());
		//Currently we have 2 exchanges, so repeat here twice. 
		PowerMockito.when(br.readLine()).
			thenReturn("Symbol,Name,LastSale,MarketCap,IPOyear,Sector,industry,Summary Quote").
			thenReturn("AAPL,Apple Inc.,117.91,$628.73B,n/a,Technology,Computer Manufacturing,http://www.nasdaq.com/symbol/aapl").
			thenReturn(null).
			thenReturn("Symbol,Name,LastSale,MarketCap,IPOyear,Sector,industry,Summary Quote").
			thenReturn("\"BABA\",\"Alibaba Group Holding Limited\",\"93.89\",\"$234.73B\",\"n/a\",\"Miscellaneous\",\"Business Services\",\"http://www.nasdaq.com/symbol/baba\"").
			thenReturn(null);
		Map<String, Company> map = DownloadHelper.downloadCompanies();
		//Check if the company list is parsed correctly.
		assertTrue(map.containsKey("AAPL"));
		Company company = map.get("AAPL");
		assertEquals(Sector.TECHNOLOGY, company.getSector());
		assertEquals(Industry.COMPUTER_MANUFACTURING, company.getIndustry());
		assertTrue(map.containsKey("BABA"));
		company = map.get("BABA");
		assertEquals(Sector.MISCELLANEOUS, company.getSector());
		assertEquals(Industry.BUSINESS_SERVICES, company.getIndustry());
		
	}
}
