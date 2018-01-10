package main.job;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.Company;
import download.DownloadHelper;
import dynamodb.DynamoDBHelper;
import dynamodb.DynamoDBProvider;
import dynamodb.Status;
import dynamodb.item.CompanyItem;
import util.CommonUtil;

public class UpdateCompanyJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(UpdateCompanyJob.class);

    @Override
    public void startJob() {
        updateCompanies();
        
        JobUtil.scheduleDailyJob(() -> {        
            updateCompanies();
        }, JobEnum.UPDATE_COMPANY);        
    }
    
    /**
     * Update companies from Nasdaq.
     */
    private void updateCompanies() {
        log.info("Checking if the companies have been updated today ...");
        Status status = DynamoDBHelper.getInstance().getStatusItem(JobEnum.UPDATE_COMPANY).toStatus();        
        if (status.isUpdatedToday()) {
            log.info(String.format("Companies have been updated from %s to %s. Update skipped.",
                status.getLastStartTime(), status.getLastEndTime()));
            return;
        }
        else {
            log.info(String.format("Companies were updated at %s which is more than one day ago.",
                status.getLastStartTime()));
        }
        
        log.info("Start updating companies from Nasdaq ...");
        status.setLastStartTime(CommonUtil.getPacificTimeNow());
        status.setJobStatus(JobStatusEnum.UPDATING);
        DynamoDBHelper.getInstance().saveStatus(status);
        
        try {
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
                status.setJobStatus(JobStatusEnum.DONE);
                DynamoDBHelper.getInstance().saveStatus(status);
                return;
            }
            
            log.info("Found " + newCompaniesMap.size() + " new companies. Saving them to DynamoDB ...");        
            for (Company company : newCompaniesMap.values()) {
                CompanyItem item = company.toCompanyItem();
                DynamoDBProvider.getInstance().getMapper().save(item);
            }            
            log.info("Done saving " + newCompaniesMap.size() + " new companies.");
            status.setJobStatus(JobStatusEnum.DONE);
            DynamoDBHelper.getInstance().saveStatus(status);
        }
        catch (Exception e) {
            log.error("Failed to update companies.", e);
            status.setJobStatus(JobStatusEnum.FAILED);
            DynamoDBHelper.getInstance().saveStatus(status);
        }
    }
}
