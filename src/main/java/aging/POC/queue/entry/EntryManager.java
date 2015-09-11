package aging.POC.queue.entry;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import aging.POC.User;
import aging.POC.enforcers.AgedUserEntryRepository;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.queue.entry.AgedUserNotificationEntry;

public class EntryManager {
	
	private AgedUserEntryRepository auRepo;
	
	public EntryManager(AgedUserEntryRepository auRepo) {
		this.auRepo = auRepo;
	}
	
	public void addWarningEntries(List<AgedUserEntry> agingCandidatesList) {
		System.out.println("number of aging candidates found: " + agingCandidatesList.size());
		
		for (AgedUserEntry mapMember : agingCandidatesList) {
			
			long userId = mapMember.getJsonData().getUser().getUserId();
			long notificationFlag = mapMember.getJsonData().getUser().getNotificationFlag();
			long isPub = mapMember.getJsonData().getUser().getIsPub();
			System.out.println("in entryManager:");
			System.out.println("userId: " + userId);
			System.out.println("notificationFlag: " +  notificationFlag);
			System.out.println("isPub: " + isPub);
			
			if (!isUserOnOPAQueue(userId)) {
			
				User user = new User();
				user.setUserId(new Long(userId).intValue());
				user.setNotificationFlag(new Long(notificationFlag).intValue());
				user.setIsPub(new Long(isPub).intValue());
				
				//auRepo.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
				
			}
		}
	}
	
	
	
	public void addExpiryEntries(User user,
			List<ProductId> productIdList,
			List<Integer> productsToSuspend,
			ConcurrentHashMap<Integer, Integer> productsToResubmitMap,
			List<Integer> productsToReassign,
			List<Integer> tasksToCancel) {

			User agedUser = user;
			agedUser.setProductIdList(productIdList);
			agedUser.setProductsToSuspend(productsToSuspend);
			agedUser.setProductsToResubmit(productsToResubmitMap);
			agedUser.setProductsToReassign(productsToReassign);
			agedUser.setTasksToCancel(tasksToCancel);
			
			//auRepo.save(AgedUserEntry.createEntry(agedUser, new AgedUserDeactivationEntry("ENFORCE_EXPIRY_POLICY")));
			auRepo.save(new AgedUserDeactivationEntry().createEntry(user));
		
	}
	
	
	
	private boolean isUserOnOPAQueue(long userId) {
    	boolean exists = false;
    	
    	List<AgedUserEntry> userEntry = auRepo.findByUserId(userId);
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
    }


}
