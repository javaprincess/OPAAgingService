package aging.POC.enforcers;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;

public abstract class AgingPolicyEnforcer  {

	@Autowired
	protected AgedUserEntryRepository agedUserEntryRepository;
	
	protected FindUserAgingCandidatesSP findUserAgingCandidatesSP; 
	protected BulkUserDeactivateSP bulkUserDeactivateSP;
	protected BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	protected ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;
	protected JdbcTemplate jdbcTemplate; 
	
	@Autowired
	public void setDataSource(DataSource source){ 
		this.jdbcTemplate = new JdbcTemplate(source); 
		this.findUserAgingCandidatesSP = new FindUserAgingCandidatesSP(jdbcTemplate.getDataSource()); 
		this.bulkUserDeactivateSP =  new BulkUserDeactivateSP(jdbcTemplate.getDataSource());
		this.bulkUserNotificationFlagUpdateSP = new BulkUserNotificationFlagUpdateSP(jdbcTemplate.getDataSource()); 
		this.productsUserIsInvolvedWithSP = new ProductsUserIsInvolvedWithSP(jdbcTemplate.getDataSource());
		
	}

	public abstract void enforcePolicy();
}
