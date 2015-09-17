package com.disney.compliance.aging.POC.queue.entry;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.disney.compliance.aging.POC.JsonData;
import com.disney.compliance.aging.POC.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("expiryEntry")
public class AgedUserExpiryEntry extends AgedUserEntry {
	
	private static String job = "ENFORCE_EXPIRY_NOTIFICATION";
	
	public AgedUserExpiryEntry() {
		
	}
	
	public AgedUserExpiryEntry(String job) {
		this.job = job;
	}
	
	public String getJob() {
		return this.job;
	}

	
	public AgedUserEntry createEntry(User user) {
		this.id = getNextId();
		this.job = "ENFORCE_EXPIRY_NOTIFICATION";
		this.createDate = Calendar.getInstance().getTime();
		
		this.jsonData = new JsonData(user);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return this;
	}

}
