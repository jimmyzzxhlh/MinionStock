package company;

import java.util.HashMap;
import java.util.Map;

public class CompanyEnum {
	
	/**
	 * Adding exchange here should modify the test case of downloading companies as well.
	 * Currently we are not considering AMEX exchange as the companies are not too useful for analysis.
	 * @author jimmyzzxhlh
	 *
	 */
    public enum Exchange {
        NASDAQ,
        NYSE,
    }
   
    public enum Sector {
    	BASIC_INDUSTRIES("Basic Industries"),
    	CAPITAL_GOODS("Capital Goods"),
    	CONSUMER_DURABLES("Consumer Durables"),
    	CONSUMER_NON_DURABLES("Consumer Non-Durables"),
    	CONSUMER_SERVICES("Consumer Services"),
    	ENERGY("Energy"),
    	FINANCE("Finance"),
    	HEALTH_CARE("Health Care"),
    	MISCELLANEOUS("Miscellaneous"),
    	NA("n/a"),
    	PUBLIC_UTILITIES("Public Utilities"),
    	TECHNOLOGY("Technology"),
    	TRANSPORTATION("Transportation");
    	
    	private final String enumStr;
    	private static final Map<String, Sector> map = new HashMap<>();
    	
    	static {
    		for (Sector s : Sector.values()) {
    			map.put(s.enumStr, s);
    		}
    	}
    	
    	private Sector(String enumStr) {
    		this.enumStr = enumStr;
    	}
    	
    	public String toString() {
    		return enumStr;
    	}
    	
    	public static Sector get(String enumStr) {
    		return map.getOrDefault(enumStr, null);
    	}
    }
    
