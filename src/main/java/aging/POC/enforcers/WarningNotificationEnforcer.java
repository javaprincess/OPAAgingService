package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.util.OPAComplianceAgingEnum;

@Component("warningNotificationEnforcer")
public class WarningNotificationEnforcer extends AgingPolicyEnforcer  {
	

	public WarningNotificationEnforcer() {
		
	}


	/*public void enforcePolicy() {
		
			Integer delta = new Integer(OPAComplianceAgingEnum.DELTA_1.getValue());
			Integer currentNotificationFlagValue = 0; //newest aging candidate
			
			EntryManager entryManager = new EntryManager(agedUserEntryRepository);
			
			Integer newNotificationFlag = 90 - delta; //this is what I'm going to set the new value of the notificationFlag to
			
			List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(currentNotificationFlagValue);
			
			System.out.println("looking for " + currentNotificationFlagValue + " aging candidate matches: " + notificationList.size());
			
			for (AgedUserEntry user : notificationList) {
				System.out.println("in 1stNotif:");
				System.out.println("userId: " + user.getJsonData().getUser().getUserId());
			}
			
			
			entryManager.addWarningEntries(notificationList);
			bulkUserNotificationFlagUpdate(notificationList, newNotificationFlag);
			/*try {
				EmailUtils emailUtils =  new EmailUtils();
				emailUtils.sendNotificationEMail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	/*}*/

}
