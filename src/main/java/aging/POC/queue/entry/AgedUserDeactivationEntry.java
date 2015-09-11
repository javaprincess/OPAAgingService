package aging.POC.queue.entry;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import aging.POC.JsonData;
import aging.POC.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("agedUserDeactivationEntry")
public class AgedUserDeactivationEntry extends AgedUserEntry {
	
	public AgedUserDeactivationEntry() {
		
	}
	
	public AgedUserDeactivationEntry(String job) {
		this.job = job;
	}
	
	public String getJob() {
		return this.job;
	}
	
	public AgedUserEntry createEntry(User user) {
		this.id = getNextId();
		this.job = "ENFORCE_EXPIRY_NOTIFICATION";
		this.createDate = Calendar.getInstance().getTime();
		
		this.jsonData = new JsonData(user);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return this;
	}

}
