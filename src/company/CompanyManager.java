package company;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import download.DownloadHelper;

public class CompanyManager {    
    private Map<String, Company> companiesMap;
    private static CompanyManager instance;
    
    private CompanyManager() {
        initCompaniesMap();
        ScheduledExecutorService executorService = 
            Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            this.companiesMap.clear();
            initCompaniesMap();
        }, 24, 24, TimeUnit.HOURS);        
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
