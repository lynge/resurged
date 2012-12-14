package org.resurged.impl.marshalling;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;

import org.resurged.impl.Log;
import org.resurged.jdbc.ResultColumn;
import org.resurged.jdbc.SQLRuntimeException;

// Fieldmarshallers are responsible for the marshalling of a specific result set field, into a pojo object 
public class FieldMarshaller{
	private String fieldName;
	private Field pojoField;
	
	public void setField (Field field) {
		this.pojoField = field;
		
		// Obtain the result set field name, based on either annotation or pojo field name
		if(field.isAnnotationPresent(ResultColumn.class)){
			ResultColumn annotation = (ResultColumn) field.getAnnotation(ResultColumn.class);
			String annotationValue=annotation.value();
			String annotationName=annotation.name();
			
			if(annotationValue.trim().length()==0 && annotationName.trim().length()==0)
				throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Either the name or value attribute must be provided");
			else if(annotationValue.trim().length()>0 && annotationName.trim().length()>0)
				throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Only the name or value attribute must be provided");
			
			fieldName = (annotationValue.trim().length()>0)?annotationValue:annotationName;
		} else {
			fieldName = field.getName();
		}
	}
	
	// Marshall field value from result set into pojo 
	public void retrieveFieldValue(ResultSet rs, Object pojoInstance, int index) throws Exception{
		boolean accessible = pojoField.isAccessible(); 
		if(!accessible)
			pojoField.setAccessible(true);
		
		if(index<0)
			Log.trace(this, "Geting " + fieldName);
		else
			Log.trace(this, "Geting field " + index);
		
		// Obtain field from result set, based on either field name or index
		Object resultSetFieldValue = (index<0)? rs.getObject(fieldName) : rs.getObject(index);
		
		// Call field translation method
		setFieldValue(resultSetFieldValue, pojoField, pojoInstance);

		if(!accessible)
			pojoField.setAccessible(false);
	}
	
	// Translate from result set field type into pojo field type
	public void setFieldValue(Object resultSetFieldValue, Field pojoField, Object pojoInstance) throws Exception{
		if(resultSetFieldValue!=null){
			try{
				if(resultSetFieldValue instanceof Integer &&(pojoField.getType()==Byte.TYPE || pojoField.getType()==Byte.class))
					pojoField.set(pojoInstance, ((Integer)resultSetFieldValue).byteValue());
				else if(resultSetFieldValue instanceof Integer &&(pojoField.getType()==Short.TYPE || pojoField.getType()==Short.class))
					pojoField.set(pojoInstance, ((Integer)resultSetFieldValue).shortValue());
				else if(resultSetFieldValue instanceof Long &&(pojoField.getType()==Integer.TYPE || pojoField.getType()==Integer.class))
					pojoField.set(pojoInstance, ((Long)resultSetFieldValue).intValue());
				else if(resultSetFieldValue instanceof BigDecimal &&(pojoField.getType()==Integer.TYPE || pojoField.getType()==Integer.class))
					pojoField.set(pojoInstance, ((BigDecimal)resultSetFieldValue).intValue());
				else if(resultSetFieldValue instanceof Double &&(pojoField.getType()==Float.TYPE || pojoField.getType()==Float.class))
					pojoField.set(pojoInstance, ((Double)resultSetFieldValue).floatValue());
				else if(resultSetFieldValue instanceof Integer &&(pojoField.getType()==Boolean.TYPE || pojoField.getType()==Boolean.class))
					pojoField.set(pojoInstance, ((Integer)resultSetFieldValue)!=0);
				else
					pojoField.set(pojoInstance, resultSetFieldValue);
			}catch(IllegalArgumentException e){
				throw new Exception("Unable to transfer result set field value of type: " + resultSetFieldValue.getClass().getName() + " to pojo field named \"" + pojoField.getName() + "\" of type: " + pojoField.getType().getName(), e);
			}
		}
	}

	public String getFieldName() {
		return fieldName;
	}
}