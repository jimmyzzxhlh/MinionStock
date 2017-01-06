package stock;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

public class CandleListTest {
	
	@Test
	public void testCandleList() {
		CandleList<DailyCandle> candleList = new CandleList<>(new Company("TEST", 100));
		List<DateTime> dateTimeList = Arrays.asList(DateTime.now());
		List<Double> openList = Arrays.asList(50.0, 50.7, 49.2);
		List<Double> highList = Arrays.asList(51.2, 51.8, 52.0);
		List<Double> lowList = Arrays.asList(49.5, 49.3, 49.2);
		List<Double> closeList = Arrays.asList(50.5, 49.4, 51.5);
		final int size = dateTimeList.size();
		for (int i = 0; i < size; i++) {
			candleList.add(new DailyCandle()
							.withDateTime(dateTimeList.get(i))
							.withOpen(openList.get(i))
							.withHigh(highList.get(i))
							.withLow(lowList.get(i))
							.withClose(closeList.get(i)));
		}
		assertEquals(Math.abs(closeList.get(size - 1) - openList.get(0)), candleList.getBodyLength(), 1e-10); 
		
	}
}
