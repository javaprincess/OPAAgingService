package aging.POC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AgedUserNotificationEntry extends AgedUserEntry {

	
	public AgedUserEntry createEntry(User user) {
		
		
		this.id = getNextId();
		this.inUseBy = "tracy"; //should come from User.name
		this.job = "ENFORCE_NOTIFICATION_POLICY";
		this.jsonData = new JsonData(user);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}



}
