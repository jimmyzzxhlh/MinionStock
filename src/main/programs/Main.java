package main.programs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import company.CompanyManager;
import dynamodb.IntraDayChartUpdater;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
	public static void main(String[] args) {
	    log.info("Starting main...");
		CompanyManager.startJob();
	    new IntraDayChartUpdater().startJob();
	}
}
