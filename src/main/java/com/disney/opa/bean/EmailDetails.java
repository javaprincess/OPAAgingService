package com.disney.opa.bean;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class EmailDetails  implements Serializable
{
	//Properties
	private String toAddresses;
	private String fromAddress;
	private String subjectContactInfo;
	private String smtpHost;
	private String environment;
	private String emailPurpose;
	private int statusId;
	private Date creationDate;
	private Date modifiedDate;
	
	//Getters and Setters
	
	/**
	 * @return the toAddresses
	 */
	public String getToAddresses() {
		return toAddresses;
	}
	/**
	 * @param toAddresses the toAddresses to set
	 */
	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}
	
	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}
	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	/**
	 * @return the subjectContactInfo
	 */
	public String getSubjectContactInfo() {
		return subjectContactInfo;
	}
	/**
	 * @param subjectContactInfo the subjectContactInfo to set
	 */
	public void setSubjectContactInfo(String subjectContactInfo) {
		this.subjectContactInfo = subjectContactInfo;
	}
	
	/**
	 * @return the smtpHost
	 */
	public String getSmtpHost() {
		return smtpHost;
	}
	/**
	 * @param smtpHost the smtpHost to set
	 */
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	
	/**
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}
	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	/**
	 * @return the emailPurpose
	 */
	public String getEmailPurpose() {
		return emailPurpose;
	}
	/**
	 * @param emailPurpose the emailPurpose to set
	 */
	public void setEmailPurpose(String emailPurpose) {
		this.emailPurpose = emailPurpose;
	}
	
	/**
	 * @return the statusId
	 */
	public int getStatusId() {
		return statusId;
	}
	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the modifiedDate
	 */
	
	public Date getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	/**
	 * @return the statusId
	 */
}
