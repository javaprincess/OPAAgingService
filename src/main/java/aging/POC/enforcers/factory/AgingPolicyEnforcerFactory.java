package aging.POC.enforcers.factory;


import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import aging.POC.enforcers.AgingPolicyEnforcer;


@Component("enforcerFactory")
public  class AgingPolicyEnforcerFactory {
	

	private static AgingPolicyEnforcerFactory enforcerFactory = new AgingPolicyEnforcerFactory();
	private static ApplicationContext ctx;
	
	private AgingPolicyEnforcerFactory() {
		
	}

	
	public static AgingPolicyEnforcerFactory getInstance(ApplicationContext applicationContext) {
		ctx = applicationContext;
		
		if (enforcerFactory==null)
			return new AgingPolicyEnforcerFactory();
		else
			return enforcerFactory;
	}
	
	public AgingPolicyEnforcer getEnforcer(String enforcerName) {
		return (AgingPolicyEnforcer)ctx.getBean(enforcerName);
	}

}
