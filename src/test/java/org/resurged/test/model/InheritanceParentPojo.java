package org.resurged.test.model;

import org.resurged.jdbc.ResultColumn;


public class InheritanceParentPojo {
	@ResultColumn("ID")
	protected int pk;

	@ResultColumn("THIS WILL NEVER WORK")
	protected String surName;

	public int getPk() {
		return pk;
	}
	public void setPk(int pk) {
		this.pk = pk;
	}

	public String getSurName() {
		return surName;
	}
	public void setSurName(String lastName) {
		this.surName = lastName;
	}
}
