package aging.POC.enforcers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.AgedUserEntry;
import aging.POC.AgedUserNotificationEntry;
import aging.POC.User;
import aging.POC.messages.ErrorMessage;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.storedprocedures.rowmappers.DeactivationMessage;
import aging.POC.storedprocedures.rowmappers.ProductId;
import aging.POC.unnamedbehavior.TheUnnamedDeactivationBehavior;
import aging.POC.util.EmailUtils;

@SpringBootApplication
public class AgingPolicyEnforcer implements CommandLineRunner {

	
	@Autowired
	private AgedUserEntryRepository auRepo;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate; 
	
	private FindUserAgingCandidatesSP findUserAgingCandidatesSP; 
	private BulkUserDeactivateSP bulkUserDeactivateSP;
	private BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;
	private ProductsUserIsInvolvedWithSP productsUserIsInvolvedWithSP;
	
	
	
	
	@Autowired
	public void setDataSource(DataSource source){ 
		this.jdbcTemplate = new JdbcTemplate(source); 
		this.findUserAgingCandidatesSP = new FindUserAgingCandidatesSP(jdbcTemplate.getDataSource()); 
		this.bulkUserDeactivateSP =  new BulkUserDeactivateSP(jdbcTemplate.getDataSource());
		this.bulkUserNotificationFlagUpdateSP = new BulkUserNotificationFlagUpdateSP(jdbcTemplate.getDataSource()); 
		this.productsUserIsInvolvedWithSP = new ProductsUserIsInvolvedWithSP(jdbcTemplate.getDataSource());
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AgingPolicyEnforcer.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		
		
		//agingPolicyScanner();
		//enforcePolicyFirstNotificationEnforcer();
		//enforcePolicySecondNotificationEnforcer();
		//enforcePolicyThirdNotificationEnforcer();
		//enforcePolicyFourthNotificationEnforcer();
		deactivate();

	}
	
	private void agingPolicyScanner() {

		Map<String, Object> resultSet =  findUserAgingCandidatesSP.execute();
		Iterator<Map.Entry<String, Object>> resultSetIterator = resultSet.entrySet().iterator();
	
		while (resultSetIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement = (ArrayList<LinkedCaseInsensitiveMap<Integer>>)resultSetIterator.next().getValue();
			putAgingCandidatesOnQueue(resultSetMapElement);
			
		}
	}
	
	private void putAgingCandidatesOnQueue(ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement) {
		System.out.println("number of aging candidates found: " + resultSetMapElement.size());
		
		for (LinkedCaseInsensitiveMap<Integer> mapMember : resultSetMapElement) {
			
			long userId = mapMember.get("userid");
			long notificationFlag = mapMember.get("notificationFlag");
			
			//TO DO: verify userId is not currently in the OPAQueue
			//TO DO: create OPAQueue.exists(userId)
			if (isUserOnOPAQueue(userId))
				System.out.println("user: " + mapMember.get("userId") + " exists in the OPAQueue");
			else {
				System.out.println("putting this user: " + mapMember.get("userId").toString() + " on the OPAQueue");
				System.out.println("notificationFlag: " + mapMember.get("notificationFlag").toString() + " on the OPAQueue");
				User user =  new User(
							userId,
							notificationFlag);
			
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
			}
		}
	}
	
	//FirstNotificationEnforcer.enforcePolicy()
	private void enforcePolicyFirstNotificationEnforcer() {
	
		Integer age = new Integer(0);
		Integer notificationFlag = new Integer(60);
		
		List<AgedUserEntry> notificationList = auRepo.findAllAgingCandidatesByAge(age);
		List<String> userIdList = new ArrayList<String>();
		
		
		
		System.out.println("looking for new aging candidate matches: " + notificationList.size());
		
		for (AgedUserEntry element : notificationList) {
			
			userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
			
			System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
			User user =  new User(
							new Long(element.getJsonData().getUser().getUserId()),
							new Long(element.getJsonData().getUser().getUserId())
						 );
			
			auRepo.save(new AgedUserNotificationEntry().createEntry(user));
		}
		
		bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
		//sendEmail(firstNotificationList);
	}
	
	

