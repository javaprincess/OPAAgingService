package aging.POC.enforcers;

import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.storedprocedures.rowmappers.ProductId;

@Component("reactivate")
public class Reactivate extends AgingPolicyEnforcer {
	
	private String userId;
	
	public Reactivate() {
		
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void enforcePolicy() {

		reactivateUser(new Long(userId));
	}
	
	private void reactivateUser(long userId) {
		//look on the QUEUE to get the user's products that were deactivated
		List<AgedUserEntry> userToReactivate = agedUserEntryRepository.findByUserId(userId);
		
		//TODO: WHATIF there >1 entry for this user on the queue?
		List<ProductId> productIdList = userToReactivate.get(0).getJsonData().getUser().getProductIdList();
		
		System.out.println(userToReactivate.get(0).getJsonData().getUser().getUserId());
		
		for (ProductId productId : productIdList) {
			System.out.println(productId.getProductId());
		}
		
		System.out.println(userToReactivate.get(0).getJsonData().getUser().getNotificationFlag());
		
		//TODO: not that simple...you have to exercise the business rules for reactivation
		//need a Reactivate.products(productIdList) and  Deactivate.products(productIdList);
		//Reactivate.users(userIdList) and Deactivate.users(userIdList);
		//Map reactivateUsersProductsStatus = bulkActivateUserProductsSP.execute(productIdList, userId);
		
		//flip the switch on the users's isActive flag
		//Map reactivateStatus = reactivateUserSP.execute(userId);
		
	}
}
