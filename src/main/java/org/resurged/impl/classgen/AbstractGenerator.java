package org.resurged.impl.classgen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.QueryObjectGenerator;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public abstract class AbstractGenerator implements QueryObjectGenerator {
	protected static final String SUFFIX = "Resurged";
	
	public void generateMethods(Class<? extends BaseQuery> queryInterface, Object[] args){
		Method[] methods = traverseMethods(queryInterface);
		for (int i = 0; i < methods.length; i++) {
			if(methods[i].isAnnotationPresent(Select.class)){
				generateMethod(queryInterface, methods[i], methods[i].getAnnotation(Select.class), args);
			}else if(methods[i].isAnnotationPresent(Update.class)){
				generateMethod(queryInterface, methods[i], methods[i].getAnnotation(Update.class), args);
			}
		}
	}
	
	protected abstract void generateMethod(Class<? extends BaseQuery> queryInterface, Method method, Annotation annotation, Object[] args);
	
	public static Method[] traverseMethods(Class<? extends BaseQuery> klass){
		HashMap<String, Method> methods=new HashMap<String, Method>();
		traverseMethods(klass, methods);
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
//				System.out.println(currentClassMethods[j].getName());
				methods.put(currentClassMethods[j].getName()+Type.getMethodDescriptor(currentClassMethods[j]), currentClassMethods[j]);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends BaseQuery>[] traverseParentInterfaces(Class<? extends BaseQuery> klass){
		ArrayList<Class<? extends BaseQuery>> interfaces=new ArrayList<Class<? extends BaseQuery>>();
		traverseParentInterfaces(klass, interfaces);
		interfaces.add(klass);
		return interfaces.toArray(new Class[]{});
	}
	@SuppressWarnings("unchecked")
	private static void traverseParentInterfaces(Class klass, ArrayList<Class<? extends BaseQuery>> inter){
		Class<?>[] interfaces = klass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			boolean isBaseQuery=interfaces[i]==BaseQuery.class;
			boolean isAsignable=BaseQuery.class.isAssignableFrom(interfaces[i]);
			if(!isBaseQuery && isAsignable){
				traverseParentInterfaces(interfaces[i], inter);
				inter.add((Class<? extends BaseQuery>) interfaces[i]);
			}
		}
	}
}
