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
			
			if (!isUserOnOPAQueue(mapMember)) {
			
				User user = new User();
				user.setUserId(new Long(userId).intValue());
				user.setNotificationFlag(new Long(notificationFlag).intValue());
				user.setIsPub(new Long(isPub).intValue());
				
				//auRepo.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
				
			}
		}
	}
	
	
	
	public void addExpiryEntry(User user) {

			User agedUser = new User();
			agedUser.setUserId(user.getUserId());
			agedUser.setNotificationFlag(user.getNotificationFlag());
			agedUser.setIsPub(user.getIsPub());
			
			/*agedUser.setProductIdList(user.getProductIdList());
			agedUser.setProductsToSuspend(productsToSuspend);
			agedUser.setProductsToResubmit(productsToResubmitMap);
			agedUser.setProductsToReassign(productsToReassign);
			agedUser.setTasksToCancel(tasksToCancel);*/
			
			System.out.println("userId in addExpiryEntries: " + agedUser.getUserId());
			
			if (!isExpiryUserOnOPAQueue(agedUser.getUserId()))
				auRepo.save(new AgedUserDeactivationEntry().createEntry(agedUser));
				
		
		
	}
	
	
	
	private boolean isUserOnOPAQueue(AgedUserEntry user) {
    	boolean exists = false;
    	
    	//List<AgedUserEntry> userEntry = auRepo.findByUserIdAndNotification(user.getJsonData().getUser().getUserId(), 
    	//		user.getJsonData().getUser().getNotificationFlag());
    	
    	List<AgedUserEntry> userEntry = auRepo.findByUserId(user.getJsonData().getUser().getUserId());
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
    }
	
	private boolean isExpiryUserOnOPAQueue(long userId) {
		List<AgedUserEntry> userEntry = auRepo.findByUserIdAndJob(userId, "ENFORCE_EXPIRY_NOTIFICIATION");
		
		boolean exists = false;
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
	}


}
