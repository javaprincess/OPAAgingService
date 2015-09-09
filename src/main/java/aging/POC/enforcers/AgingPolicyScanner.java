package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;

@Component("scanner")
public class AgingPolicyScanner extends AgingPolicyEnforcer {

	public void enforcePolicy() {

		Map<String, Object> resultSet =  findUserAgingCandidatesSP.execute();
		Iterator<Map.Entry<String, Object>> resultSetIterator = resultSet.entrySet().iterator();
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		while (resultSetIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement = (ArrayList<LinkedCaseInsensitiveMap<Integer>>)resultSetIterator.next().getValue();
			entryManager.putAgingCandidatesOnQueue(resultSetMapElement);
			
		}
	}
}
