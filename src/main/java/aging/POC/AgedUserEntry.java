package aging.POC;

import org.springframework.data.annotation.Id;

public abstract class AgedUserEntry {

	@Id
	protected long id;
	
	protected String inUseBy;
	protected String job;
	protected String entryName;
	protected JsonData jsonData;

	public abstract AgedUserEntry createEntry(User user);
	
	public long getNextId() { return OPAUniqueId.getInstance().getNextId();}
	public long getId() {return id; }
	public String getInUseBy() {return inUseBy; }
	public String getJob() { return job; }
	public String getEntryName() { return entryName; }
	public JsonData getJsonData() {return jsonData; }
	
}
