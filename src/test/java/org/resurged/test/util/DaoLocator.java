package org.resurged.test.util;

import org.resurged.jdbc.BaseQuery;

public class DaoLocator {
	@SuppressWarnings("unchecked")
	public static Class<? extends BaseQuery> getClass(Class<? extends BaseQuery> klass, Vendor vendor){
		try {
			int index = klass.getName().lastIndexOf('.');
			String className=klass.getName().substring(0, index) + "." + vendor.toString().toLowerCase() + klass.getName().substring(index);
			
//			System.out.println("Returning dao: " + className);
			
			return (Class<? extends BaseQuery>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			return klass;
		}
	}
}
