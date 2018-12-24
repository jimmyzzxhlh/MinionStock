package main.job.daily;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import download.DownloadHelper;
import download.iex.IexDailyData;
import download.iex.IexConst;
import download.iex.IexUrlBuilder;
import dynamodb.DynamoDBProvider;
import dynamodb.item.DailyItem;
import exceptions.JobException;
import main.job.JobEnum;
import util.CommonUtil;

public class UpdateDailyChartJob extends DailyJob {  
  private static final Logger log = LoggerFactory.getLogger(UpdateDailyChartJob.class);
  
  private final Gson g = new GsonBuilder().setLenient().create();
  private final Type type = new TypeToken<Map<String, Map<String, IexDailyData[]>>>(){}.getType();
  
  public UpdateDailyChartJob() {
    super(JobEnum.UPDATE_DAILY_CHART);     
  }
  
  @Override
  public void doUpdate() throws JobException {
    try {      
      List<String> symbols = new ArrayList<>(); 
  
      for (String symbol : DownloadHelper.downloadCompanies().keySet()) {        
        symbols.add(symbol);
        if (symbols.size() == IexConst.MAX_BATCH_SYMBOLS) {
          batchUpdateDailyChart(symbols);
          symbols.clear();        
        }            
      }
      if (symbols.size() > 0) {
        batchUpdateDailyChart(symbols);
      }
    }
    catch (Exception e) {
      throw new JobException(e);
    }
  }
  
  private void batchUpdateDailyChart(List<String> symbols) {
    String url = new IexUrlBuilder()
        .withBatch()
        .withSymbols(symbols)
        .withChart()
        .withOneMonth()        
        .build();
    log.info(String.format("Getting one month daily chart from %s to %s ...",
      symbols.get(0),
      symbols.get(symbols.size() - 1)
    ));    
    
    String str = DownloadHelper.downloadURLToString(url);
    if (str.length() == 0) {
      log.error("No data downloaded. This should probably not happen.");        
      return;
    }
  
    Map<String, Map<String, IexDailyData[]>> dataMap = g.fromJson(str, type);    
    for (String symbol : symbols) {
      if (!dataMap.containsKey(symbol)) {
        log.warn(String.format("%s does not seem to be a valid symbol. No data is downloaded. Skipped.", symbol));
        continue;
      }
      try {        
        List<IexDailyData> dataList = Arrays.asList(dataMap.get(symbol).get("chart"));
        IexDailyData data = dataList.get(dataList.size() - 1);
        if (!data.getDate().equals(CommonUtil.getPacificDateNow())) {
          log.warn("The data is not today's data! Skipped and not saved: " + data);
          continue;
        }        
        log.info(String.format("Start saving item for %s on %s ...", symbol, data.getDate()));
        DailyItem item = data.toDailyItem(symbol);        
        DynamoDBProvider.getInstance().getMapper().save(item);
        log.info(String.format("Done saving item for %s on %s.", symbol, data.getDate()));
      }
      catch (Exception e) {
        log.error("Failed to update daily chart for " + symbol + ": ", e);
      }
    }    
  }
}
