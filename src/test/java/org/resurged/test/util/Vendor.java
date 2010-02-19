package org.resurged.test.util;

public enum Vendor {
	Derby(0), MySql(1), Postgres(2), Oracle(3);

	private int intValue;
	
	private Vendor(int intValue){
		this.intValue = intValue;
	}
	
	public int intValue(){
		return intValue;
	}
}
