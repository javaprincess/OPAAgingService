package aging.POC.queue.entry;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import aging.POC.JsonData;
import aging.POC.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("agedUserNotificationEntry")
public class AgedUserNotificationEntry extends AgedUserEntry {

	public AgedUserNotificationEntry() {
		
	}
	
	public AgedUserNotificationEntry(String job) {
		this.job = job;
	}
	
	public String getJob() {
		return this.job;
	} 
	
	public AgedUserEntry createEntry(User user) {
		
		this.id = getNextId();
		this.job = "ENFORCE_WARNING_NOTIFICATION";
		this.jsonData = new JsonData(user);
		this.createDate = Calendar.getInstance().getTime();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return this;
	}



}
