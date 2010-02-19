package org.resurged.impl.classgen;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.QueryObjectGenerator;
import org.resurged.test.model.PersonDao;

public abstract class AbstractGenerator implements QueryObjectGenerator {
	protected static Method[] traverseMethods(Class<? extends BaseQuery> klass){
		HashMap<String, Method> methods=new HashMap<String, Method>();
		traverseMethods(PersonDao.class, methods);
		return (Method[]) methods.values().toArray(new Method[]{});
	}
	@SuppressWarnings("unchecked")
	private static void traverseMethods(Class klass, HashMap<String, Method> methods){
		Class<?>[] interfaces = klass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if(interfaces[i]!=BaseQuery.class && BaseQuery.class.isAssignableFrom(interfaces[i]))
				traverseMethods(interfaces[i], methods);
			
			Method[] currentClassMethods = klass.getDeclaredMethods();
			for (int j = 0; j < currentClassMethods.length; j++) {
				methods.put(currentClassMethods[j].getName(), currentClassMethods[j]);
			}
		}
	}
	
	protected Class<? extends BaseQuery>[] traverseParentInterfaces(Class<? extends BaseQuery> klass){
		return null;
	}
}
