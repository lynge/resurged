package org.resurged.test.model;

import org.resurged.jdbc.ResultColumn;

public class Person {
	@ResultColumn(name="LAST_NAME")
	private String lastName;
	
	@ResultColumn("FIRST_NAME")
	private String firstName;
	
	private int id;

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName()).append("{").append(id).append(",").append(firstName).append(",").append(lastName).append("}");
		
		return sb.toString();
	}
}
