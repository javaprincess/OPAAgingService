package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.queue.entry.EntryManager;
import aging.POC.storedprocedures.rowmappers.AgedUser;
import aging.POC.util.OPAComplianceAgingEnum;

@Component("fourthNotificationEnforcer")
public class FourthNotificationEnforcer extends AgingPolicyEnforcer  {

	@Resource(name="deactivatePolicyEnforcer")
	DeactivatePolicyEnforcer deactivatePolicyEnforcer;
	
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
		Integer age = new Integer(OPAComplianceAgingEnum.DELTA_3.getValue());
		Integer notificationFlag = new Integer(OPAComplianceAgingEnum.DELTA_4.getValue());
		EntryManager entryManager = new EntryManager(agedUserEntryRepository);
			
		List<AgedUser> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(90-age);
		List<String> userIdList = new ArrayList<String>();
		List<User> userList = new ArrayList<User>();
			
		System.out.println("looking for " + notificationFlag + " day aging candidate matches: " + notificationList.size());
			
		/*for (AgedUser element : notificationList) {
			isPub = element.getIsPub();
			userIdList.add(new Long(element..getUserId()).toString());
				
			System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
			User user =  new User(
							new Long(element.getJsonData().getUser().getUserId()),
							90-notificationFlag,
							isPub
							);
				
			userList.add(user);
			
			agedUserEntryRepository.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
		}*/
			
		entryManager.addWarningEntries(notificationList);
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
		
		deactivatePolicyEnforcer.setUserList(userList);
		deactivatePolicyEnforcer.enforcePolicy();

	}
	
	public void bulkUserNotificationFlagUpdate(List<String> notificationList, Integer notificationFlag) {
		bulkUserNotificationFlagUpdateSP.execute(notificationList, notificationFlag);
	}
}
