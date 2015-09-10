package aging.POC.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum OPAComplianceAgingEnum {
	
	DELTA_1("OPA_COMPLIANCE_AGING_DELTA_1"),
	DELTA_2("OPA_COMPLIANCE_AGING_DELTA_2"),
	DELTA_3("OPA_COMPLIANCE_AGING_DELTA_3"),
	DELTA_4("OPA_COMPLIANCE_AGING_DELTA_4"),
	LICENSEE("2");
	
	private String value;

	private OPAComplianceAgingEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	private void setValue(String value) {
		this.value = value;
	}
	
	public static void initialize() {
		try (InputStream in = new FileInputStream("C:\\was_data\\websphere8\\Opa1\\config\\ProdApp\\properties\\prodapp.properties")) {
			Properties prop = new Properties();
			prop.load(in);
			
			for (OPAComplianceAgingEnum property : values()) 
				property.setValue(prop.getProperty(property.getValue()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
