package aging.POC.queue.entry;

import java.util.Date;

import org.springframework.data.annotation.Id;

import aging.POC.User;
import aging.POC.deleteThis.JsonData;
import aging.POC.deleteThis.OPAUniqueId;

public abstract class AgedUserEntry {

	@Id
	protected long id;
	
	protected String inUseBy;
	protected String job;
	protected String entryName;
	protected JsonData jsonData;
	protected Date createDate;
	
	public abstract AgedUserEntry createEntry(User user);
	
	public long getNextId() { return OPAUniqueId.getInstance().getNextId();}
	public long getId() {return id; }
	public String getInUseBy() {return inUseBy; }
	public String getJob() { return job; }
	public String getEntryName() { return entryName; }
	public JsonData getJsonData() {return jsonData; }
	public Date getCreateDate() {return createDate;}
	
}
