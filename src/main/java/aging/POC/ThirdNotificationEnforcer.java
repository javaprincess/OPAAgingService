package aging.POC;


public class ThirdNotificationEnforcer extends AgingPolicyEnforcer {

	public ThirdNotificationEnforcer() {
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(new String("FourthNotificationEnforcer"));
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(new Integer(3));
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
