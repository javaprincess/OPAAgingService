package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.util.OPAComplianceAgingConstants;

@Component("firstNotificationEnforcer")
public class FirstNotificationEnforcer extends AgingPolicyEnforcer  {
	
	private String enforcerName;
	private Integer notificationFlag;
	private String nextEnforcerToCall;

	public FirstNotificationEnforcer() {
		
	}

	public void setEnforcerName(String name) {
		this.enforcerName = name;
	}
	
	public void setNextEnforcerToCall(String nextEnforcer) {
		this.nextEnforcerToCall = nextEnforcer;
	}
	
	protected boolean isValidAgingCandidate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void enforcePolicy() {
		
			Integer age = new Integer(0);
			System.out.println(OPAComplianceAgingConstants.DELTA_1.getNotificationDelta());
			
			Integer notificationFlag = new Integer(OPAComplianceAgingConstants.DELTA_1.getNotificationDelta());
			
			List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(age);
			List<String> userIdList = new ArrayList<String>();
			
			System.out.println("looking for " + notificationFlag + " aging candidate matches: " + notificationList.size());
			
			for (AgedUserEntry element : notificationList) {
				
				userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
				
				System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
				User user =  new User(
								new Long(element.getJsonData().getUser().getUserId()),
								notificationFlag
							 );
				
				agedUserEntryRepository.save(new AgedUserNotificationEntry().createEntry(user));
			}
			
			bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
			/*try {
				EmailUtils emailUtils =  new EmailUtils();
				emailUtils.sendNotificationEMail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}


	public void bulkUserNotificationFlagUpdate(List<String> notificationList, Integer notificationFlag) {
		bulkUserNotificationFlagUpdateSP.execute(notificationList, notificationFlag);
	}
}
