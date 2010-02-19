package org.resurged.test.util;

public enum Generator {
	Asm(0), Jdk(1);

	private int intValue;
	
	private Generator(int intValue){
		this.intValue = intValue;
	}
	
	public int intValue(){
		return intValue;
	}
}
