package org.resurged.test.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.resurged.impl.marshalling.FieldMarshaller;

public class MyFieldMarshaller extends FieldMarshaller {
	@Override
	public void setFieldValue(Object resultSetFieldValue, Field pojoField, Object pojoInstance) throws Exception{
		if(resultSetFieldValue instanceof BigDecimal &&(pojoField.getType()==Long.TYPE || pojoField.getType()==Long.class))
			pojoField.set(pojoInstance, ((BigDecimal)resultSetFieldValue).longValue());
		else
			super.setFieldValue(resultSetFieldValue, pojoField, pojoInstance);
	}
}
