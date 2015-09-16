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
	
	public void addWarningEntries(List<AgedUserEntry> agingCandidatesList) {
		System.out.println("number of aging candidates found: " + agingCandidatesList.size());
		
		for (AgedUserEntry mapMember : agingCandidatesList) {
			
			Integer userId = new Long(mapMember.getJsonData().getUser().getUserId()).intValue();
			long notificationFlag = mapMember.getJsonData().getUser().getNotificationFlag();
			long isPub = mapMember.getJsonData().getUser().getIsPub();
			System.out.println("in entryManager:");
			System.out.println("userId: " + userId);
			System.out.println("notificationFlag: " +  notificationFlag);
			System.out.println("isPub: " + isPub);
			
			if (!isUserOnOPAQueue(userId)) {
			
				User user = new User();
				user.setUserId(userId);
				user.setNotificationFlag(new Long(notificationFlag).intValue());
				user.setIsPub(new Long(isPub).intValue());
				
				//auRepo.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
				auRepo.save(new AgedUserWarningEntry().createEntry(user));
				
			}
		}
	}
	
	
	
	public void addExpiryEntry(User user) {

			User agedUser = new User();
			agedUser.setUserId(user.getUserId());
			agedUser.setNotificationFlag(user.getNotificationFlag());
			agedUser.setIsPub(user.getIsPub());
			
			agedUser.setProductIdList(user.getProductIdList());
			agedUser.setProductsToSuspend(user.getProductsToSuspend());
			agedUser.setProductsToResubmit(user.getProductsToResubmit());
			agedUser.setProductsToReassign(user.getProductsToReassign());
			agedUser.setTasksToCancel(user.getTasksToCancel());
			
			System.out.println("userId in addExpiryEntries: " + agedUser.getUserId());
			
			if (!isExpiryUserOnOPAQueue(agedUser.getUserId()))
				auRepo.save(new AgedUserExpiryEntry().createEntry(agedUser));
				
		
		
	}
	
	public AgedUserEntry pullAgedUserToReactivate(Integer userId) {
		AgedUserEntry userToReactivate = null;
		
		//TODO: if user is not on the queue...throw an Exception and send the error message to the OPAAdmin
		//screen
		if (!isUserOnOPAQueue(userId)) {
			return userToReactivate;
		} else {
			List<AgedUserEntry> agedUserEntry = auRepo.findByUserId(userId);
			userToReactivate =  agedUserEntry.get(0);
		}

		return userToReactivate;
	}
	
	private boolean isUserOnOPAQueue(Integer userId) {
    	boolean exists = false;
    	
    	//List<AgedUserEntry> userEntry = auRepo.findByUserIdAndNotification(user.getJsonData().getUser().getUserId(), 
    	//		user.getJsonData().getUser().getNotificationFlag());
    	
    	List<AgedUserEntry> userEntry = auRepo.findByUserId(userId);
    	
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
