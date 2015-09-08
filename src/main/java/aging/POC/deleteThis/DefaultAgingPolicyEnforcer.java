package aging.POC.deleteThis;



public class DefaultAgingPolicyEnforcer extends AgingPolicyEnforcer {

	public DefaultAgingPolicyEnforcer() {
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(new String("FirstNotificationEnforcer"));
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(new Integer(0));
	}
	
	public void setEnforcerName(String name) {
		this.enforcerName = name;
	}
	
	public void setAgingPolicyTarget(AgingPolicyTarget target) {
		this.agingPolicyTarget = target;
	}
	
	public void setNotificationStatus(Integer notification) {
		this.notificationStatus = notification;
	}
	
	public void setAgingPolicy(AgingPolicy policy) {
		this.agingPolicy = policy;
	}
	
	public void setNextEnforcerToCall(String nextEnforcer) {
		this.nextEnforcerToCall = nextEnforcer;
	}

	@Override
	protected void enforcePolicy() throws AgingPolicyEnforcementException {
		//1. get users that match the >90 login rule (agingPolicy for the defaultEnforcer for NorthAmerica)
		//2. if (matchIsNotInOPAQueue())
		//	  a. ObjectMapper objectMapper = createJSONObject();
		//	  b. createAgedUserEntry(objectMapper)
		// 	  c. update user.notificationFlag
		//3. private void createAgedUserEntry(ObjectMapper objectMapper) {
		//     
		
	}

}
