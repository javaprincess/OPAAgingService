package aging.POC.messages;

import java.util.List;

public class ErrorMessage {
	private String userId;
	private List<Integer> listOfProductIdsThatFailedDeactivation;
	private String errorMessage;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Integer> getListOfProductIdsThatFailedDeactivation() {
		return listOfProductIdsThatFailedDeactivation;
	}
	public void setListOfProductIdsThatFailedDeactivation(
			List<Integer> listOfProductIdsThatFailedDeactivation) {
		this.listOfProductIdsThatFailedDeactivation = listOfProductIdsThatFailedDeactivation;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

}
