package test;

import aurora.AuroraDAO;
import aurora.AuroraDAOInterface;

public class Test {
	public static void main(String[] args) {
		AuroraDAOInterface aurora = new AuroraDAO();
		System.out.println(aurora.companyExists("CAMT"));
	}
}
