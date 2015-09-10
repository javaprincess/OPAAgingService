package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;




@Component("scanner")
public class AgingPolicyScanner extends AgingPolicyEnforcer {

	public void enforcePolicy() {

		//Map<String, Object> resultSet =  findUserAgingCandidatesSP.execute();
		
		Map agingCandidatesMap = findUserAgingCandidatesSP.execute();
		
		//@SuppressWarnings("unchecked")
		ArrayList<AgedUser> candidateList = (ArrayList<AgedUser>) agingCandidatesMap.get("RESULT_LIST");
		
		//cHashMap.put(user, productIdList);
		
		//Iterator<Map.Entry<String, Object>> resultSetIterator = resultSet.entrySet().iterator();
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		//while (resultSetIterator.hasNext()) {
			//@SuppressWarnings("unchecked")
			//ArrayList<AgingCandidateRowMapper> resultSetMapElement = (ArrayList<AgingCandidateRowMapper>)resultSetIterator.next().getValue();
		
			entryManager.putAgingCandidatesOnQueue(candidateList);
			
		//}
		
	}
}
