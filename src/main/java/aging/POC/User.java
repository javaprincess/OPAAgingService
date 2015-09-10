package aging.POC;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import aging.POC.storedprocedures.rowmappers.ProductId;

public class User {
	private long userId;
	private long notificationFlag;
	private long isPub;
	private long activeLicensee;
	private List<ProductId> productIdList;
	private List<Integer> productsToSuspend;
	private ConcurrentHashMap<Long, Integer> productsToResubmitMap;
	private List<Integer> productsToReassign;
	private List<Integer> tasksToCancel;
	
	public User(long userId,
			long notificationFlag,
			long isPub) {
		this.userId = userId;
		this.notificationFlag =  notificationFlag;
		this.isPub = isPub;
	}

	public long getUserId() { return this.userId; } 
	public long getNotificationFlag() { return this.notificationFlag; } 
	public List<ProductId> getProductIdList() { return this.productIdList; }
	public List<Integer> getProductsToSuspend() {return this.productsToSuspend; }
	public ConcurrentHashMap<Long, Integer> getProductsToResubmit() {return this.productsToResubmitMap; }
	public List<Integer> getProductsToReassign() {return this.productsToReassign; }
	public List<Integer> getTasksToCancel() { return this.tasksToCancel; }
	public long getIsPub() { return this.isPub; }
	public long getActiveLicensee() { return this.activeLicensee; }
	
	public void setProductIdList(List<ProductId> productIdList) { this.productIdList = productIdList; }
	public void setProductsToSuspend(List<Integer> suspendList) {this.productsToSuspend = suspendList; }
	public void setProductsToResubmit(ConcurrentHashMap<Long, Integer> resubmitMap) {this.productsToResubmitMap = resubmitMap; }
	public void setProductsToReassign(List<Integer> reassignList) {this.productsToReassign = reassignList; }
	public void setTasksToCancel(List<Integer> cancelList) {this.tasksToCancel = cancelList; }
	public void setActiveLicensee(long activeLicensee) { this.activeLicensee = activeLicensee; }
	
	public boolean isPubAssociate() {
		boolean isPubAssociate = false;
		
		if (this.isPub == 1) 
			isPubAssociate = true;
		
		return isPubAssociate;
	}
	
	public boolean hasAnActiveLicenseeForProduct(long productId) {
		boolean hasAnActiveLicenseeForProduct = false;
		
		//TODO: figure out how to implement this pseudo code
		//if (something) {
		//  get the activeLicensee
		//  setActiveLicensee(activeLicensee);
		setActiveLicensee(2);
		//	hasAnActiveLicenseeForProduct = true;
		//}
		
		return hasAnActiveLicenseeForProduct;
	}
}
