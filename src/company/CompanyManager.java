package company;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import download.DownloadHelper;
import util.CommonUtil;

public class CompanyManager {    
    private Map<String, Company> companiesMap;
    private static CompanyManager instance;
    
    private CompanyManager() {
        initCompaniesMap();
        CommonUtil.scheduleDailyJob(() -> {        
            this.companiesMap.clear();
            initCompaniesMap();
        });        
    }
    
    public static CompanyManager getInstance() {
        if (instance == null) {
            instance = new CompanyManager();                  
        }
        return instance;
    }
    
    public static void startJob() {
        getInstance();
    }
    
    private void initCompaniesMap() {        
        this.companiesMap = DownloadHelper.downloadCompanies();        
    }
    
    public Map<String, Company> getCompaniesMap() {
        return this.companiesMap;
    }
}
