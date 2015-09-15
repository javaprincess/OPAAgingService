package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;




@Component("scanner")
public class AgingPolicyScanner extends AgingPolicyEnforcer {

	//Scanner's enforcePolicy is different because it is putting AgedUsers's on the queue...not pulling off
	//AgedUserEntry from the queue.  So a User object has to be created, an AgedUserNotificationEntry created, then 
	//passed to the EntryManager to add to the queue.  All the other notifiers don't have to go through this step because 
	//they are pulling AgedUserEntry off the queue and then putting a new one on the queue.
	//Note: incomingDeltaValue is only provided to accommodate inheritance
	@Override
	public void enforcePolicy(Integer incomingDeltaValue) {

		Map agingCandidatesMap = findUserAgingCandidatesSP.execute();
		List<AgedUserEntry> candidateList =  new ArrayList<AgedUserEntry>();
		
		@SuppressWarnings("unchecked")
		ArrayList<AgedUser> candidateResultList = (ArrayList<AgedUser>) agingCandidatesMap.get("RESULT_LIST");
		
		for (AgedUser agedUserEntry : candidateResultList) {
			
			User agedUser = new User();
			agedUser.setUserId(agedUserEntry.getUserId());
			agedUser.setNotificationFlag(agedUserEntry.getNotificationFlag());
			agedUser.setIsPub(agedUserEntry.getIsPub());
			
			candidateList.add(new AgedUserNotificationEntry().createEntry(agedUser));
		}
			
		
		new EntryManager(agedUserEntryRepository).addWarningEntries(candidateList);
	
	}
}
