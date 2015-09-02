package aging.POC;

public class AgedUserDeactivationEntry extends AgedUserEntry {

	
	public AgedUserEntry createEntry(User user) {
		this.id = getNextId();
		this.inUseBy = "tracy"; //should come from User.name
		this.job = "ENFORCE_DEACTIVIATION_POLICY";
		
		return this;
	}

}
