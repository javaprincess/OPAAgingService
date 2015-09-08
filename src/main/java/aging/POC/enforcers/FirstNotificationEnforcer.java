package aging.POC.enforcers;

import java.util.ArrayList;
import java.util.List;

import aging.POC.User;
import aging.POC.deleteThis.AgingPolicy;
import aging.POC.deleteThis.AgingPolicyEnforcer;
import aging.POC.deleteThis.AgingPolicyTarget;
import aging.POC.deleteThis.UserAgingPolicyTarget;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.queue.entry.AgedUserNotificationEntry;
import aging.POC.storedprocedures.BulkUserNotificationFlagUpdateSP;
import aging.POC.util.OPAComplianceAgingConstants;


public class FirstNotificationEnforcer extends AgingPolicyEnforcer  {
	
	private AgedUserEntryRepository auRepo;
	private String enforcerName;
	private Integer notificationFlag;
	private AgingPolicyTarget agingPolicyTarget;
	private String nextEnforcerToCall;
		
	private  BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP;

	public FirstNotificationEnforcer(BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP,
			AgedUserEntryRepository auRepo) {
		setAgedUserEntryRepository(auRepo);
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(new String("SecondNotificationEnforcer"));
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(new Integer(60));
		setBulkUserNotificationFlagUpdateSP(bulkUserNotificationFlagUpdateSP);
	}
	
	

	public void setAgedUserEntryRepository(AgedUserEntryRepository auRepo) { this.auRepo = auRepo;}


	public void setEnforcerName(String name) {
		this.enforcerName = name;
	}
	
	public void setAgingPolicyTarget(AgingPolicyTarget target) {
		this.agingPolicyTarget = target;
	}
	
	public void setNotificationStatus(Integer notification) {
		this.notificationStatus = notification;
	}
	
	public void setAgingPolicy(AgingPolicy policy) {
		this.agingPolicy = policy;
	}
	
	public void setNextEnforcerToCall(String nextEnforcer) {
		this.nextEnforcerToCall = nextEnforcer;
	}

	private void setBulkUserNotificationFlagUpdateSP(
			BulkUserNotificationFlagUpdateSP bulkUserNotificationFlagUpdateSP) {
		this.bulkUserNotificationFlagUpdateSP = bulkUserNotificationFlagUpdateSP;
		
	}
	
	@Override
	protected boolean isValidAgingCandidate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//FirstNotificationEnforcer.enforcePolicy()
	public void enforcePolicy() {
		
			Integer age = new Integer(0);
			Integer notificationFlag = new Integer(OPAComplianceAgingConstants.DELTA_1.getNotificationDelta());
			
			List<AgedUserEntry> notificationList = auRepo.findAllAgingCandidatesByAge(age);
			List<String> userIdList = new ArrayList<String>();
			
			
			
			System.out.println("looking for " + notificationFlag + " aging candidate matches: " + notificationList.size());
			
			for (AgedUserEntry element : notificationList) {
				
				userIdList.add(new Long(element.getJsonData().getUser().getUserId()).toString());
				
				System.out.println("putting this on the queue: " +element.getJsonData().getUser().getUserId());
				User user =  new User(
								new Long(element.getJsonData().getUser().getUserId()),
								notificationFlag
							 );
				
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));
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
