package aging.POC;

import org.springframework.beans.factory.annotation.Value;

//This is a configuration class 
//all vars should come from an xml file
public class AgingPolicy {
	
	private AgingPolicyEnforcer enforcer = null;
	
	@Value("$defaultAgingPolicy")
	private Integer defaultAgingPolicy;
	
	@Value("${daysSinceFirstNotification}")
	private Integer daysSinceFirstNotification;
	
	@Value("${daysSinceSecondNotification}")
	private Integer daysSinceSecondNotification;
	
	@Value("${daysSinceThirdNotification}")
	private Integer daysSinceThirdNotification;
	
	@Value("${daysSinceFourthNotification}")
	private Integer daysSinceFourthNotification;
	
	public AgingPolicy() {
		
	}
	
	public AgingPolicy(AgingPolicyEnforcer enforcer) {
		this.enforcer = enforcer;
	}
	
	public AgingPolicyEnforcer getEnforcer() {
		return this.enforcer;
	}
	
	public void setEnforcer(AgingPolicyEnforcer enforcer) {
		this.enforcer = enforcer;
	}
	
	public Integer getDefaultAgingPolicy() {
		return defaultAgingPolicy;
	}
	
	public void setDefaultAgingPolicy(Integer defaultAgingPolicy) {
		this.defaultAgingPolicy = defaultAgingPolicy;
	}
	
	public Integer getDaysSinceFirstNotification() {
		return daysSinceFirstNotification;
	}

	public void setDaysSinceFirstNotification(Integer daysSinceFirstNotification) {
		this.daysSinceFirstNotification = daysSinceFirstNotification;
	}

	public Integer getDaysSinceSecondNotification() {
		return daysSinceSecondNotification;
	}

	public void setDaysSinceSecondNotification(Integer daysSinceSecondNotification) {
		this.daysSinceSecondNotification = daysSinceSecondNotification;
	}

	public Integer getDaysSinceThirdNotification() {
		return daysSinceThirdNotification;
	}

	public void setDaysSinceThirdNotification(Integer daysSinceThirdNotification) {
		this.daysSinceThirdNotification = daysSinceThirdNotification;
	}

	public Integer getDaysSinceFourthNotification() {
		return daysSinceFourthNotification;
	}

	public void setDaysSinceFourthNotification(Integer daysSinceFourthNotification) {
		this.daysSinceFourthNotification = daysSinceFourthNotification;
	}

}
