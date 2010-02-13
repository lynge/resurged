package org.resurged.marshalling;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.resurged.jdbc.ResultColumn;



public class Marshaller <T> {
	HashMap<String, FieldMarshaller> fieldMarshallers = new HashMap<String, FieldMarshaller>();
	private final Class<T> type;
	
	public Marshaller(Class<T> type) {
		this.type = type;
		
		try{ 
			type.getConstructor();
		}catch (NoSuchMethodException e) {
			throw new RuntimeException(type.getName() + " must have a no-args constructor!");
		}
		
		Field[] declaredFields = type.getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			FieldMarshaller fieldMarshaller = new FieldMarshaller(declaredFields[i]);
			fieldMarshallers.put(fieldMarshaller.getFieldName(), fieldMarshaller);
		}
	}

	public T getObject(ResultSet rs) throws SQLException {
		try {
			T object = type.newInstance();
			for(int i=1;i<=rs.getMetaData().getColumnCount();i++){
				String columnName = rs.getMetaData().getColumnName(i);
				FieldMarshaller fieldMarshaller = null;
				if(fieldMarshallers.containsKey(columnName)){
					fieldMarshaller = fieldMarshallers.get(rs.getMetaData().getColumnName(i));
				} else {
					for(String key: fieldMarshallers.keySet()){
						if(key.equalsIgnoreCase(columnName)){
							fieldMarshaller = fieldMarshallers.get(key);
						}
					}
					if(fieldMarshaller != null){
						// Change fieldMarshaller key to correct case
						fieldMarshallers.remove(fieldMarshaller);
						fieldMarshallers.put(columnName, fieldMarshaller);
					}
				}
				if(fieldMarshaller != null)
					fieldMarshaller.retrieveFieldValue(rs, object);
			}
			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
class FieldMarshaller{
	private String fieldName;
	private Field field;
	
	public FieldMarshaller(Field field) {
		this.field = field;
		
		if(field.isAnnotationPresent(ResultColumn.class)){
			ResultColumn annotation = (ResultColumn) field.getAnnotation(ResultColumn.class);
			fieldName = annotation.value();
		} else {
			fieldName = field.getName();
		}
	}
	
	public void retrieveFieldValue(ResultSet rs, Object o) throws Exception{
		boolean accessible = field.isAccessible(); 
		if(!accessible)
			field.setAccessible(true);
		
		System.out.println("Geting " + fieldName);
		Object value = rs.getObject(fieldName);
		if(value!=null && value instanceof Integer &&(field.getType()==Byte.TYPE || field.getType()==Byte.class))
			field.set(o, ((Integer)value).byteValue());
		else if(value!=null && value instanceof Integer &&(field.getType()==Short.TYPE || field.getType()==Short.class))
			field.set(o, ((Integer)value).shortValue());
		else if(value!=null && value instanceof Long &&(field.getType()==Integer.TYPE || field.getType()==Integer.class))
			field.set(o, ((Long)value).intValue());
		else if(value!=null && value instanceof Double &&(field.getType()==Float.TYPE || field.getType()==Float.class))
			field.set(o, ((Double)value).floatValue());
		else if(value!=null && value instanceof Integer &&(field.getType()==Boolean.TYPE || field.getType()==Boolean.class))
			field.set(o, ((Integer)value)!=0);
		else
			field.set(o, value);

		if(!accessible)
			field.setAccessible(false);
	}

	public String getFieldName() {
		return fieldName;
	}
}