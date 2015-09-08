package aging.POC;

import java.util.List;

import aging.POC.storedprocedures.rowmappers.ProductId;

public class User {
	private long userId;
	private long notificationFlag;
	private List<ProductId> productIdList;
	private List<Integer> productsToSuspend;
	private List<Integer> productsToResubmit;
	private List<Integer> productsToReassign;
	private List<Integer> tasksToCancel;
	
	public User(long userId,
			long notificationFlag) {
		this.userId = userId;
		this.notificationFlag =  notificationFlag;
	}

	public long getUserId() { return this.userId; } 
	public long getNotificationFlag() { return this.notificationFlag; } 
	public List<ProductId> getProductIdList() { return this.productIdList; }
	public List<Integer> getProductsToSuspend() {return this.productsToSuspend; }
	public List<Integer> getProductsToResubmit() {return this.productsToResubmit; }
	public List<Integer> getProductsToReassign() {return this.productsToReassign; }
	public List<Integer> getTasksToCancel() { return this.tasksToCancel; }
	
	public void setProductIdList(List<ProductId> productIdList) { this.productIdList = productIdList; }
	public void setProductsToSuspend(List<Integer> suspendList) {this.productsToSuspend = suspendList; }
	public void setProductsToResubmit(List<Integer> resubmitList) {this.productsToResubmit = resubmitList; }
	public void setProductsToReassign(List<Integer> reassignList) {this.productsToReassign = reassignList; }
	public void setTasksToCancel(List<Integer> cancelList) {this.tasksToCancel = cancelList; }
}
