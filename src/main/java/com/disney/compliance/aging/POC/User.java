package com.disney.compliance.aging.POC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.JdbcTemplate;

import com.disney.compliance.aging.POC.storedprocedures.rowmappers.ProductId;
import com.disney.compliance.aging.POC.util.OPAComplianceAgingEnum;

public class User {
	private Integer userId;
	private Integer notificationFlag;
	private Integer isPub;
	private Integer activeLicensee;
	private List<ProductId> productIdList;
	private List<Integer> productsToSuspend;
	private ConcurrentHashMap<Integer, Integer> productsToResubmitMap;
	private List<Integer> productsToReassign;
	private List<Integer> tasksToCancel;
	private JdbcTemplate jdbcTemplate;
	
	
	public User() {
		
	}
	
	public User(Integer userId,
			Integer notificationFlag,
			Integer isPub) {
		this.userId = userId;
		this.notificationFlag =  notificationFlag;
		this.isPub = isPub;
	}

	public Integer getUserId() { return this.userId; } 
	public Integer getNotificationFlag() { return this.notificationFlag; } 
	public List<ProductId> getProductIdList() { return this.productIdList; }
	public List<Integer> getProductsToSuspend() {return this.productsToSuspend; }
	public ConcurrentHashMap<Integer, Integer> getProductsToResubmit() {return this.productsToResubmitMap; }
	public List<Integer> getProductsToReassign() {return this.productsToReassign; }
	public List<Integer> getTasksToCancel() { return this.tasksToCancel; }
	public Integer getIsPub() { return this.isPub; }
	public Integer getActiveLicensee() { return this.activeLicensee; }
	
	public void setProductIdList(List<ProductId> productIdList) { this.productIdList = productIdList; }
	public void setProductsToSuspend(List<Integer> suspendList) {this.productsToSuspend = suspendList; }
	public void setProductsToResubmit(ConcurrentHashMap<Integer, Integer> resubmitMap) {this.productsToResubmitMap = resubmitMap; }
	public void setProductsToReassign(List<Integer> reassignList) {this.productsToReassign = reassignList; }
	public void setTasksToCancel(List<Integer> cancelList) {this.tasksToCancel = cancelList; }
	public void setActiveLicensee(Integer activeLicensee) { this.activeLicensee = activeLicensee; }
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
	public void setUserId(Integer userId) { this.userId = userId; }
	public void setNotificationFlag(Integer notificationFlag) {this.notificationFlag = notificationFlag; }
	public void setIsPub(Integer isPub) { this.isPub =  isPub; }
	
	public boolean isPubAssociate() {
		boolean isPubAssociate = false;
		
		if (isPub == 1) 
			isPubAssociate = true;
		
		return isPubAssociate;
	}
	
	public boolean hasAnActiveLicenseeForProduct(long productId) {
		boolean hasAnActiveLicenseeForProduct = false;
		
		String hasAnActivateLicenseeForProductSql = "SELECT DISTINCT ownerUserId FROM productState WHERE productId=? and userTypeId=?";

		List<Map<String, Object>> activeLicensees = jdbcTemplate.queryForList(hasAnActivateLicenseeForProductSql, productId, OPAComplianceAgingEnum.LICENSEE.getValue());
		
		if (activeLicensees.size() > 0) {
			System.out.println("the active licensee for this product: " + (Integer)activeLicensees.get(0).get("ownerUserId"));
			
			setActiveLicensee((Integer)activeLicensees.get(0).get("ownerUserId"));
			hasAnActiveLicenseeForProduct=true;
		}
	
		return hasAnActiveLicenseeForProduct;
	}
}
