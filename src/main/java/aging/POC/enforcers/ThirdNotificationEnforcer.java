package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserDeactivationEntry;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.util.OPAComplianceAgingConstants;

@Component("thirdNotificationEnforcer")
public class ThirdNotificationEnforcer extends AgingPolicyEnforcer  {

	//TODO: this injection causes a BeanCreationException on startup..gotta figure out why
	//@Resource(name="emailUtils")
	//EmailUtils emailUtils;
	
	private String enforcerName;
	private Integer notificationFlag;
	private String nextEnforcerToCall;


	public ThirdNotificationEnforcer() {
		
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
		Integer age = new Integer(OPAComplianceAgingConstants.DELTA_2.getNotificationDelta());
		Integer notificationFlag = new Integer(OPAComplianceAgingConstants.DELTA_3.getNotificationDelta());
	
		List<AgedUserEntry> notificationList = agedUserEntryRepository.findAllAgingCandidatesByAge(90-age);
		List<String> userIdList = new ArrayList<String>();

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
				
			//agedUserEntryRepository.save(new AgedUserNotificationEntry().createEntry(user));
			agedUserEntryRepository.save(AgedUserEntry.createEntry(user, new AgedUserNotificationEntry("ENFORCE_WARNING_NOTIFICATION")));
		}
			
		bulkUserNotificationFlagUpdate(userIdList, notificationFlag);
		/*try {
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
