package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.util.OPAComplianceAgingEnum;
import aging.POC.deactivation.DeactivationBehavior;

@Component("deactivatePolicyEnforcer")
public class DeactivatePolicyEnforcer extends AgingPolicyEnforcer {

	private EntryManager entryManager;
	private List<User> userList;
	
	
	public DeactivatePolicyEnforcer() {
		this.entryManager =  new EntryManager(agedUserEntryRepository);
	}
	
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	public void enforcePolicy() {

		deactivateUsers(deactivateProductsUserIdListIsInvolvedWith(userList));
	}
	
	private void deactivateUsers(List<Long> userIdsToDeactivateList) {
		bulkUserDeactivateSP.execute(userIdsToDeactivateList.toString().replace("[", "").replace("]","")); 
	}
	
	private List<Long>deactivateProductsUserIdListIsInvolvedWith(List<User> userList) {
		List<Long> userIdsToDeactivateList = new ArrayList<Long>();
		
		//TODO: select * from productStatus where isComplete=0
		String productStatusSql = "SELECT id FROM productStatus WHERE isComplete=0";
		List<Map<String, Object>> productStatusListMap = jdbcTemplate.queryForList(productStatusSql);
		
		System.out.println("productStatusList from the DB: " + productStatusListMap.toString());
	
		//TODO: get rid of this line
		List<String> productStatusList = new ArrayList<String>( Arrays.asList("1","2","3","6","7","8") );
		
		ConcurrentHashMap<User, ArrayList<ProductId>> userProductIdsConcurrentHashMap = null;
		ConcurrentHashMap<String, ArrayList<String>> errorMessageMapForUsersWithProductsThatFailedDeactivation =  null;
		
		userProductIdsConcurrentHashMap = getProductsUsersAreInvolvedWith(productStatusList, userList);	
		errorMessageMapForUsersWithProductsThatFailedDeactivation = bulkDeactivateUserProducts(userProductIdsConcurrentHashMap);
		
		for (User userToDeactivate : userList) {
			//if (!errorMessageMapForUsersWithProductsThatFailedDeactivation.contains(userToDeactivate.getUserId()))
				userIdsToDeactivateList.add(new Long(userToDeactivate.getUserId()));
		}
		
		return userIdsToDeactivateList;
	}
	
	
    private ConcurrentHashMap<String, ArrayList<String>> bulkDeactivateUserProducts(
				ConcurrentHashMap<User, ArrayList<ProductId>> userProductsConcurrentHashMap) {
			

    		DeactivationBehavior deactivationBehavior = new DeactivationBehavior();
    		
    		//TODO : I'm building stuff here....where o where is my builder pattern?
    		//which one should I use -- REFACTOR is calling me....hear her voice in the distance.
    	    for (User user : userProductsConcurrentHashMap.keySet()) {
    	    	
    	    	
    	    	List<Integer> productsToSuspend = new ArrayList<Integer>();
        		ConcurrentHashMap<Long, Integer> productsToResubmitMap = new ConcurrentHashMap<Long, Integer>();
        		List<Integer> productsToReassign = new ArrayList<Integer>();
        		List<Integer> tasksToCancel = new ArrayList<Integer>();
        		List<ProductId> productIdList = userProductsConcurrentHashMap.get(user.getUserId());
        		
    	    	System.out.println("userId: " + user.getUserId());
    	    	

    	    	if (productIdList != null ) {
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
			    				user.setJdbcTemplate(jdbcTemplate);
			    				
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
    	    	
	    			System.out.println("number of products to resubmit -- user is an associate (MRCH) on these products w/an active licensee: " + productsToResubmitMap.size());
	    			System.out.println("number of products to reassign -- user is an associate (PUB) on these products: " + productsToReassign.size());
	    			System.out.println("number of products to suspend -- user is a licensee on these products: " + productsToSuspend.size());
	    			System.out.println("number of tasks to cancel -- user is a reviewer on these products: " + tasksToCancel.size());
	
	    			entryManager.addExpiryEntries(user,
	    					productIdList,
	    					productsToReassign,
	    					productsToResubmitMap,
	    					productsToSuspend,
	    					tasksToCancel);
	    
	    			
	    			/*if (!productsToSuspend.isEmpty()) {
	    				List<DeactivationMessage> suspensionErrorMessages = deactivationBehavior.suspendProducts(productsToSuspend, 
	    						user.getUserId(), 
	    						jdbcTemplate);
	    			}
	    			
	    			if (!productsToResubmitMap.isEmpty()) {
	    				List<DeactivationMessage> resubmissionErrorMessages = deactivationBehavior.resubmitProducts(productsToResubmitMap, 
	    						user.getUserId(), 
	    						jdbcTemplate);
	    			}
	    			
	    			if (!productsToReassign.isEmpty()) {
	    				List<DeactivationMessage> resassignmentErrorMessages = deactivationBehavior.reassignProducts(productsToReassign, 
	    						user.getUserId(), 
	    						jdbcTemplate);
	    			}
	    			
	    			if (!tasksToCancel.isEmpty()) {
	    				List<DeactivationMessage> cancellationErrorMessages = deactivationBehavior.cancelTasks(tasksToCancel, 
	    						user.getUserId(), 
	    						jdbcTemplate);
	    			}*/
	    			
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
    	
    	if (userTypeId.equals(OPAComplianceAgingEnum.LICENSEE.getValue()))
    		isLicensee = true;
    	
    	return isLicensee;
    }

	private ConcurrentHashMap<User, ArrayList<ProductId>> getProductsUsersAreInvolvedWith(
			    List<String> productStatusList,
				List<User> userList) {
		
		
			List<User> localCopyOfUserList = userList;
			ConcurrentHashMap<User, ArrayList<ProductId>> cHashMap = new ConcurrentHashMap<User, ArrayList<ProductId>>();
	
			for ( User user : localCopyOfUserList) {
				
				Map productsUserIsInvolvedWith = productsUserIsInvolvedWithSP.execute(productStatusList, user.getUserId());
				
				@SuppressWarnings("unchecked")
				ArrayList<ProductId> productIdList = (ArrayList<ProductId>) productsUserIsInvolvedWith.get("RESULT_LIST");
				
				cHashMap.put(user, productIdList);
			}
		
			return cHashMap;
		}
	



}
