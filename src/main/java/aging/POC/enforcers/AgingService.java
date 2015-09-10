package aging.POC.enforcers;



import java.util.Arrays;
import javax.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import aging.POC.util.OPAComplianceAgingConstants;
import aging.POC.enforcers.factory.AgingPolicyEnforcerFactory;


@SpringBootApplication
public class AgingService implements CommandLineRunner {
	
	
	@Resource
	public ApplicationContext ctx;
	
	@Resource(name="enforcerFactory")
	private AgingPolicyEnforcerFactory enforcerFactory;
	
	@Resource
	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}
	
	public AgingService() {
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AgingService.class, args);
		
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		OPAComplianceAgingConstants.initialize();
		
		//printOutAllSpringBeans();
	
		AgingPolicyEnforcer enforcer = AgingPolicyEnforcerFactory.getInstance(ctx).getEnforcer("scanner");
		//AgingPolicyEnforcer enforcer = AgingPolicyEnforcerFactory.getInstance(ctx).getEnforcer("firstNotificationEnforcer");
		//AgingPolicyEnforcer enforcer = AgingPolicyEnforcerFactory.getInstance(ctx).getEnforcer("secondNotificationEnforcer");
		//AgingPolicyEnforcer enforcer = AgingPolicyEnforcerFactory.getInstance(ctx).getEnforcer("thirdNotificationEnforcer");
		//AgingPolicyEnforcer enforcer = AgingPolicyEnforcerFactory.getInstance(ctx).getEnforcer("fourthNotificationEnforcer");
		
		enforcer.enforcePolicy();
	}
		
	private void printOutAllSpringBeans() {
		System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
		//Map<String, AgingPolicyEnforcer> beanNames = ctx.getBeansOfType(AgingPolicyEnforcer.class);
		
		//Set<Entry<String, AgingPolicyEnforcer>> beanNameSet = beanNames.entrySet();
		
		//System.out.println(beanNames.size());
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
		//for (Entry<String, AgingPolicyEnforcer> beanName : beanNameSet) {
            //System.out.println(beanName.getValue());
        	System.out.println(beanName);
        }
	}

}
