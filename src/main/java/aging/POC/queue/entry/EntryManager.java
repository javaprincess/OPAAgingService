package aging.POC.queue.entry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.User;
import aging.POC.enforcers.AgedUserEntryRepository;

public class EntryManager {
	
	private AgedUserEntryRepository auRepo;
	
	public EntryManager(AgedUserEntryRepository auRepo) {
		this.auRepo = auRepo;
	}
	
	public void putAgingCandidatesOnQueue(ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement) {
		System.out.println("number of aging candidates found: " + resultSetMapElement.size());
		
		for (LinkedCaseInsensitiveMap<Integer> mapMember : resultSetMapElement) {
			
			long userId = mapMember.get("userid");
			long notificationFlag = mapMember.get("notificationFlag");
			
			if (isUserOnOPAQueue(userId))
				System.out.println("user: " + mapMember.get("userId") + " exists in the OPAQueue");
			else {
				System.out.println("putting this user: " + mapMember.get("userId").toString() + " on the OPAQueue");
				System.out.println("notificationFlag: " + mapMember.get("notificationFlag").toString() + " on the OPAQueue");
				User user =  new User(
							userId,
							notificationFlag);
			
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
			}
		}
	}
	
	
	private boolean isUserOnOPAQueue(long userId) {
    	boolean exists = false;
    	
    	List<AgedUserEntry> userEntry = auRepo.findByUserId(userId);
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
    }


}
