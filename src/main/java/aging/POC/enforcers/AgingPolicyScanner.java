package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Component;

import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;




@Component("scanner")
public class AgingPolicyScanner extends AgingPolicyEnforcer {

	public void enforcePolicy() {

		Map agingCandidatesMap = findUserAgingCandidatesSP.execute();
		
		@SuppressWarnings("unchecked")
		ArrayList<AgedUser> candidateList = (ArrayList<AgedUser>) agingCandidatesMap.get("RESULT_LIST");
		
		new EntryManager(agedUserEntryRepository).addWarningEntries(candidateList);
	
	}
}
