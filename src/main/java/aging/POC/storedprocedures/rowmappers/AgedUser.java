package aging.POC.storedprocedures.rowmappers;

public class AgedUser {

	private Integer userId;
	private Integer notificationFlag;
	private Integer isPub;
	
	public Integer getUserId() {
		return userId;
	}

	public Integer getNotificationFlag() {
		return notificationFlag;
	}

	public Integer getIsPub() {
		return isPub;
	}

	public void setUserId(Integer userId) { this.userId = userId; }
	public void setNotificationFlag(Integer notificationFlag) {this.notificationFlag = notificationFlag; }
	public void setIsPub(Integer isPub) { this.isPub =  isPub; }
	
}
