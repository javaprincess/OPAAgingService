package aging.POC.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum OPAComplianceAgingConstants {
	
	DELTA_1("OPA_COMPLIANCE_AGING_DELTA_1"),
	DELTA_2("OPA_COMPLIANCE_AGING_DELTA_2"),
	DELTA_3("OPA_COMPLIANCE_AGING_DELTA_3"),
	DELTA_4("OPA_COMPLIANCE_AGING_DELTA_4");
	
	private String notificationDelta;

	private OPAComplianceAgingConstants(String notificationDelta) {
		this.notificationDelta = notificationDelta;
	}
	
	public String getNotificationDelta() {
		return this.notificationDelta;
	}
	
	private void setNotificationDelta(String notificationDelta) {
		this.notificationDelta = notificationDelta;
	}
	
	public static void initialize() {
		try (InputStream in = new FileInputStream("C:\\was_data\\websphere8\\Opa1\\config\\ProdApp\\properties\\prodapp.properties")) {
			Properties prop = new Properties();
			prop.load(in);
			
			for (OPAComplianceAgingConstants property : values()) 
				property.setNotificationDelta(prop.getProperty(property.getNotificationDelta()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
