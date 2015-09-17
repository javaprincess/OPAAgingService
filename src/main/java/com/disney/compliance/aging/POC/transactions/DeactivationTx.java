package com.disney.compliance.aging.POC.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.disney.compliance.aging.POC.storedprocedures.BulkProductCancelSP;
import com.disney.compliance.aging.POC.storedprocedures.BulkProductReassignSP;
import com.disney.compliance.aging.POC.storedprocedures.BulkProductSuspendSP;
import com.disney.compliance.aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import com.disney.compliance.aging.POC.storedprocedures.rowmappers.ProductId;

@Component("deactivationTx")
public class DeactivationTx {

	public DeactivationTx() {
		
	}
	
	@Transactional
	public List<DeactivationMessage> suspendProducts(List<Integer> productsToSuspend, 
			Integer userId, 
			JdbcTemplate jdbcTemplate) {
		
		String comments = "User: " + userId + "has met the OPA 90 Day Compliance Rule for account suspension.";
		BulkProductSuspendSP bulkSuspend = new BulkProductSuspendSP(jdbcTemplate.getDataSource());
		
		Map bulkSuspensionResults = bulkSuspend.execute(productsToSuspend.toString().replace("[", "").replace("]",""), 
				comments,
				userId);
		
		@SuppressWarnings("unchecked")
		ArrayList<DeactivationMessage> deactivationMessageList = (ArrayList<DeactivationMessage>) bulkSuspensionResults.get("RESULT_LIST");
		
		for (DeactivationMessage deactivationMessage : deactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					deactivationMessage.getProductId(), 
					deactivationMessage.getStatus(), 
					deactivationMessage.getMessage());
		}
        
		return deactivationMessageList;
	}
	
	@Transactional
	public List<DeactivationMessage>  resubmitProducts(ConcurrentHashMap<Integer, Integer> productsToResubmitMap, 
			Integer userId,
			JdbcTemplate jdbcTemplate) {
		//ProductsToResubmitMap ==> ActiveLicensee, ProductId
		
		Set<Integer> activeLicensees = productsToResubmitMap.keySet();
		
		for (Integer activeLic : activeLicensees) {
			ArrayList<Integer> productIdList = new ArrayList<Integer>();
			Integer productId = productsToResubmitMap.get(activeLic);
			productIdList.add(productId);
			
			reassignProducts(productIdList,
					userId,
					activeLic,
					jdbcTemplate);
					
		}		

		return null;

	}
	
	//TODO: Make this a bulk process
	@Transactional
	public List<DeactivationMessage>  reassignProducts(List<Integer> productsToReassign,
			Integer oldUserId,
			Integer newUserId,
			JdbcTemplate jdbcTemplate) {
		
		String comments = "User: " + oldUserId + "has met the OPA 90 Day Compliance Rule for product reassignment.";
		//TODO: fix this..need to get straight from DB...as the userId for the PubHolding account will vary across envs
		
		Map bulkReassignResults =  null;
		
		//not really bulk just yet but we are projecting good things
		BulkProductReassignSP bulkReassign = new BulkProductReassignSP(jdbcTemplate.getDataSource());
		
		//Map bulkSuspensionResults = bulkSuspend.execute(productsToReassign.toString().replace("[", "").replace("]",""),
		for (Integer productId : productsToReassign) {
			bulkReassignResults = bulkReassign.execute(oldUserId,
				newUserId,
				productId,
				comments);
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<DeactivationMessage> deactivationMessageList = (ArrayList<DeactivationMessage>) bulkReassignResults.get("RESULT_LIST");
		
		for (DeactivationMessage deactivationMessage : deactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					deactivationMessage.getProductId(), 
					deactivationMessage.getStatus(), 
					deactivationMessage.getMessage());
		}
        
		return deactivationMessageList;
	}
	
	@Transactional
	public List<DeactivationMessage>  cancelTasks(List<Integer>tasksToCancel, 
			Integer userId,
			JdbcTemplate jdbcTemplate) {
		String comments = "User: " + userId + "has met the OPA 90 Day Compliance Rule for account cancellation.";
		//TODO: fix this..need to get straight from DB...as the userId for the OPAComplianceAdmin will vary across envs
		Integer adminUserId = 24863;
		
		BulkProductCancelSP bulkCancel = new BulkProductCancelSP(jdbcTemplate.getDataSource());
		
		Map bulkCancellationResults = bulkCancel.execute(tasksToCancel.toString().replace("[", "").replace("]",""), 
				adminUserId,
				userId,
				comments);
		
		
		@SuppressWarnings("unchecked")
		ArrayList<DeactivationMessage> deactivationMessageList = (ArrayList<DeactivationMessage>) bulkCancellationResults.get("RESULT_LIST");
		//TODO: right now the spBulkCancel doens't return anything...we need to fix this so we get messages
		/*for (DeactivationMessage deactivationMessage : deactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					deactivationMessage.getProductId(), 
					deactivationMessage.getStatus(), 
					deactivationMessage.getMessage());
		}*/
        
		return deactivationMessageList;
	}

}
