package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.storedprocedures.UndoBulkProductCancelSP;
import aging.POC.storedprocedures.UndoBulkProductSuspendSP;
import aging.POC.storedprocedures.rowmappers.AgedUser;

public abstract class AgingPolicyEnforcer  {

	@Resource(name="agedUserEntryRepository")
	protected AgedUserEntryRepository agedUserEntryRepository;
	
	protected FindUserAgingCandidatesSP findUserAgingCandidatesSP; 
	protected BulkUserDeactivateSP bulkUserDeactivateSP;
	protected BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	protected ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;
	protected JdbcTemplate jdbcTemplate; 
	
	@Resource
	public void setDataSource(DataSource source){ 
		this.jdbcTemplate = new JdbcTemplate(source); 
		this.findUserAgingCandidatesSP = new FindUserAgingCandidatesSP(jdbcTemplate.getDataSource()); 
		this.bulkUserDeactivateSP =  new BulkUserDeactivateSP(jdbcTemplate.getDataSource());
		this.bulkUserNotificationFlagUpdateSP = new BulkUserNotificationFlagUpdateSP(jdbcTemplate.getDataSource()); 
		this.productsUserIsInvolvedWithSP = new ProductsUserIsInvolvedWithSP(jdbcTemplate.getDataSource());
		
	}
	
	public void bulkUserNotificationFlagUpdate(List<AgedUserEntry> notificationList, Integer notificationFlag) {
		List<String> userIdList = new ArrayList<String>();
		
		for (AgedUserEntry user : notificationList)
			userIdList.add(new Long(user.getJsonData().getUser().getUserId()).toString());
		
		bulkUserNotificationFlagUpdateSP.execute(userIdList, notificationFlag);
	}

	public abstract void enforcePolicy();
}
