package org.resurged.test.util;

import org.objectweb.asm.util.ASMifierClassVisitor;

public class AsmReverseEngineer {
	private static Class<?> CLASS = Object.class;
	
	public static void main(String[] args) {
		try {
			ASMifierClassVisitor.main(new String[] { CLASS.getName() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
