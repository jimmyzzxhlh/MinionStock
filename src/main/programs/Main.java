package main.programs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.CompanyUpdater;
import dynamodb.DailyChartUpdater;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
	public static void main(String[] args) {
	    log.info("Starting main...");
		new CompanyUpdater().startJob();
	    new DailyChartUpdater().startJob();
	}
}
