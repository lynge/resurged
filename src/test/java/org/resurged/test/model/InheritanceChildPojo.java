package org.resurged.test.model;

import org.resurged.jdbc.ResultColumn;


public class InheritanceChildPojo extends InheritanceParentPojo{
	private String firstName;
	
	@ResultColumn("LASTNAME")
	private String surName;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurName() {
		return surName;
	}
	public void setSurName(String lastName) {
		this.surName = lastName;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName()).append("{").append(pk).append(",").append(firstName).append(",").append(surName).append("}");
		
		return sb.toString();
	}
}
