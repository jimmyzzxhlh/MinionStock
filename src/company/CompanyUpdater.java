package company;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import download.DownloadHelper;
import dynamodb.DynamoDBHelper;
import dynamodb.DynamoDBProvider;
import dynamodb.Status;
import dynamodb.item.CompanyItem;
import enums.JobEnum;
import util.CommonUtil;

public class CompanyUpdater {
    private static final int HOUR = 16;
    private static final int MINUTE = 0;
    private static final Logger log = LoggerFactory.getLogger(CompanyUpdater.class);
    
    /**
     * Update companies from Nasdaq.
     */
    private void updateCompanies() {
        log.info("Checking if the companies have been updated in the last 24 hours ...");
        Status status = DynamoDBHelper.getInstance().getStatusItem(JobEnum.UPDATE_COMPANY).toStatus();        
        if (CommonUtil.getPacificTimeNow().isBefore(status.getLastUpdatedTime().plusDays(1))) {
            log.info("Companies have been updated at " + status.getLastUpdatedTime() + ". Skipping the update.");
            return;
        }
        
        log.info("Start updating companies from Nasdaq ...");
        log.info("Reading existing companies from DynamoDB ...");
        Map<String, Company> existingCompaniesMap = DynamoDBHelper.getInstance().getCompaniesMap();
        log.info("Downloading new companies from Nasdaq ...");
        Map<String, Company> newCompaniesMap = DownloadHelper.downloadCompanies();
        
        // Remove symbols that already exist.
        for (String symbol : existingCompaniesMap.keySet()) {
            if (newCompaniesMap.containsKey(symbol)) {
                newCompaniesMap.remove(symbol);
            }
        }
        
        if (newCompaniesMap.size() == 0) {
            log.info("No new companies found.");
            saveStatus(status);
            return;
        }
        
        log.info("Found " + newCompaniesMap.size() + " new companies. Saving them to DynamoDB ...");        
        for (Company company : newCompaniesMap.values()) {
            CompanyItem item = company.toCompanyItem();
            DynamoDBProvider.getInstance().getMapper().save(item);
        }
        log.info("Done saving " + newCompaniesMap.size() + " new companies.");
        saveStatus(status);        
    }
    
    private void saveStatus(Status status) {
        status.setLastUpdatedTime(CommonUtil.getPacificTimeNow());
        DynamoDBProvider.getInstance().getMapper().save(status.toStatusItem());
        log.info("Next update for companies will be at " + CommonUtil.getTime(HOUR, MINUTE) + ".");
    }
    
    public void startJob() {
        updateCompanies();        
        CommonUtil.scheduleDailyJob(() -> {        
            updateCompanies();
        }, HOUR, MINUTE);        
    }
}
