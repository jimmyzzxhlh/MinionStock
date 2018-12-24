package main.job.daily;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.Company;
import download.DownloadHelper;
import dynamodb.DynamoDBHelper;
import dynamodb.DynamoDBProvider;
import dynamodb.item.CompanyItem;
import exceptions.JobException;
import main.job.JobEnum;

public class UpdateCompanyJob extends DailyJob {
  private static final Logger log = LoggerFactory.getLogger(UpdateCompanyJob.class);

  public UpdateCompanyJob() {
    super(JobEnum.UPDATE_COMPANY);
  }
  
  public void doUpdate() throws JobException {
    try {
      log.info("Reading existing company list from DynamoDB ...");
      Map<String, Company> existingCompaniesMap = DynamoDBHelper.getInstance().getCompaniesMap();
      log.info("Downloading new company list from Nasdaq ...");
      Map<String, Company> newCompaniesMap = DownloadHelper.downloadCompanies();
      
      // Remove symbols that already exist.
      for (String symbol : existingCompaniesMap.keySet()) {
        if (newCompaniesMap.containsKey(symbol)) {
          newCompaniesMap.remove(symbol);
        }
      }
      
      if (newCompaniesMap.size() == 0) {
        log.info("No new company found.");      
        return;
      }
      
      log.info("Found " + newCompaniesMap.size() + " new companies. Saving them to DynamoDB ...");    
      for (Company company : newCompaniesMap.values()) {
        CompanyItem item = company.toCompanyItem();
        DynamoDBProvider.getInstance().getMapper().save(item);
      }
    }
    catch (Exception e) {
      throw new JobException(e);
    }
  }  
}
