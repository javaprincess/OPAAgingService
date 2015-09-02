package aging.POC;


import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class AgingPolicyEnforcer {

	protected String enforcerName = null;
	protected String nextEnforcerToCall = null;
	protected AgingPolicy agingPolicy = null;
	protected final String JOB = "ENFORCER_AGING_POLICY";
	protected AgingPolicyTarget agingPolicyTarget = null;
	protected Integer notificationStatus = null;
	
	public abstract void setEnforcerName(String name);
	public abstract void setAgingPolicyTarget(AgingPolicyTarget target);
	public abstract void setNotificationStatus(Integer notification);
	public abstract void setAgingPolicy(AgingPolicy policy);
	public abstract void setNextEnforcerToCall(String nextEnforcer);
	
	protected void enforcePolicy() throws AgingPolicyEnforcementException  {
		
		if (isValidAgingCandidate()) {
			//AgedUserNotificationEntry notificationEntry = (AgedUserNotificationEntry) new AgedUserNotificationEntry().createEntry(new User(null, null));
			sendMail();
		} /*else if (isBetweenNotifications()) {
			
		} else if (isNoLongerValidAgingCandidate()) {
			
		} */
	}
	
	protected ObjectMapper createJSONObject() {
		ObjectMapper mapper = new ObjectMapper();
		
		
		return mapper;
	}
	
	protected boolean isBetweenNotifications() {
		boolean isBetweenNotifications = false;
		
		return isBetweenNotifications;
	}
	
	protected boolean isNoLongerValidAgingCandidate() {
		boolean isNoLongerValidAgingCandidate = false;
		
		return isNoLongerValidAgingCandidate;
	}
	
	protected boolean isValidAgingCandidate() {
		boolean isValidAgingCandidate = false;
		
		return isValidAgingCandidate;
	}
	
	//protected abstract boolean isValidAgingCandidate();
	
	protected void updateAgedUserEntity(ObjectMapper mapper) {
		
	}
	
	protected void sendMail() {
		
	}
	
	public Integer getNotificationStatus() {
		return notificationStatus;
	}
	
	
	
	public String getEnforcerName() {
		return enforcerName;
	}
	
	

}
