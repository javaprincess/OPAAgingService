package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserDeactivationEntry;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.storedprocedures.BulkUserDeactivateSP;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.storedprocedures.ProductsUserIsInvolvedWithSP;
import aging.POC.util.OPAComplianceAgingConstants;

@Component("fourthNotificationEnforcer")
public class FourthNotificationEnforcer extends AgingPolicyEnforcer  {

	@Resource(name="deactivate")
	Deactivate deactivate;
	
	private String enforcerName;
	private Integer notificationFlag;
	private String nextEnforcerToCall;
		

	public FourthNotificationEnforcer() {
		
	}

	public void setEnforcerName(String name) {
		this.enforcerName = name;
	}
	
	protected boolean isValidAgingCandidate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void enforcePolicy() {
		
		long isPub;
		Integer age = new Integer(OPAComplianceAgingConstants.DELTA_3.getNotificationDelta());
		Integer notificationFlag = new Integer(OPAComplianceAgingConstants.DELTA_4.getNotificationDelta());
			
		List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(90-age);
		List<String> userIdList = new ArrayList<String>();
		List<User> userList = new ArrayList<User>();
			
		System.out.println("looking for " + notificationFlag + " day aging candidate matches: " + notificationList.size());
			
		for (AgedUserEntry element : notificationList) {
			isPub = element.getJsonData().getUser().getIsPub();
			userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
				
			System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
			User user =  new User(
							new Long(element.getJsonData().getUser().getUserId()),
							90-notificationFlag,
							isPub
							);
				
			userList.add(user);
			//agedUserEntryRepository.save(new AgedUserDeactivationEntry().createEntry(user));
			agedUserEntryRepository.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
		}
			
		bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
		/*try {
			EmailUtils emailUtils =  new EmailUtils();
			emailUtils.sendNotificationEMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			
		deactivate(userList);
	}


	private void deactivate(List<User> userList) {
		//List<String> userIdList = new ArrayList<String> ( Arrays.asList("24294", "24745", "90", "12771", "54", "91","5"));
		List<String> userIdList = new ArrayList<String> ( Arrays.asList("10"));
		deactivate.setUserList(userList);
		deactivate.setUserIdList(userIdList);
		deactivate.enforcePolicy();
		
		
	}
	
	public void bulkUserNotificationFlagUpdate(List<String> notificationList, Integer notificationFlag) {
		bulkUserNotificationFlagUpdateSP.execute(notificationList, notificationFlag);
	}
}
