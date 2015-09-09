package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserDeactivationEntry;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.unnamedbehavior.TheUnnamedDeactivationBehavior;

@Component("deactivate")
public class Deactivate extends AgingPolicyEnforcer {
	

	private List<String> userIdList;
	
	public Deactivate() {
		
	}
	
	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}
	
	public void enforcePolicy() {

		deactivateProductsUserIdListIsInvolvedWith(userIdList);
		deactivateUsers(userIdList);
	}
	
	private void deactivateUsers(List<String> userIdListToDeactivate) {
		bulkUserDeactivateSP.execute(userIdListToDeactivate.toString().replace("[", "").replace("]","")); 
	}
	
	private void deactivateProductsUserIdListIsInvolvedWith(List<String> userIdList) {
	
		List<String> productStatusList = new ArrayList<String>( Arrays.asList("1","2","3","6","7","8") );
		ConcurrentHashMap<String, ArrayList<ProductId>> userProductIdsConcurrentHashMap = null;
		ConcurrentHashMap<String, ArrayList<String>> errorMessageMapForUsersWithProductsThatFailedDeactivation =  null;
		
		userProductIdsConcurrentHashMap = getProductsUsersAreInvolvedWith(productStatusList, userIdList);	
		errorMessageMapForUsersWithProductsThatFailedDeactivation = bulkDeactivateUserProducts(userProductIdsConcurrentHashMap);
		
		/*for (String userId : userIdList) {
			if (!errorMessageMapForUsersWithProductsThatFailedDeactivation.contains(userId))
				usersToDeactivateList.add(userId);
		}
		
		bulkDeactivateUsers(usersToDeactivateList); */
	}
	
	private void putAgingCandidatesToDeactivateOnTheQueue(Long userId,
			List<ProductId> productIdList,
			List<Integer> productsToSuspend,
			List<Integer> productsToResubmit,
			List<Integer> productsToReassign,
			List<Integer> tasksToCancel) {

			User user = new User(userId, 90);
			user.setProductIdList(productIdList);
			user.setProductsToSuspend(productsToSuspend);
			user.setProductsToResubmit(productsToResubmit);
			user.setProductsToReassign(productsToReassign);
			user.setTasksToCancel(tasksToCancel);
			
			agedUserEntryRepository.save(new AgedUserDeactivationEntry().createEntry(user));
		
	}
	
	
	
    private ConcurrentHashMap<String, ArrayList<String>> bulkDeactivateUserProducts(
				ConcurrentHashMap<String, ArrayList<ProductId>> userProductsConcurrentHashMap) {
			

    		//TODO : I'm building stuff here....where o where is my builder pattern?
    		//which one should I use -- REFACTOR is calling me....hear her voice in the distance.
    	    for (String userId : userProductsConcurrentHashMap.keySet()) {
    	    	List<Integer> productsToSuspend = new ArrayList<Integer>();
        		List<Integer> productsToResubmit = new ArrayList<Integer>();
        		List<Integer> productsToReassign = new ArrayList<Integer>();
        		List<Integer> tasksToCancel = new ArrayList<Integer>();
        		List<ProductId> productIdList = userProductsConcurrentHashMap.get(userId);
        		
    	    	System.out.println("userId: " + userId);
    	    	System.out.println("productIdList size: " + productIdList.size());

    			for (ProductId productIdListElement : productIdList) {

    				Integer productId = productIdListElement.getProductId();
    				
    				//--check for licensee or associate products
		    		String associateOrLicenseeSql = "SELECT DISTINCT userTypeId, productId FROM productState WHERE productId=?";

		    		//--check for reviewer tasks
		    		String reviewerSql = "SELECT DISTINCT * FROM productReviews WHERE productStateId in (SELECT id FROM productState WHERE id = ?)";
		    		
		    		List<Map<String, Object>> rows = jdbcTemplate.queryForList(associateOrLicenseeSql, productId);
		    		List<Map<String, Object>> reviewerRows = jdbcTemplate.queryForList(reviewerSql, productId);

		    		
		    		if (!reviewerRows.isEmpty()) 
		    			tasksToCancel.add(productId);

		    		for (Map<String, Object> rowElement : rows) {

		    			if (isAssociate((Integer)rowElement.get("userTypeId"))) {
		    				//if MRCH associate w/active licensee, resubmit the product else, suspend the product
		    				//TODO: figure out how to resubmit...and figure out how to determine if there is
		    				//an active licensee on the product
		    				//productsToResubmit.add(productId);
		    				
		    	    	    //if PUB associate, reassign product to the PUBHOLDING account
		    				productsToReassign.add(productId);
		    					
		    				
		    			} else if (isLicensee((Integer)rowElement.get("userTypeId"))) {
		    					productsToSuspend.add(productId);
		    			}
		    		}
		    		
    			}
    		
    			System.out.println("number of products to resassign -- user is an associate (PUB) on these products: " + productsToReassign.size());
    			System.out.println("number of products to suspend -- user is a licensee on these products: " + productsToSuspend.size());
    			System.out.println("number of tasks to cancel -- user is a reviewer on these products: " + tasksToCancel.size());
    			//TODO: don't forget resubmit!
    			
    			putAgingCandidatesToDeactivateOnTheQueue(new Long(userId),
    					productIdList,
    					productsToReassign,
    					productsToResubmit,
    					productsToSuspend,
    					tasksToCancel);
    			
    		
    			
    			//TODO: this is behavior...need another pattern
    			TheUnnamedDeactivationBehavior nameMe = new TheUnnamedDeactivationBehavior();
    			if (!productsToSuspend.isEmpty()) {
    				List<DeactivationMessage> suspensionErrorMessages = nameMe.suspend(productsToSuspend, 
    						userId, 
    						jdbcTemplate);
    			}
    			
    			//If (!productsToResubmit.isEmpty()) {
    			//	List<DeactivationMessage> resubmissionErrorMessages = nameMe.resumit(productsToResubmit, 
    			//			userId, 
    			//			jdbcTemplate);
    			
    			if (!productsToReassign.isEmpty()) {
    				List<DeactivationMessage> resassignmentErrorMessages = nameMe.reassign(productsToReassign, 
    						userId, 
    						jdbcTemplate);
    			}
    			
    			if (!tasksToCancel.isEmpty()) {
    				List<DeactivationMessage> cancellationErrorMessages = nameMe.cancel(tasksToCancel, 
    						userId, 
    						jdbcTemplate);
    			}
    			
    		} 
    	    
			return null;
	}
    
    private boolean isAssociate(Integer userTypeId) {
    	boolean isAssociate = false;
    	
    	if (userTypeId.equals(1))
    		isAssociate = true;
    	
    	return isAssociate;
    }
    
    private boolean isLicensee(Integer userTypeId) {
    	boolean isLicensee = false;
    	
    	if (userTypeId.equals(2))
    		isLicensee = true;
    	
    	return isLicensee;
    }

	private ConcurrentHashMap<String, ArrayList<ProductId>> getProductsUsersAreInvolvedWith(
			    List<String> productStatusList,
				List<String> userIdList) {
		
			ConcurrentHashMap<String, ArrayList<ProductId>> cHashMap = new ConcurrentHashMap<String, ArrayList<ProductId>>();
			
			//TODO :  To all the GoodCodingPracticeGoddessesUpAbove -- please let Tracy refactor this crap.  She knows better than this
			//but please have grace upon her that she may remember to come back here when she is finished
			//with stringing this all together.  Amen.  Thank you.  Blessed Be and all that jazz :-)
			for ( String userIdListElement : userIdList) {
				Integer userId = new Integer(userIdListElement);
				
				Map productsUserIsInvolvedWith = productsUserIsInvolvedWithSP.execute(productStatusList, userId);
				
				@SuppressWarnings("unchecked")
				ArrayList<ProductId> productIdList = (ArrayList<ProductId>) productsUserIsInvolvedWith.get("RESULT_LIST");
				
				cHashMap.put(userIdListElement, productIdList);
			}
		
			return cHashMap;
		}
	



}
