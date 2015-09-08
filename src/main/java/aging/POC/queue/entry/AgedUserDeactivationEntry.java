package aging.POC.queue.entry;

import java.util.Calendar;

import aging.POC.User;
import aging.POC.deleteThis.JsonData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgedUserDeactivationEntry extends AgedUserEntry {

	
	public AgedUserEntry createEntry(User user) {
		this.id = getNextId();
		this.inUseBy = "tracy"; //should come from User.name
		this.job = "ENFORCE_DEACTIVIATION_POLICY";
		this.createDate = Calendar.getInstance().getTime();
		
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
