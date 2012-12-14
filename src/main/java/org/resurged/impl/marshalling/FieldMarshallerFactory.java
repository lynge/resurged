package org.resurged.impl.marshalling;

import java.lang.reflect.Field;

// Temporary singleton implementation. Should be changed to a configuration based approach, 
// in order to support multiple database vendors in a single VM
public class FieldMarshallerFactory{
	private static Class<?> fmClass = FieldMarshaller.class;
	
	public static void setClass(Class<?> clazz){
		FieldMarshallerFactory.fmClass = clazz;
	}
		
	public static FieldMarshaller getInstance(Field field){
		try {
			FieldMarshaller marshaller = (FieldMarshaller) fmClass.newInstance();
			marshaller.setField(field);
			
			return marshaller;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}