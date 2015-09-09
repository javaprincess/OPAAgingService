package aging.POC.enforcers.factory;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import aging.POC.enforcers.AgedUserEntryRepository;
import aging.POC.enforcers.AgingPolicyEnforcer;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;

@Component("enforcerFactory")
public  class AgingPolicyEnforcerFactory {
	
	//TODO: all DI candidates
	@Autowired
	protected AgedUserEntryRepository auRepo;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	protected BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	protected ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;
	protected BulkUserDeactivateSP bulkUserDeactivateSP;
	protected FindUserAgingCandidatesSP findUserAgingCandidatesSP;
	protected static AgingPolicyEnforcerFactory enforcerFactory = new AgingPolicyEnforcerFactory();
	protected ConcurrentHashMap enforcerMap = new ConcurrentHashMap();
	
	private static ApplicationContext ctx;
	
	private AgingPolicyEnforcerFactory() {
		
	}
	
	/*public void initialize(AgedUserEntryRepository auRepo,
			JdbcTemplate jdbcTemplate,
			BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP,
			ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP,
			BulkUserDeactivateSP bulkUserDeactivateSP,
			FindUserAgingCandidatesSP findUserAgingCandidatesSP){
		
		this.auRepo = auRepo;
		this.jdbcTemplate = jdbcTemplate;
		this.bulkUserNotificationFlagUpdateSP = bulkUserNotificationFlagUpdateSP;
		this.productsUserIsInvolvedWithSP = productsUserIsInvolvedWithSP;
		this.findUserAgingCandidatesSP = findUserAgingCandidatesSP;
		
		//load your enforcers
		
	}*/
	
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
