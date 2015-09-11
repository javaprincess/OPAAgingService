package aging.POC.deactivation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.enforcers.AgingPolicyEnforcer;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkProductReassignSP;
import aging.POC.storedprocedures.BulkProductSuspendSP;
import aging.POC.storedprocedures.UndoBulkProductCancelSP;
import aging.POC.storedprocedures.UndoBulkProductSuspendSP;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.storedprocedures.rowmappers.ReactivationMessage;

@Component("reactivationBehavior")
public class ReactivationBehavior extends AgingPolicyEnforcer {
	
	private String userId;
	
	public ReactivationBehavior() {
		
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public void enforcePolicy() {
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		AgedUserEntry userToReactivate = entryManager.pullAgedUserToReactivate(new Integer(userId));
		unSuspendProducts(userToReactivate.getJsonData().getUser().getProductsToSuspend());
		unCancelTasks(userToReactivate.getJsonData().getUser().getTasksToCancel());
		
		if (userToReactivate.getJsonData().getUser().isPubAssociate())
			unReassignProducts(userToReactivate.getJsonData().getUser().getProductsToReassign());
	}
	
	private ArrayList<ReactivationMessage>  unSuspendProducts(List<Integer> unsuspendList) {
		List<Integer> productsToUnsuspendList = unsuspendList;
		String comments = "User: " + userId + "is being reactivated by the OPAAdmin. Unsuspending user's products";
		UndoBulkProductSuspendSP undoBulkSuspend = new UndoBulkProductSuspendSP(jdbcTemplate.getDataSource());
		
		Map undoBulkSuspensionResults = undoBulkSuspend.execute(productsToUnsuspendList.toString().replace("[", "").replace("]",""), 
				comments,
				userId);
		
		@SuppressWarnings("unchecked")
		ArrayList<ReactivationMessage> reactivationMessageList = (ArrayList<ReactivationMessage>) undoBulkSuspensionResults.get("RESULT_LIST");
		
		for (ReactivationMessage reactivationMessage : reactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					reactivationMessage.getProductId(), 
					reactivationMessage.getStatus(), 
					reactivationMessage.getMessage());
		}
        
		return reactivationMessageList;
	}
	
	private ArrayList<ReactivationMessage> unCancelTasks(List<Integer> uncancelTasksList) {
		List<Integer> tasksToUncancelList = uncancelTasksList;
		String comments = "User: " + userId + "is being reactivated by the OPAAdmin. Reactivating user's tasks.";
		UndoBulkProductCancelSP undoBulkCancel = new UndoBulkProductCancelSP(jdbcTemplate.getDataSource());
		
		Map undoBulkSuspensionResults = undoBulkCancel.execute(tasksToUncancelList.toString().replace("[", "").replace("]",""), 
				comments,
				userId);
		
		@SuppressWarnings("unchecked")
		ArrayList<ReactivationMessage> reactivationMessageList = (ArrayList<ReactivationMessage>) undoBulkSuspensionResults.get("RESULT_LIST");
		
		for (ReactivationMessage reactivationMessage : reactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					reactivationMessage.getProductId(), 
					reactivationMessage.getStatus(), 
					reactivationMessage.getMessage());
		}
        
		return reactivationMessageList;
	}
	
	//This is only for PUBAssociates.  
	//If the productState hasn't changed then
	//the product can be reassigned from PUBHolding to the reactivatedUser
	private ArrayList<ReactivationMessage>  unReassignProducts(List<Integer> unreassignProductList) {
		
		Integer pubHoldingUser = 24850;
		List<Integer> productsToUnreassignList = unreassignProductList;
		String comments = "User: " + userId + "is being reactivated by the OPAAdmin. unResubmitting (rassigning) user's tasks.";
		BulkProductReassignSP undoReassign = new BulkProductReassignSP(jdbcTemplate.getDataSource());
		ArrayList<ReactivationMessage> reactivationMessageList = null;
		Map undoBulkReassignResults = null;
		
		for (Integer productId : productsToUnreassignList ) {
		
			//need to check the state of the product assigned to PUBHolding.  
			//if the state hasn't changed, then you can resubmit/resassign the product
			//reassign the product to the associate.
			undoBulkReassignResults = undoReassign.execute(pubHoldingUser,
				userId,
				productId,
				comments);
		
		}
		
		
		reactivationMessageList = (ArrayList<ReactivationMessage>) undoBulkReassignResults.get("RESULT_LIST");
		
		for (ReactivationMessage reactivationMessage : reactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
				reactivationMessage.getProductId(), 
				reactivationMessage.getStatus(), 
				reactivationMessage.getMessage());
		}
		
        
		return reactivationMessageList;
	}	
}
