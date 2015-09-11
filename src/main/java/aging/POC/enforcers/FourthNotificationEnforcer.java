package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.util.OPAComplianceAgingEnum;

@Component("fourthNotificationEnforcer")
public class FourthNotificationEnforcer extends AgingPolicyEnforcer  {

	@Resource(name="deactivatePolicyEnforcer")
	private DeactivatePolicyEnforcer deactivatePolicyEnforcer;
	
	public void enforcePolicy() {
		
		Integer currentNotificationFlagValue = 90 - new Integer(OPAComplianceAgingEnum.DELTA_3.getValue());
		Integer newNotificationFlagValue  = 90 - new Integer(OPAComplianceAgingEnum.DELTA_4.getValue());
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
			
		List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(currentNotificationFlagValue);
			
		System.out.println("looking for " + currentNotificationFlagValue  + " day aging candidate matches: " + notificationList.size());

		entryManager.addWarningEntries(notificationList);
		bulkUserNotificationFlagUpdate(notificationList, newNotificationFlagValue );
		/*try {
			EmailUtils emailUtils =  new EmailUtils();
			emailUtils.sendNotificationEMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			
		//deactivate(notificationList);
	}


	/*private void deactivate(List<AgedUserEntry> agedUserEntryList) {
		List<User> userList = new ArrayList<User>();
		
		for (AgedUserEntry agedUserEntry : agedUserEntryList)
			userList.add(agedUserEntry.getJsonData().getUser());
		
		deactivatePolicyEnforcer.setUserList(userList);
		deactivatePolicyEnforcer.enforcePolicy();

	}*/
	
	
}
