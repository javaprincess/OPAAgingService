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

@Component("thirdNotificationEnforcer")
public class ThirdNotificationEnforcer extends AgingPolicyEnforcer  {


	public void enforcePolicy() {
		
	
		Integer currentNotificationFlagValue = 90 - new Integer(OPAComplianceAgingEnum.DELTA_2.getValue());
		Integer newNotificationFlagValue = 90 - new Integer(OPAComplianceAgingEnum.DELTA_3.getValue());
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(currentNotificationFlagValue);
		List<String> userIdList = new ArrayList<String>();

		System.out.println("looking for " + currentNotificationFlagValue  + " day aging candidate matches: " + notificationList.size());
			
		entryManager.addWarningEntries(notificationList);
		bulkUserNotificationFlagUpdate(notificationList, newNotificationFlagValue );
		/*try {
			emailUtils.sendNotificationEMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
