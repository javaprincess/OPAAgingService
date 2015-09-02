package aging.POC;


public class FourthNotificationEnforcer extends AgingPolicyEnforcer {
	
	private final AgingPolicyDeactivationTx agingPolicyDeactivationTx = new AgingPolicyDeactivationTx();

	public FourthNotificationEnforcer() {
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(null);
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(4);
	}

	@Override
	protected void enforcePolicy() throws AgingPolicyEnforcementException {
		//User user = null;
		super.enforcePolicy();
		//agingPolicyDeactivationTx.deactivate(user);
		
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
