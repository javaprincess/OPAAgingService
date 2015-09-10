package aging.POC.queue.entry;

import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aging.POC.JsonData;
import aging.POC.OPAUniqueId;
import aging.POC.User;

public abstract class AgedUserEntry {

	@Id
	protected long id;
	protected String job;
	protected final String inUseBy = "OPA_Compliance_User_Aging_Service";
	protected String entryName;
	protected JsonData jsonData;
	protected Date createDate;
	
	//public abstract AgedUserEntry createEntry(User user);
	public static <T extends AgedUserEntry> T createEntry(User user, T entry) {
		
		entry.job = entry.getJob();
		entry.id = entry.getNextId();
		//this.inUseBy = "OPA_Compliance_User_Aging_Service";
		entry.createDate = Calendar.getInstance().getTime();
		
		entry.jsonData = new JsonData(user);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(entry.jsonData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return entry;
	}
	
	public long getNextId() { return OPAUniqueId.getInstance().getNextId();}
	public long getId() {return id; }
	public String getInUseBy() {return inUseBy; }
	public abstract String getJob();
	public String getEntryName() { return entryName; }
	public JsonData getJsonData() {return jsonData; }
	public Date getCreateDate() {return createDate;}
	
}
