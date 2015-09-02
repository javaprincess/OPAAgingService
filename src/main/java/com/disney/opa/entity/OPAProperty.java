/**
 * 
 */
package com.disney.opa.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="OPAProperties"
)
public class OPAProperty implements Serializable {
	
	private static final long serialVersionUID = 2L;

	@Id
	@Column(name = "Name")
	private String name;
	
	@Column(name = "DataTypeID")
	private String dataTypeId;	
	
	@Column(name = "Value")
	private String value;
	
	@Column(name = "Active")
	private Long active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataTypeId() {
		return dataTypeId;
	}

	public void setDataTypeId(String dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}		
	
	


}