	//SecondNotificationEnforcer.enforcePolicy()
	private void enforcePolicySecondNotificationEnforcer() {
		
		Integer age = new Integer(60);
		Integer notificationFlag = new Integer(75);
		
		List<AgedUserEntry> notificationList = auRepo.findAllAgingCandidatesByAge(age);
		List<String> userIdList = new ArrayList<String>();
		
		
		System.out.println("looking for 60 day matches: " + notificationList.size());
		
		for (AgedUserEntry element : notificationList) {
			
			
			//build list of userIds to update in RDBMS
			userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
			
			//build User to add to NoSQL
			System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
			User user =  new User(
							new Long(element.getJsonData().getUser().getUserId()),
							new Long(element.getJsonData().getUser().getUserId())
						 );
			
			auRepo.save(new AgedUserNotificationEntry().createEntry(user));
		}
		
		bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
		//sendEmail(firstNotificationList);
	}
	
	//ThirdNotificationEnforcer.enforcePolicy()
	private void enforcePolicyThirdNotificationEnforcer() {
		
			Integer age = new Integer(75);
			Integer notificationFlag = new Integer(85);
		
			List<AgedUserEntry> notificationList = auRepo.findAllAgingCandidatesByAge(age);
			List<String> userIdList = new ArrayList<String>();
			
			
			System.out.println("looking for 75 day matches: " + notificationList.size());
			
			for (AgedUserEntry element : notificationList) {
				
				//build list of userIds to update in RDBMS
				userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
				
				//build User to add to NoSQL
				System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
				User user =  new User(
								new Long(element.getJsonData().getUser().getUserId()),
								new Long(element.getJsonData().getUser().getUserId())
							 );
				
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
			}
			
			bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
			//sendEmail(firstNotificationList);
		} 
	
		//FourthNotificationEnforcer.enforcePolicy()
		private void enforcePolicyFourthNotificationEnforcer() {
			
			Integer age = new Integer(85);
			Integer notificationFlag = new Integer(90);
			List<String> usersToDeactivateList = new ArrayList<String>();
			
			List<AgedUserEntry> agedUserEntryNotificationList = auRepo.findAllAgingCandidatesByAge(age);
			List<String> userIdList = new ArrayList<String>();
			
			
			
			System.out.println("looking for 85 day matches: " + agedUserEntryNotificationList.size());
			
			for (AgedUserEntry element : agedUserEntryNotificationList) {
				
				//build list of userIds to update in RDBMS
				userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
				
				//build User to add to NoSQL
				System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
				User user =  new User(
								new Long(element.getJsonData().getUser().getUserId()),
								new Long(element.getJsonData().getUser().getUserId())
							 );
				
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
			}
			
			bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
			
			try {
				EmailUtils emailUtils =  new EmailUtils();
				emailUtils.sendNotificationEMail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	private void deactivate() {
		List<String> userIdList = new ArrayList<String> ( Arrays.asList("24294", "24745", "90", "12771", "54", "91","5"));
		List<String> productStatusList = new ArrayList<String>( Arrays.asList("1","2","3","6","7","8") );
		
		ConcurrentHashMap<String, ArrayList<ProductId>> userProductsConcurrentHashMap = getProductsUsersAreInvolvedWith(productStatusList, userIdList);
		ConcurrentHashMap<String, ArrayList<String>> errorMessageMapForUsersWithProductsThatFailedDeactivation = bulkDeactivateUserProducts(userProductsConcurrentHashMap);
		
		/*for (String userId : userIdList) {
			if (!errorMessageMapForUsersWithProductsThatFailedDeactivation.contains(userId))
				usersToDeactivateList.add(userId);
		}
		
		bulkDeactivateUsers(usersToDeactivateList); */
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

	private boolean isUserOnOPAQueue(long userId) {
    	boolean exists = false;
    	
    	List<AgedUserEntry> userEntry = auRepo.findByUserId(userId);
    	
    	if (userEntry.size() > 0)
    		exists = true;
    	
    	return exists;
    }
    
	private void bulkUserNotificationFlagUpdate(List<String> notificationList, Integer notificationFlag) {
		bulkUserNotificationFlagUpdateSP.execute(notificationList, notificationFlag);
	}
	
	private void bulkDeactivateUsers(List<String> userIdList) {
		bulkUserDeactivateSP.execute(userIdList);
	}

}
