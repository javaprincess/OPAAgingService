package aging.POC.unnamedbehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.JdbcTemplate;

import aging.POC.storedprocedures.BulkProductCancelSP;
import aging.POC.storedprocedures.BulkProductReassignSP;
import aging.POC.storedprocedures.BulkProductSuspendSP;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;


//TODO: begging for a REFACTOR and a RENAME
public class TheUnnamedDeactivationBehavior {

	public List<DeactivationMessage> suspend(List<Integer> productsToSuspend, 
			long userId, 
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
	
	public List<DeactivationMessage>  resubmit(ConcurrentHashMap<Long, Integer> productsToResubmitMap, 
			long userId,
			JdbcTemplate jdbcTemplate) {

		return null;

	}
	
	//TODO: Create PUBHolding Account for PUB Associate reassignment.  Using UserId=54 for right now
	//Make this a bulk process
	public List<DeactivationMessage>  reassign(List<Integer> productsToReassign,
			long userId,
			JdbcTemplate jdbcTemplate) {
		
		String comments = "User: " + userId + "has met the OPA 90 Day Compliance Rule for account reassignment.";
		Integer newUserId = 54;
		Map bulkReassignResults =  null;
		
		//not really bulk just yet but we are projecting good things
		BulkProductReassignSP bulkReassign = new BulkProductReassignSP(jdbcTemplate.getDataSource());
		
		//Map bulkSuspensionResults = bulkSuspend.execute(productsToReassign.toString().replace("[", "").replace("]",""),
		for (Integer productId : productsToReassign) {
			bulkReassignResults = bulkReassign.execute(userId,
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
	public List<DeactivationMessage>  cancel(List<Integer>tasksToCancel, 
			long userId,
			JdbcTemplate jdbcTemplate) {
		String comments = "User: " + userId + "has met the OPA 90 Day Compliance Rule for account cancellation.";
		//TODO: fix this
		//Integer adminUserId = -1; --GENERATING NUMBERFORMAT EXCEPTION
		Integer adminUserId = 2;
		
		BulkProductCancelSP bulkCancel = new BulkProductCancelSP(jdbcTemplate.getDataSource());
		
		Map bulkCancellationResults = bulkCancel.execute(tasksToCancel.toString().replace("[", "").replace("]",""), 
				adminUserId,
				userId,
				comments);
		
		@SuppressWarnings("unchecked")
		ArrayList<DeactivationMessage> deactivationMessageList = (ArrayList<DeactivationMessage>) bulkCancellationResults.get("RESULT_LIST");
		
		for (DeactivationMessage deactivationMessage : deactivationMessageList) {
			System.out.format("%d, %s, %s\n", 
					deactivationMessage.getProductId(), 
					deactivationMessage.getStatus(), 
					deactivationMessage.getMessage());
		}
        
		return deactivationMessageList;
	}
}
