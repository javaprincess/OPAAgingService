package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.storedprocedures.UndoBulkProductCancelSP;
import aging.POC.storedprocedures.UndoBulkProductSuspendSP;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.util.OPAComplianceAgingEnum;

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

	//public abstract void enforcePolicy();
	public void enforcePolicy(Integer incomingDeltaValue) {
		Integer delta = incomingDeltaValue;
		//Integer delta = new Integer(OPAComplianceAgingEnum.DELTA_1.getValue());
		
		Integer currentNotificationFlagValue = 0; //newest aging candidate
		
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		Integer newNotificationFlag = 90 - delta; //this is what I'm going to set the new value of the notificationFlag to
		
		List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(currentNotificationFlagValue);
		
		entryManager.addWarningEntries(notificationList);
		bulkUserNotificationFlagUpdate(notificationList, newNotificationFlag);
		//TODO: add the delta to the call to the Notification utility
		/*try {
			EmailUtils emailUtils =  new EmailUtils();
			emailUtils.sendNotificationEMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
