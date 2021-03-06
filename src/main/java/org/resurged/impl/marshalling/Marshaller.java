package org.resurged.impl.marshalling;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import org.resurged.jdbc.AutoGeneratedKeys;
import org.resurged.jdbc.SQLRuntimeException;

// The marshaller is responsible for the translation of JDBC resultsets into pojo objects
// The marshaller parses the class definition once, and creates a field mapper for each field
public class Marshaller <T> {
	HashMap<String, FieldMarshaller> fieldMarshallers = new HashMap<String, FieldMarshaller>();
	private final Class<T> type;
	// isReturnKeys indicate that the pojo is annotated with the AutoGeneratedKeys annotation,
	// or in other words, if it is the return object of an insert operation
	private boolean isReturnKeys;
	
	public Marshaller(Class<T> type) {
		this.type = type;
		
		try{
			// Check if the class is annotated with the AutoGeneratedKeys annotation 
			AutoGeneratedKeys annotation = type.getAnnotation(AutoGeneratedKeys.class);
			if(annotation!=null)
				isReturnKeys=true;
		}catch (Exception e) {}
		
		try{ 
			// Check if a no args constructor exist
			type.getConstructor();
		}catch (NoSuchMethodException e) {
			throw new RuntimeException(type.getName() + " must have a no-args constructor!");
		}
		
		// Obtain an array of fields to be mapped
		Field[] fields = traverseFields(type);
		for (int i = 0; i < fields.length; i++) {
			// Obtain a field mapper for the field, and store it for later user
			FieldMarshaller fieldMarshaller = FieldMarshallerFactory.getInstance(fields[i]);
			// The field are initially stored with the pojo field name as key, if no ResultColumn annotation exists for the pojo field.
			// This key may be changed later on, if the case doesn't match the result set field
			fieldMarshallers.put(fieldMarshaller.getFieldName(), fieldMarshaller);
		}
	}

	// Traverse all fields of pojo class hierachy
	private Field[] traverseFields(Class<T> klass) {
		HashMap<String, Field> methods=new HashMap<String, Field>();
		traverseFields(klass, methods);
		return (Field[]) methods.values().toArray(new Field[]{});
	}
	
	// Recursive traversion of fields on pojo class
	private static void traverseFields(Class<?> klass, HashMap<String, Field> fields){
		Class<?> superclass = klass.getSuperclass();
		if(superclass!=Object.class)
			traverseFields(superclass, fields);
			
		Field[] currentClassFields = klass.getDeclaredFields();
		for (int j = 0; j < currentClassFields.length; j++)
			fields.put(currentClassFields[j].getName(), currentClassFields[j]);
	}

	// Marshall resultset into a pojo object
	public T getObject(ResultSet rs) throws SQLException {
		try {
			// Instantiate pojo
			T pojo = type.newInstance();

			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();
			
			// If the pojo is an annotated return object of an insert operation, the field counts of pojo and result set much match each other
			Field[] keyFields=type.getDeclaredFields();
			if(isReturnKeys && keyFields.length!=colCount)
				throw new SQLRuntimeException("Expected number of auto generated keys returned was " + keyFields.length + ", but actual number of keys returned was " + colCount);
			
			// Iterate fields of resultset
			for(int i=1;i<=meta.getColumnCount();i++){
				String columnName = meta.getColumnName(i);
				FieldMarshaller fieldMarshaller = null;
				if(fieldMarshallers.containsKey(columnName)){
					// If a field marshaller already exists
					fieldMarshaller = fieldMarshallers.get(columnName);
				} else {
					// Iterate field mashaller keys, to find marshaller with different case
					for(String key: fieldMarshallers.keySet()){
						if(key.equalsIgnoreCase(columnName)){
							fieldMarshaller = fieldMarshallers.get(key);
						}
					}
					if(fieldMarshaller != null){
						// Fix case of field marshaller key
						fieldMarshallers.remove(fieldMarshaller);
						fieldMarshallers.put(columnName, fieldMarshaller);
					}
				}
				
				// TODO: What if an auto generated composite keys, only keys with a length of 1 are handled below
				if(fieldMarshaller != null){
					if(isReturnKeys && keyFields.length==1){
						// Values for auto generated keys are marshalled by field index
						fieldMarshaller.retrieveFieldValue(rs, pojo, 1);
					}else{
						// Other fields are marshalled by field name, indicated by index -1
						fieldMarshaller.retrieveFieldValue(rs, pojo, -1);
					}
				}else if(isReturnKeys && keyFields.length==1){
					// Instantiate marshaller and obtain field value for auto generated key
					FieldMarshaller fm = FieldMarshallerFactory.getInstance(keyFields[0]);
					fieldMarshallers.put(columnName, fm);
					fm.retrieveFieldValue(rs, pojo, 1);
				}
			}
			return pojo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}