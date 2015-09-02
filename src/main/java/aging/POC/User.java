package aging.POC;

public class User {
	private long userId;
	private long notificationFlag;
	
	public User(long userId,
			long notificationFlag) {
		this.userId = userId;
		this.notificationFlag =  notificationFlag;
	}

	public long getUserId() { return this.userId; } 
	public long getNotificationFlag() { return this.notificationFlag; } 
}
