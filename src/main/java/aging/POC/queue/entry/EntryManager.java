package aging.POC.queue.entry;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import aging.POC.User;
import aging.POC.enforcers.AgedUserEntryRepository;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.storedprocedures.rowmappers.ProductId;

public class EntryManager {
	
	private AgedUserEntryRepository auRepo;
	
	public EntryManager(AgedUserEntryRepository auRepo) {
		this.auRepo = auRepo;
	}
	
	public void addWarningEntries(List<AgedUser> agingCandidatesList) {
		System.out.println("number of aging candidates found: " + agingCandidatesList.size());
		
		for (AgedUser mapMember : agingCandidatesList) {
			
			long userId = mapMember.getUserId();
			long notificationFlag = mapMember.getNotificationFlag();
			long isPub = mapMember.getIsPub();
			
			if (!isUserOnOPAQueue(userId)) {
			
				User user =  new User(
							userId,
							notificationFlag,
							isPub);
				
				auRepo.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
			}
		}
	}
	
	public void addExpiryEntries(User user,
			List<ProductId> productIdList,
			List<Integer> productsToSuspend,
			ConcurrentHashMap<Long, Integer> productsToResubmitMap,
			List<Integer> productsToReassign,
			List<Integer> tasksToCancel) {

			User agedUser = user;
			agedUser.setProductIdList(productIdList);
			agedUser.setProductsToSuspend(productsToSuspend);
			agedUser.setProductsToResubmit(productsToResubmitMap);
			agedUser.setProductsToReassign(productsToReassign);
			agedUser.setTasksToCancel(tasksToCancel);
			
			auRepo.save(AgedUserEntry.createEntry(agedUser, new AgedUserDeactivationEntry("ENFORCE_EXPIRY_POLICY")));
		
	}
	
	
	
	private boolean isUserOnOPAQueue(long userId) {
    	boolean exists = false;
    	
    	List<User> userEntry = auRepo.findByUserId(userId);
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
    }


}
