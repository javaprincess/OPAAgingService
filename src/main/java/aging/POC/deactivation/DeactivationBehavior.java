package aging.POC.deactivation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import aging.POC.storedprocedures.BulkProductCancelSP;
import aging.POC.storedprocedures.BulkProductReassignSP;
import aging.POC.storedprocedures.BulkProductSuspendSP;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;

@Component("deactivationBehavior")
public class DeactivationBehavior {

	public DeactivationBehavior() {
		
	}
	
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
	
	public List<DeactivationMessage>  resubmitProducts(ConcurrentHashMap<Integer, Integer> productsToResubmitMap, 
			Integer userId,
			JdbcTemplate jdbcTemplate) {
		//ProductsToResubmitMap ==> ActiveLicensee, ProductId
		
		Set<Integer> activeLicensees = productsToResubmitMap.keySet();
		
		//TODO: This has to change once the value in the map becomes a list
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
	
	//TODO: need to decide what userId we are acting as when we cancel.  For right now, using -1
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
