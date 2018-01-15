package main.programs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.job.UpdateDividendJob;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
	public static void main(String[] args) {
	    log.info("Starting main...");
//	    new UpdateCapacityJob().startJob();
//		new UpdateCompanyJob().startJob();
//	    new UpdateDailyChartJob().startJob();
	    new UpdateDividendJob().startJob();
	}
}
