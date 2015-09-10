package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserDeactivationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.unnamedbehavior.TheUnnamedDeactivationBehavior;

@Component("deactivate")
public class Deactivate extends AgingPolicyEnforcer {

	private EntryManager entryManager;
	private List<String> userIdList;
	private List<User> userList;
	
	public Deactivate() {
		
	}
	
	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}
	
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	public void enforcePolicy() {

		this.entryManager =  new EntryManager(agedUserEntryRepository);
		deactivateProductsUserIdListIsInvolvedWith(userList);
		deactivateUsers(userIdList);
	}
	
	private void deactivateUsers(List<String> userIdListToDeactivate) {
		bulkUserDeactivateSP.execute(userIdListToDeactivate.toString().replace("[", "").replace("]","")); 
	}
	
	private void deactivateProductsUserIdListIsInvolvedWith(List<User> userList) {
	
		//TODO: I'm thinking this should be in OPAProperties.
		List<String> productStatusList = new ArrayList<String>( Arrays.asList("1","2","3","6","7","8") );
		ConcurrentHashMap<User, ArrayList<ProductId>> userProductIdsConcurrentHashMap = null;
		ConcurrentHashMap<String, ArrayList<String>> errorMessageMapForUsersWithProductsThatFailedDeactivation =  null;
		
		userProductIdsConcurrentHashMap = getProductsUsersAreInvolvedWith(productStatusList, userList);	
		errorMessageMapForUsersWithProductsThatFailedDeactivation = bulkDeactivateUserProducts(userProductIdsConcurrentHashMap);
		
		/*for (String userId : userIdList) {
			if (!errorMessageMapForUsersWithProductsThatFailedDeactivation.contains(userId))
				usersToDeactivateList.add(userId);
		}
		
		bulkDeactivateUsers(usersToDeactivateList); */
	}
	
	
	
	
    private ConcurrentHashMap<String, ArrayList<String>> bulkDeactivateUserProducts(
				ConcurrentHashMap<User, ArrayList<ProductId>> userProductsConcurrentHashMap) {
			

    		//TODO : I'm building stuff here....where o where is my builder pattern?
    		//which one should I use -- REFACTOR is calling me....hear her voice in the distance.
    	    for (User user : userProductsConcurrentHashMap.keySet()) {
    	    	List<Integer> productsToSuspend = new ArrayList<Integer>();
        		ConcurrentHashMap<Long, Integer> productsToResubmitMap = new ConcurrentHashMap<Long, Integer>();
        		List<Integer> productsToReassign = new ArrayList<Integer>();
        		List<Integer> tasksToCancel = new ArrayList<Integer>();
        		List<ProductId> productIdList = userProductsConcurrentHashMap.get(user.getUserId());
        		
    	    	System.out.println("userId: " + user.getUserId());
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
		    			
		    				if (user.isPubAssociate()) {
		    					productsToReassign.add(productId);
		    				} else if (user.hasAnActiveLicenseeForProduct(productId)) {
		    					productsToResubmitMap.put(user.getActiveLicensee(), productId);
		    				} else {
		    					productsToSuspend.add(productId);
		    				}
		    					
		    				
		    			} else if (isLicensee((Integer)rowElement.get("userTypeId"))) {
		    					productsToSuspend.add(productId);
		    			}
		    		}
		    		
    			}
    		
    			System.out.println("number of products to resassign -- user is an associate (PUB) on these products: " + productsToReassign.size());
    			System.out.println("number of products to suspend -- user is a licensee on these products: " + productsToSuspend.size());
    			System.out.println("number of tasks to cancel -- user is a reviewer on these products: " + tasksToCancel.size());
    			//TODO: don't forget resubmit!
    			
    			entryManager.putAgingCandidatesToDeactivateOnTheQueue(user,
    					productIdList,
    					productsToReassign,
    					productsToResubmitMap,
    					productsToSuspend,
    					tasksToCancel);
    			
    		
    			
    			//TODO: this is behavior...need another pattern
    			TheUnnamedDeactivationBehavior nameMe = new TheUnnamedDeactivationBehavior();
    			if (!productsToSuspend.isEmpty()) {
    				List<DeactivationMessage> suspensionErrorMessages = nameMe.suspend(productsToSuspend, 
    						user.getUserId(), 
    						jdbcTemplate);
    			}
    			
    			if (!productsToResubmitMap.isEmpty()) {
    				List<DeactivationMessage> resubmissionErrorMessages = nameMe.resubmit(productsToResubmitMap, 
    						user.getUserId(), 
    						jdbcTemplate);
    			}
    			
    			if (!productsToReassign.isEmpty()) {
    				List<DeactivationMessage> resassignmentErrorMessages = nameMe.reassign(productsToReassign, 
    						user.getUserId(), 
    						jdbcTemplate);
    			}
    			
    			if (!tasksToCancel.isEmpty()) {
    				List<DeactivationMessage> cancellationErrorMessages = nameMe.cancel(tasksToCancel, 
    						user.getUserId(), 
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

	private ConcurrentHashMap<User, ArrayList<ProductId>> getProductsUsersAreInvolvedWith(
			    List<String> productStatusList,
				List<User> userList) {
		
		//TODO: shouldn't i make a  local copy of userList?
		
			ConcurrentHashMap<User, ArrayList<ProductId>> cHashMap = new ConcurrentHashMap<User, ArrayList<ProductId>>();
			
			//TODO :  To all the GoodCodingPracticeGoddessesUpAbove -- please let Tracy refactor this crap.  She knows better than this
			//but please have grace upon her that she may remember to come back here when she is finished
			//with stringing this all together.  Amen.  Thank you.  Blessed Be and all that jazz :-)
			for ( User user : userList) {
				//Integer userId = new Integer(userIdListElement);
				
				Map productsUserIsInvolvedWith = productsUserIsInvolvedWithSP.execute(productStatusList, user.getUserId());
				
				@SuppressWarnings("unchecked")
				ArrayList<ProductId> productIdList = (ArrayList<ProductId>) productsUserIsInvolvedWith.get("RESULT_LIST");
				
				cHashMap.put(user, productIdList);
			}
		
			return cHashMap;
		}
	



}