        public enum Industry {
    	ACCIDENT_HEALTH_INSURANCE("Accident &Health Insurance"),
    	ADVERTISING("Advertising"),
    	AEROSPACE("Aerospace"),
    	AGRICULTURAL_CHEMICALS("Agricultural Chemicals"),
    	AIR_DELIVERY_SERVICES("Air Freight_Delivery Services"),
    	ALUMINUM("Aluminum"),
    	APPAREL("Apparel"),
    	AUTO_MANUFACTURING("Auto Manufacturing"),
    	AUTO_PARTS_OEM("Auto Parts:O.E.M."),
    	AUTOMOTIVE_AFTERMARKET("Automotive Aftermarket"),
    	BANKS("Banks"),
    	BEVERAGES("Beverages (Production_Distribution)"),
    	BIO_PRODUCTS("Biotechnology: Biological Products (No Diagnostic Substances)"),
    	BIO_RESEARCH("Biotechnology: Commercial Physical & Biological Resarch"),
    	BIO_APPARATUS("Biotechnology: Electromedical & Electrotherapeutic Apparatus"),
    	BIO_SUBSTANCES("Biotechnology: In Vitro & In Vivo Diagnostic Substances"),
    	BIO_INSTRUMENTS("Biotechnology: Laboratory Analytical Instruments"),
    	BOOKS("Books"),
    	BROADCASTING("Broadcasting"),
    	BUILDING_MATERIALS("Building Materials"),
    	BUILDING_OPERATORS("Building operators"),
    	BUILDING_PRODUCTS("Building Products"),
    	BUSINESS_SERVICES("Business Services"),
    	CATALOG_DISTRIBUTION("Catalog_Specialty Distribution"),
    	CLOTHING_STORES("Clothing_Shoe_Accessory Stores"),
    	COAL_MINING("Coal Mining"),
    	COMMERCIAL_BANKS("Commercial Banks"),
    	COMPUTER_COMMUNICATIONS_EQUIPMENT("Computer Communications Equipment"),
    	COMPUTER_MANUFACTURING("Computer Manufacturing"),
    	COMPUTER_PERIPHERAL_EQUIPMENT("Computer peripheral equipment"),
    	COMPUTER_SOFTWARE_PREPACKAGED("Computer Software: Prepackaged Software"),
    	COMPUTER_SOFTWARE_PROGRAMMING("Computer Software: Programming, Data Processing"),
    	CONSTRUCTION("Construction_Ag Equipment_Trucks"),
    	CONSUMER_ELECTRONICS_APPLIANCES("Consumer Electronics_Appliances"),
    	CONSUMER_ELECTRONICS_VIDEO_CHAINS("Consumer Electronics_Video Chains"),
    	CONSUMER_SPECIALTIES("Consumer Specialties"),
    	CONSUMER_GREETING_CARDS("Consumer: Greeting Cards"),
    	CONTAINERS_PACKAGING("Containers_Packaging"),
    	DEPARTMENT_STORES("Department_Specialty Retail Stores"),
    	DIVERSIFIED_COMMERCIAL_SERVICES("Diversified Commercial Services"),
    	DIVERSIFIED_ELECTRONIC_PRODUCTS("Diversified Electronic Products"),
    	DIVERSIFIED_FINANCIAL_SERVICES("Diversified Financial Services"),
    	EDP_SERVICES("EDP Services"),
    	ELECTRIC_UTILITIES_CENTRAL("Electric Utilities: Central"),
    	ELECTRIC_PRODUCTS("Electrical Products"),
    	ELECTRONIC_COMPONENTS("Electronic Components"),
    	ELECTRONICS_DISTRIBUTION("Electronics Distribution"),
    	ENGINEERING_CONSTRUCTION("Engineering & Construction"),
    	ENVIRONMENTAL_SERVICES("Environmental Services"),
    	FARMING("Farming_Seeds_Milling"),
    	FINANCE_COMPANIES("Finance Companies"),
    	FINANCE_INVESTORS_SERVICES("Finance_Investors Services"),
    	FINANCE_CONSUMER_SERVICES("Finance: Consumer Services"),
    	FLUID_CONTROLS("Fluid Controls"),
    	FOOD_CHAINS("Food Chains"),
    	FOOD_DISTRIBUTORS("Food Distributors"),
    	FOREST_PRODUCTS("Forest Products"),
    	GENERAL_BLDG_CONTRACTORS("General Bldg Contractors _ Nonresidential Bldgs"),
    	HOME_FURNISHINGS("Home Furnishings"),
    	HOMEBUILDING("Homebuilding"),
    	HOSPITAL_MANAGEMENT("Hospital_Nursing Management"),
    	HOTELS("Hotels_Resorts"),
    	INDUSTRIAL_MACHINERY("Industrial Machinery_Components"),
    	INDUSTRIAL_SPECIALTIES("Industrial Specialties"),
    	INTEGRATED_OIL_COMPANIES("Integrated oil Companies"),
    	INVESTMENT_BANKERS("Investment Bankers_Brokers_Service"),
    	INVESTMENT_MANAGERS("Investment Managers"),
    	LIFE_INSURANCE("Life Insurance"),
    	MAJOR_BANKS("Major Banks"),
    	MAJOR_CHEMICALS("Major Chemicals"),
    	MAJOR_PHARMACEUTICALS("Major Pharmaceuticals"),
    	MARINE_TRANSPORTATION("Marine Transportation"),
    	MEAT("Meat_Poultry_Fish"),
    	MEDICAL_ELECTRONICS("Medical Electronics"),
    	MEDICAL_SPECIALITIES("Medical Specialities"),
    	MEDICAL_DENTAL_INSTRUMENTS("Medical_Dental Instruments"),
    	MEDICAL_NURSING_SERVICES("Medical_Nursing Services"),
    	METAL_FABRICATIONS("Metal Fabrications"),
    	MILITARY_GOVERNMENT_TECHNICAL("Military_Government_Technical"),
    	MINING("Mining & Quarrying of Nonmetallic Minerals (No Fuels)"),
    	MISCELLANEOUS("Miscellaneous"),
    	MISCELLANEOUS_MANUFACTURING_INDUSTRIES("Miscellaneous manufacturing industries"),
    	MOTOR_VEHICLES("Motor Vehicles"),
    	MOVIES_ENTERTAINMENT("Movies_Entertainment"),
    	MULTI_SECTOR_COMPANIES("Multi_Sector Companies"),
    	NA("n_a"),
    	NATURAL_GAS_DISTRIBUTION("Natural Gas Distribution"),
    	NEWSPAPERS_MAGAZINES("Newspapers_Magazines"),
    	OFFICE_EQUIPMENT("Office Equipment_Supplies_Services"),
    	OIL_GAS_PRODUCTION("Oil & Gas Production"),
    	OIL_REFINING_MARKETING("Oil Refining_Marketing"),
    	OIL_GAS_TRANSMISSION("Oil_Gas Transmission"),
    	OILFIELD_SERVICES_EQUIPMENT("Oilfield Services_Equipment"),
    	OPHTHALMIC_GOODS("Ophthalmic Goods"),
    	ORDNANCE_AND_ACCESSORIES("Ordnance And Accessories"),
    	OTHER_CONSUMER_SERVICES("Other Consumer Services"),
    	OTHER_PHARMACEUTICALS("Other Pharmaceuticals"),
    	OTHER_SPECIALTY_STORES("Other Specialty Stores"),
    	OTHER_TRANSPORTATION("Other Transportation"),
    	PACKAGE_GOODS_COSMETICS("Package Goods_Cosmetics"),
    	PACKAGED_FOODS("Packaged Foods"),
    	PAINTS_COATINGS("Paints_Coatings"),
    	PAPER("Paper"),
    	PLASTIC_PRODUCTS("Plastic Products"),
    	POLLUTION_CONTROL_EQUIPMENT("Pollution Control Equipment"),
    	POWER_GENERATION("Power Generation"),
    	PRECIOUS_METALS("Precious Metals"),
    	PRECISION_INSTRUMENTS("Precision Instruments"),
    	PROFESSIONAL_SERVICES("Professional Services"),
    	PROPERTY_CASUALTY_INSURERS("Property_Casualty Insurers"),
    	PUBLISHING("Publishing"),
    	RADIO_TV_EQUIPMENT("Radio And Television Broadcasting And Communications Equipment"),
    	RAILROADS("Railroads"),
    	REAL_ESTATE("Real Estate"),
    	REAL_ESTATE_INVESTMENT_TRUSTS("Real Estate Investment Trusts"),
    	RECREATIONAL_PRODUCTS_TOYS("Recreational Products_Toys"),
    	RENTAL_LEASING_COMPANIES("Rental_Leasing Companies"),
    	RESTAURANTS("Restaurants"),
    	RETAIL_BUILDING_MATERIALS("RETAIL: Building Materials"),
    	RETAIL_COMPUTER_SOFTWARE("Retail: Computer Software & Peripheral Equipment"),
    	SAVINGS_INSTITUTIONS("Savings Institutions"),
    	SEMICONDUCTORS("Semiconductors"),
    	SERVICES_AMUSEMENT_RECREATION("Services_Misc. Amusement & Recreation"),
    	SHOE_MANUFACTURING("Shoe Manufacturing"),
    	SPECIALTY_CHEMICALS("Specialty Chemicals"),
    	SPECIALTY_FOODS("Specialty Foods"),
    	SPECIALTY_INSURERS("Specialty Insurers"),
    	STEEL_IRON_ORE("Steel_Iron Ore"),
    	TELECOMMUNICATIONS_EQUIPMENT("Telecommunications Equipment"),
    	TELEVISION_SERVICES("Television Services"),
    	TEXTILES("Textiles"),
    	TOBACCO("Tobacco"),
    	TOOLS_HARDWARE("Tools_Hardware"),
    	TRANSPORTATION_SERVICES("Transportation Services"),
    	TRUCKING_COURIER_SERVICES("Trucking Freight_Courier Services"),
    	WATER_SUPPLY("Water Supply"),
    	WHOLESALE_DISTRIBUTORS("Wholesale Distributors");

    	private final String enumStr;
    	private static final Map<String, Industry> map = new HashMap<>();
    	
    	static {
    		for (Industry s : Industry.values()) {
    			map.put(s.enumStr, s);
    		}
    	}
    	
    	private Industry(String enumStr) {
    		this.enumStr = enumStr;
    	}
    	
    	public String toString() {
    		return enumStr;
    	}
    	
    	public static Industry get(String enumStr) {
    		return map.getOrDefault(enumStr, null);
    	}
    }
}
