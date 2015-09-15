package aging.POC.deleteThis;


import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.enforcers.AgingPolicyEnforcer;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.util.OPAComplianceAgingEnum;

@Component("secondNotificationEnforcer")
public class SecondNotificationEnforcer extends AgingPolicyEnforcer  {

	public SecondNotificationEnforcer() {
		
	}
	
	public void enforcePolicy() {
		
			Integer currentNotificationFlagValue = 90 - new Integer(OPAComplianceAgingEnum.DELTA_1.getValue()); //this is what I'm looking for
			Integer newNotificationFlagValue = 90 - new Integer(OPAComplianceAgingEnum.DELTA_2.getValue()); //new value for notification
			 
			List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(currentNotificationFlagValue);
			EntryManager entryManager = new EntryManager(agedUserEntryRepository);
			
			System.out.println("looking for " + currentNotificationFlagValue + " day aging candidate matches: " + notificationList.size());
		
			for (AgedUserEntry user : notificationList) {
				System.out.println("in 2ndNotif:");
				System.out.println("userId: " + user.getJsonData().getUser().getUserId());
			}
			
			entryManager.addWarningEntries(notificationList);
			bulkUserNotificationFlagUpdate(notificationList, newNotificationFlagValue);
			/*try {
			EmailUtils emailUtils =  new EmailUtils();
			emailUtils.sendNotificationEMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	
}
