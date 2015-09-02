package aging.POC;


public class SecondNotificationEnforcer extends AgingPolicyEnforcer {

	public SecondNotificationEnforcer() {
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(new String("ThirdNotificationEnforcer"));
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(new Integer(2));
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
}
