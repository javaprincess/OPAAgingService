package aging.POC.enforcers;

import java.sql.CallableStatement;
import java.sql.ResultSet;
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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.AgedUserEntry;
import aging.POC.AgedUserNotificationEntry;
import aging.POC.User;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.FindUserAgingCandidatesSP;
import aging.POC.storedprocedures.ProductId;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
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
			//System.out.println(element.getJsonData().getUser().getUserId());
			//System.out.println(element.getJsonData().getUser().getNotificationFlag());
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
			//System.out.println(element.getJsonData().getUser().getUserId());
			//System.out.println(element.getJsonData().getUser().getNotificationFlag());
			
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
				//System.out.println(element.getJsonData().getUser().getUserId());
				//System.out.println(element.getJsonData().getUser().getNotificationFlag());
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
				//System.out.println(element.getJsonData().getUser().getUserId());
				//System.out.println(element.getJsonData().getUser().getNotificationFlag());
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
		List<String> userIdList = new ArrayList<String> ( Arrays.asList("24292", "24745", "12768", "12767", "12683", "12772"));
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
			//for each product, we need to find out what the user's role is in the product
    		//if MRCH associate w/active licensee, resubmit the product else, suspend the product
    	    //if PUB associate, reassign product to the PUBHOLDING account
    	    //if licensee, suspend products
    	    //if reviewwer, cancel tasks
    	
    		
    		while (userProductsConcurrentHashMap.keys().hasMoreElements()) {
	    		//--check for licensee or associate products
	    		//select userTypeId from productState where productId = 11919
    			String userId = userProductsConcurrentHashMap.keys().nextElement();
    			ArrayList<ProductId> productIdList = userProductsConcurrentHashMap.get(userId);
    			
    			for (ProductId productId : productIdList) {
    			
	    			
	    			
		    		String sql = "Select userTypeId, productId from productState where productId=?";
		    		
		    		
		    		//--check for reviewer tasks
		    		//select * from productReviews where productStateId in (select id from productState where productId = 11919)
		    		String reviewerSql = "select * from productReviews where productStateId in (select id from productState where productId = ?)";
		    		
		    		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, productId.getProductId());
		    		List<Map<String, Object>> reviewerRows = jdbcTemplate.queryForList(reviewerSql, productId.getProductId());
		    		
		    		System.out.println("userId: " + userId);
		    		System.out.println("productId (incoming): " + productId.getProductId());
		    		
		    		
		    		if (!reviewerRows.isEmpty()) {
		    			System.out.println("user is a reviewer on this product");
		    		}
		    		
		    		for (Map<String, Object> rowElement : rows) {
		    			
		    			
		    			System.out.println(rowElement.get("productId") + " from query");
		    			
		    			
		    			
		    			if (isAssociate((Integer)rowElement.get("userTypeId")))
		    				System.out.println("user is an associate on this product");
		    			else if (isLicensee((Integer)rowElement.get("userTypeId"))) 
		    				System.out.println("user is a licensee on this product");
		    			
		    			System.out.println("------------------------");
		    		}
		    		
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
				
			//Iterator iterator = result.iterator();
				
			//while (iterator.hasNext()) {
			//	ProductId productId = (ProductId) iterator.next();
			//	System.out.println("results: " + productId.getProductId());
			//}
				
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
