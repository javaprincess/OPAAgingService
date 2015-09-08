package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;


@SpringBootApplication
public class AgingPolicyEnforcer implements CommandLineRunner {

	
	@Autowired
	private AgedUserEntryRepository auRepo;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate; 
	
	protected FindUserAgingCandidatesSP findUserAgingCandidatesSP; 
	protected  BulkUserDeactivateSP bulkUserDeactivateSP;
	protected  BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	protected  ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;
	
	
	
	
	@Autowired
	public void setDataSource(DataSource source){ 
		this.jdbcTemplate = new JdbcTemplate(source); 
		this.findUserAgingCandidatesSP = new FindUserAgingCandidatesSP(jdbcTemplate.getDataSource()); 
		this.bulkUserDeactivateSP =  new BulkUserDeactivateSP(jdbcTemplate.getDataSource());
		this.bulkUserNotificationFlagUpdateSP = new BulkUserNotificationFlagUpdateSP(jdbcTemplate.getDataSource()); 
		this.productsUserIsInvolvedWithSP = new ProductsUserIsInvolvedWithSP(jdbcTemplate.getDataSource());
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AgingPolicyEnforcer.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		
		
		//agingPolicyScanner();
		/*FirstNotificationEnforcer firstNotification = new FirstNotificationEnforcer(bulkUserNotificationFlagUpdateSP,
				auRepo);
		firstNotification.enforcePolicy();
		SecondNotificationEnforcer secondNotification = new SecondNotificationEnforcer(bulkUserNotificationFlagUpdateSP,
				auRepo);
		secondNotification.enforcePolicy();
		ThirdNotificationEnforcer thirdNotification = new ThirdNotificationEnforcer(bulkUserNotificationFlagUpdateSP,
				auRepo);
		thirdNotification.enforcePolicy();
		*/
		FourthNotificationEnforcer fourthNotification = new FourthNotificationEnforcer(bulkUserNotificationFlagUpdateSP,
				bulkUserDeactivateSP,
				productsUserIsInvolvedWithSP,
				auRepo,
				jdbcTemplate);
		fourthNotification.enforcePolicy();
	
		//long userId = 10;
		//Reactivate reactivate = new Reactivate(AgedUserEntryRepository auRepo);
		//reactivate.user(userId);

	}
	
	private void agingPolicyScanner() {

		Map<String, Object> resultSet =  findUserAgingCandidatesSP.execute();
		Iterator<Map.Entry<String, Object>> resultSetIterator = resultSet.entrySet().iterator();
		EntryManager entryManager = new EntryManager(auRepo);
		
		while (resultSetIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement = (ArrayList<LinkedCaseInsensitiveMap<Integer>>)resultSetIterator.next().getValue();
			entryManager.putAgingCandidatesOnQueue(resultSetMapElement);
			
		}
	}


}
