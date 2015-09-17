package com.disney.compliance.aging.POC.queue.entry;


import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.disney.compliance.aging.POC.JsonData;
import com.disney.compliance.aging.POC.User;


@Component("warningEntry")
public class AgedUserWarningEntry extends AgedUserEntry {

	public static String job = "ENFORCE_WARNING_NOTIFICATION";
	
	public AgedUserWarningEntry() {
		
	}
	
	public AgedUserWarningEntry(String job) {
		this.job = job;
	}
	
	public String getJob() {
		return this.job;
	} 
	

	
	public AgedUserEntry createEntry(User user) {
		
		this.id = getNextId();
		this.job = "ENFORCE_WARNING_NOTIFICATION";
		this.jsonData = new JsonData(user);
		this.createDate = Calendar.getInstance().getTime();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return this;
	}



}
