package com.disney.compliance.aging.POC.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.disney.compliance.aging.POC.enforcers.AgingPolicyEnforcer;
import com.disney.compliance.aging.POC.queue.entry.AgedUserEntry;
import com.disney.compliance.aging.POC.queue.entry.EntryManager;
import com.disney.compliance.aging.POC.storedprocedures.BulkProductReassignSP;
import com.disney.compliance.aging.POC.storedprocedures.UndoBulkProductCancelSP;
import com.disney.compliance.aging.POC.storedprocedures.UndoBulkProductSuspendSP;
import com.disney.compliance.aging.POC.storedprocedures.rowmappers.ReactivationMessage;

@Component("reactivationTx")
public class ReactivationTx extends AgingPolicyEnforcer {
	
	private String userId;
	
	public ReactivationTx() {
		
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	@Transactional
	public void enforcePolicy() {
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
		
		AgedUserEntry userToReactivate = entryManager.pullAgedUserToReactivate(new Integer(userId));
		unSuspendProducts(userToReactivate.getJsonData().getUser().getProductsToSuspend());
		unCancelTasks(userToReactivate.getJsonData().getUser().getTasksToCancel());
		
		if (userToReactivate.getJsonData().getUser().isPubAssociate())
			unReassignProducts(userToReactivate.getJsonData().getUser().getProductsToReassign());
	}
	
	@Transactional
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
	
	@Transactional
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
	@Transactional
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
