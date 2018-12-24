//package stock;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import org.joda.time.DateTime;
//import org.junit.Test;
//
//import company.Company;
//import enums.StockEnum.CandleDataType;
//import util.CommonUtil;
//
//public class CandleListTest {
//	
//	private static final double EPSILON = 1e-10;
//	/**
//	 * Test daily candle list APIs. 
//	 */
//	@Test
//	public void testDailyCandleList() {
//		CandleList<DailyCandle> candleList = new CandleList<>(new Company());
//		List<DateTime> dateTimeList = Arrays.asList(
//				CommonUtil.parseDate("20170101"),
//				CommonUtil.parseDate("20170102"),
//				CommonUtil.parseDate("20170103"));
//		
//		//Create a white candle list with 3 days. 
//		List<Double> openList = Arrays.asList(50.0, 50.7, 49.2);
//		List<Double> highList = Arrays.asList(51.2, 51.8, 52.0);
//		List<Double> lowList = Arrays.asList(49.5, 49.3, 49.2);
//		List<Double> closeList = Arrays.asList(50.5, 49.4, 51.5);
//		List<Long> volumeList = Arrays.asList(100000L, 120000L, 110000L);
//		final int size = dateTimeList.size();
//		for (int i = 0; i < size; i++) {
//			candleList.add(new DailyCandle()
//							.withDateTime(dateTimeList.get(i))
//							.withOpen(openList.get(i))
//							.withHigh(highList.get(i))
//							.withLow(lowList.get(i))
//							.withClose(closeList.get(i))
//							.withVolume(volumeList.get(i)));
//		}
//		
//		//Check candle list basic property
//		assertTrue(candleList.isWhite());
//		assertFalse(candleList.isBlack());
//		assertEquals(Math.abs(closeList.get(size - 1) - openList.get(0)), candleList.getBodyLength(), EPSILON); 
//		assertEquals(Collections.max(highList) - closeList.get(size - 1), candleList.getUpperShadowLength(), EPSILON);
//		assertEquals(openList.get(0) - Collections.min(lowList), candleList.getLowerShadowLength(), EPSILON);
//		
//		//Check candle list maximum and minimum price
//		assertEquals(Collections.max(openList), candleList.getMaxPrice(0, size - 1, CandleDataType.OPEN), EPSILON);
//		assertEquals(Collections.max(highList), candleList.getMaxPrice(0, size - 1, CandleDataType.HIGH), EPSILON);
//		assertEquals(Collections.max(lowList), candleList.getMaxPrice(0, size - 1, CandleDataType.LOW), EPSILON);
//		assertEquals(Collections.max(closeList), candleList.getMaxPrice(0, size - 1, CandleDataType.CLOSE), EPSILON);
//		assertEquals(Collections.min(openList), candleList.getMinPrice(0, size - 1, CandleDataType.OPEN), EPSILON);
//		assertEquals(Collections.min(highList), candleList.getMinPrice(0, size - 1, CandleDataType.HIGH), EPSILON);
//		assertEquals(Collections.min(lowList), candleList.getMinPrice(0, size - 1, CandleDataType.LOW), EPSILON);
//		assertEquals(Collections.min(closeList), candleList.getMinPrice(0, size - 1, CandleDataType.CLOSE), EPSILON);
//		
//		//Check candle list dateTimeMap
//		assertTrue(candleList.hasCandle(CommonUtil.getDateTime("20170101")));
//		assertTrue(candleList.hasCandle(CommonUtil.getDateTime("20170102")));
//		assertTrue(candleList.hasCandle(CommonUtil.getDateTime("20170103")));
//		
//		//Test deep copy
//		CandleList<DailyCandle> candleListCopy = new CandleList<DailyCandle>(candleList);
//		for (int i = 0; i < size; i++) {
//			DailyCandle candle = candleList.get(i);
//			DailyCandle candleCopy = candleListCopy.get(i);
//			assertEquals(candle.getOpen(), candleCopy.getOpen(), EPSILON);
//			assertEquals(candle.getHigh(), candleCopy.getHigh(), EPSILON);
//			assertEquals(candle.getLow(), candleCopy.getLow(), EPSILON);
//			assertEquals(candle.getClose(), candleCopy.getClose(), EPSILON);
//			assertEquals(candle.getDateTime(), candleCopy.getDateTime());
//			assertEquals(candle.getVolume(), candleCopy.getVolume());
//		}
//	}
//}
