package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.util.OPAComplianceAgingConstants;

@SpringBootApplication
public class AgingPolicyEnforcer implements CommandLineRunner {
	
	@Autowired
	private AgedUserEntryRepository auRepo;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate; 
	
	protected FindUserAgingCandidatesSP findUserAgingCandidatesSP; 
	protected BulkUserDeactivateSP bulkUserDeactivateSP;
	protected BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	protected ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;

	
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
		OPAComplianceAgingConstants.initialize();
		agingPolicyScanner();
	}
		
	
	public AgingPolicyEnforcer() {
		
	}
	
	public AgingPolicyEnforcer(AgedUserEntryRepository auRepo,
			FindUserAgingCandidatesSP findUserAgingCandidatesSP) {
		this.auRepo = auRepo;
		this.findUserAgingCandidatesSP = findUserAgingCandidatesSP;
	}
	
	
	public void agingPolicyScanner() {

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
