package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.util.OPAComplianceAgingEnum;
import aging.POC.deactivation.DeactivationBehavior;

@Component("deactivatePolicyEnforcer")
public class DeactivatePolicyEnforcer extends AgingPolicyEnforcer {

	//private EntryManager entryManager;
	private List<User> userList;
	
	
	public DeactivatePolicyEnforcer() {
		
	}
	
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	public void enforcePolicy() {
		
		//EntryManager entryManager = new EntryManager(agedUserEntryRepository);
			
		List<AgedUserEntry> agedUserEntryExpiryList = agedUserEntryRepository.findAllAgingCandidatesByAge(90);
		List<User> expiryList = new ArrayList<User>();
		
		for (AgedUserEntry userToExpire : agedUserEntryExpiryList) 
			expiryList.add(userToExpire.getJsonData().getUser());
		
		
		System.out.println("looking for " + 90  + " day aging candidate matches: " + expiryList.size());
		deactivateUsers(deactivateProductsUserIdListIsInvolvedWith(expiryList));
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
		
		List<User> userWithProductList = null;
		ConcurrentHashMap<String, ArrayList<String>> errorMessageMapForUsersWithProductsThatFailedDeactivation =  null;
		
		userWithProductList = getProductsUsersAreInvolvedWith(productStatusList, userList);	
		errorMessageMapForUsersWithProductsThatFailedDeactivation = bulkDeactivateUserProducts(userWithProductList);
		
		//for (User userToDeactivate : userList) {
			//if (!errorMessageMapForUsersWithProductsThatFailedDeactivation.contains(userToDeactivate.getUserId()))
				//userIdsToDeactivateList.add(new Long(userToDeactivate.getUserId()));
		//}
		
		return userIdsToDeactivateList;
	}
	
	
    private ConcurrentHashMap<String, ArrayList<String>> bulkDeactivateUserProducts(
    		List<User> usersWithProductList) {
			
    		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
    		DeactivationBehavior deactivationBehavior = new DeactivationBehavior();
    		
    		//TODO : I'm building stuff here....where o where is my builder pattern?
    		//which one should I use -- REFACTOR is calling me....hear her voice in the distance.
    	    for (User user : usersWithProductList) {
    	    	
    	    	
    	    	List<Integer> productsToSuspend = new ArrayList<Integer>();
        		ConcurrentHashMap<Integer, Integer> productsToResubmitMap = new ConcurrentHashMap<Integer, Integer>();
        		List<Integer> productsToReassign = new ArrayList<Integer>();
        		List<Integer> tasksToCancel = new ArrayList<Integer>();
        		List<ProductId> productIdList = user.getProductIdList();
        		
    	    	System.out.println("userId: " + user.getUserId());
    	    	System.out.println("productList in bulkDeactivateUserProducts: " + productIdList.toString());
    	    	

    	    	if (productIdList != null ) {
    	    		System.out.println("productIdList size in bulkDeactivateUserProducts: " + productIdList.size());
	    			for (ProductId productIdListElement : productIdList) {
	
	    				Integer productId = productIdListElement.getProductId();
	    				
	    				//--check for licensee or associate products
			    		String associateOrLicenseeSql = "SELECT DISTINCT userTypeId, productId FROM productState WHERE productId=? and ownerUserId > 0";
	
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
	
	    			user.setProductIdList(productIdList);
	    			user.setProductsToReassign(productsToReassign);
	    			user.setProductsToResubmit(productsToResubmitMap);
	    			user.setProductsToSuspend(productsToSuspend);
	    			user.setTasksToCancel(tasksToCancel);
	    			
	    			entryManager.addExpiryEntry(user);
	    
	    			
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

	private List<User> getProductsUsersAreInvolvedWith(
			    List<String> productStatusList,
				List<User> userList) {
		
		
			List<User> localCopyOfUserList = userList;
			
			System.out.println(userList.size());
			
			List<User> userListWithProducts = new ArrayList<User>();
	
			for ( User user : localCopyOfUserList) {
				
				
				//this condidtion is to limit the population size of users to deactivate during testing
				if ((( user.getUserId() < 20500 ) && (user.getUserId() > 20400))) {
					System.out.println("userId in DeactivationPolicyEnforcer: " + user.getUserId());
					Map productsUserIsInvolvedWith = productsUserIsInvolvedWithSP.execute(productStatusList, user.getUserId());
				
					@SuppressWarnings("unchecked")
					ArrayList<ProductId> productIdList = (ArrayList<ProductId>) productsUserIsInvolvedWith.get("RESULT_LIST");
				
					System.out.println("productIdList.size(): " + productIdList.size());
					user.setProductIdList(productIdList);
					userListWithProducts.add(user);
				}
			}
		
			return userListWithProducts;
		}
	



}
