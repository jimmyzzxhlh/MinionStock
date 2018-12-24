//package main.job.daily;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.amazonaws.util.CollectionUtils;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//
//import download.DownloadHelper;
//import download.iex.IexDividendData;
//import download.iex.IexConst;
//import download.iex.IexUrlBuilder;
//import dynamodb.DynamoDBHelper;
//import dynamodb.DynamoDBProvider;
//import dynamodb.item.DividendItem;
//import exceptions.JobException;
//import main.job.JobEnum;
//import util.gson.DoubleTypeAdapter;
//
//public class UpdateDividendJob extends DailyJob {
//  private static final Logger log = LoggerFactory.getLogger(UpdateDailyChartJob.class);
//  
//  private final Gson g; 
//  private final Type type; 
//  
//  public UpdateDividendJob() {
//    super(JobEnum.UPDATE_DIVIDEND);
//    type = new TypeToken<Map<String, Map<String, IexDividendData[]>>>(){}.getType();
//    // We need to register the double type adapter because the amount field can be returned
//    // as an empty string, which shouldn't be.
//    // https://github.com/iexg/IEX-API/issues/173
//    g = new GsonBuilder()        
//      .setLenient()
//      .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
//      .create();
//  }
//  
//  public void doUpdate() throws JobException {
//    List<String> symbols = new ArrayList<>();
//    try {
//      for (String symbol : DownloadHelper.downloadCompanies().keySet()) {
//        symbols.add(symbol);
//        if (symbols.size() == IexConst.MAX_BATCH_SYMBOLS) {
//          batchUpdateDividend(symbols);
//          symbols.clear();        
//        }            
//      }
//      if (symbols.size() > 0) {
//        batchUpdateDividend(symbols);
//      }
//    }
//    catch (Exception e) {
//      throw new JobException(e);
//    }
//  }
//  
//  private void batchUpdateDividend(List<String> symbols) {
//    String url = new IexUrlBuilder()
//        .withBatch()
//        .withSymbols(symbols)
//        .withDividends()
//        .withFiveYears()        
//        .build();
//    log.info(String.format("Getting five years dividends from %s to %s ...",
//      symbols.get(0),
//      symbols.get(symbols.size() - 1)
//    ));    
//    
//    String str = DownloadHelper.downloadURLToString(url);
//    if (str.length() == 0) {
//      log.error("No data downloaded. This should probably not happen.");        
//      return;
//    }
//  
//    Map<String, Map<String, IexDividendData[]>> dataMap = g.fromJson(str, type);    
//    for (String symbol : symbols) {
//      if (!dataMap.containsKey(symbol)) {
//        log.warn(String.format("%s does not seem to be a valid symbol. No data is downloaded. Skipped.", symbol));
//        continue;
//      }
//      try {
////        log.info(String.format("Checking the last updated dividend downloaded for %s ...", symbol));
////        DividendItem lastItem = DynamoDBHelper.getInstance().getLastItem(new DividendItem(symbol), DividendItem.class);
//        List<IexDividendData> dataList = Arrays.asList(dataMap.get(symbol).get("dividends"));
//        dataList = dataList.stream()            
//          .filter(filterInvalidDividend())
//          .collect(Collectors.toList());
//        if (CollectionUtils.isNullOrEmpty(dataList)) {
//          log.info(String.format("No valid dividend data is found for %s. Skipping ...", symbol));
//          continue;
//        }
////        
////        if (lastItem == null) {
////          log.info(String.format("No last updated item is found for %s. Is this a new symbol?", symbol));        
////        }
////        else {
////          log.info(String.format("Last ex-dividend date is %s.", lastItem.getExDate()));
////          dataList = dataList.stream()            
////            .filter(data -> data.getExDate().compareTo(lastItem.getExDate()) > 0)
////            .collect(Collectors.toList());
////          if (dataList.size() == 0) {  
////            log.info(String.format("No dividend data after %s is found for %s. Skipping ...",
////              lastItem.getExDate(), symbol));
////            continue;
////          }
////        }      
//        
//        log.info(String.format("Start saving %d items for %s ...", dataList.size(), symbol));
//        for (IexDividendData data : dataList) {
//          DividendItem item = data.toDividendItem(symbol);        
//          DynamoDBProvider.getInstance().getMapper().save(item);
//        }      
//        log.info(String.format("Done saving %d items for %s.", dataList.size(), symbol));
//      }
//      catch (Exception e) {
//        log.info(String.format("Failed to update dividend for %s: ", symbol));
//      }
//    }
//  }
//  
//  private static Predicate<IexDividendData> filterInvalidDividend() {
//    return data ->
//      StringUtils.isNotEmpty(data.getExDate()) &&
////      StringUtils.isNotEmpty(data.getDeclaredDate()) &&
////      StringUtils.isNotEmpty(data.getPaymentDate()) &&
////      StringUtils.isNotEmpty(data.getRecordDate()) &&
//      data.getAmount() != null &&
//      data.getAmount() != 0;    
//  }
//
//}
