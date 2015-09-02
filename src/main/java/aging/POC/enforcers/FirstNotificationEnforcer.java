package aging.POC.enforcers;

import aging.POC.AgingPolicy;
import aging.POC.AgingPolicyEnforcer;
import aging.POC.AgingPolicyTarget;
import aging.POC.UserAgingPolicyTarget;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.LinkedCaseInsensitiveMap;

import aging.POC.AgedUserNotificationEntry;
import aging.POC.User;


@SpringBootApplication
public class FirstNotificationEnforcer extends AgingPolicyEnforcer implements CommandLineRunner  {

		
		@Autowired
		private AgedUserEntryRepository auRepo;
		
	
		private String enforcerName;
		private Integer notificationFlag;
		private AgingPolicyTarget agingPolicyTarget;
		private String nextEnforcerToCall;
	
		
		public static void main(String[] args) {
			SpringApplication.run(FirstNotificationEnforcer.class, args);
		}
		
		@Override
		public void run(String... args) throws Exception {
			enforcePolicy();
		
		}
		
		private void putAgingCandidatesOnQueue(ArrayList<LinkedCaseInsensitiveMap<Integer>> resultSetMapElement) {
			for (LinkedCaseInsensitiveMap<Integer> mapMember : resultSetMapElement) {
				
				//TO DO: verify userId is not currently in the OPAQueue
				//TO DO: create OPAQueue.exists(userId)
				
				User user =  new User(
								new Long(mapMember.get("userId").toString()).longValue(),
								new Long(mapMember.get("notificationFlag").toString()).longValue()
							 );
				
				auRepo.save(new AgedUserNotificationEntry().createEntry(user));

			}
		}

	public FirstNotificationEnforcer() {
		setEnforcerName(this.getClass().getName());
		setNextEnforcerToCall(new String("SecondNotificationEnforcer"));
		setAgingPolicyTarget(new UserAgingPolicyTarget());
		setNotificationStatus(new Integer(60));
	}
	
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

	@Override
	protected boolean isValidAgingCandidate() {
		// TODO Auto-generated method stub
		return false;
	}

}
