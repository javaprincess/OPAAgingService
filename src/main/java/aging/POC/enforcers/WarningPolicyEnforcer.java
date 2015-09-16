package aging.POC.enforcers;



import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;


@Component("warningNotificationEnforcer")
public class WarningPolicyEnforcer extends AgingPolicyEnforcer  {
	

	public WarningPolicyEnforcer() {
		
	}


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
