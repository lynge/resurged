package org.resurged.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.resurged.impl.classgen.AbstractGenerator;
import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.SQLRuntimeException;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public abstract class AbstractBaseQuery implements BaseQuery {
	private Connection con;
	private DataSource ds;

	public AbstractBaseQuery(Connection con) {
		this.con = con;
	}
	public AbstractBaseQuery(DataSource ds) {
		this.ds = ds;
	}
	
	@Override
	public void close() {
		con=null;
		ds=null;
	}

	@Override
	public boolean isClosed() {
		return con==null && ds==null;
	}

	protected Object executeQuery(Class<?> interfaceClass, Class<? extends Annotation> annotationClass, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) throws SQLRuntimeException {
		Connection connection=null;
		try{
			connection = getConnection();
			Method method = getMethod(interfaceClass, methodName, parameterTypes);
			Annotation annotation = method.getAnnotation(annotationClass);
			Class<?> returnType = getReturnType(method);
			
			if(annotation instanceof Select)
				return QueryEngine.executeQuery(returnType, connection, getQuery(annotation), parameterValues);
			else if(returnType==Integer.TYPE)
				return QueryEngine.executeUpdate(connection, getQuery(annotation), parameterValues);
			else
				return QueryEngine.executeUpdate(returnType, (Update)annotation, connection, getQuery(annotation), parameterValues);
		} catch (Exception e) {
			throw new SQLRuntimeException(e);
		} finally {
			if(connection!=null)
				cleanupConnection(connection);
		}
		
	}

	@SuppressWarnings("unchecked")
	private Method getMethod(Class<?> interfaceClass, String methodName, Class<?>[] parameterTypes) {
		Method[] methods=AbstractGenerator.traverseMethods((Class<? extends BaseQuery>)interfaceClass);
		for (int i = 0; i < methods.length; i++) {
			Class<?>[] methodParameterTypes = methods[i].getParameterTypes();
			if(methods[i].getName().equals(methodName) && methodParameterTypes.length==parameterTypes.length){
				boolean allMatch=true;
				for (int j = 0; j < parameterTypes.length; j++) {
					if(!parameterTypes[j].equals(methodParameterTypes[j]))
						allMatch=false;
				}
				if(allMatch)
					return methods[i];
			}
		}
		throw new SQLRuntimeException("Method " + methodName + "(" + parameterTypes + ") was not found in " + interfaceClass.getName());
	}
	private static Class<?> getReturnType(Method method) {
		Type returnType = method.getGenericReturnType();
		if(returnType instanceof ParameterizedType){
		    ParameterizedType type = (ParameterizedType) returnType;
		    Type[] typeArguments = type.getActualTypeArguments();
		    for(Type typeArgument : typeArguments){
		        Class<?> typeArgClass = (Class<?>) typeArgument;
		        return typeArgClass;
		    }
		} else {
			return method.getReturnType();
		}
		throw new SQLRuntimeException("Unable to resolve return type for method " + method.getName());
	}
	
	private String getQuery(Annotation annotation) {
		String annotationValue="", annotationSql="";
		if(annotation instanceof Select){
			annotationValue=((Select)annotation).value();
			annotationSql=((Select)annotation).sql();
		}else if(annotation instanceof Update){
			annotationValue=((Update)annotation).value();
			annotationSql=((Update)annotation).sql();
		}
		
		if(annotationValue.trim().length()==0 && annotationSql.trim().length()==0)
			throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Either the sql or value attribute must be provided");
		else if(annotationValue.trim().length()>0 && annotationSql.trim().length()>0)
			throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Only the sql or value attribute must be provided");
		
		return (annotationValue.trim().length()>0)?annotationValue:annotationSql;
	}
	
	private Connection getConnection() {
		try {
			if(con!=null)
				return con;
			else if(ds!=null)
				return ds.getConnection();
			else
				throw new SQLRuntimeException("The QueryObject " + this.getClass().getName() + " has been closed");
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}
	private void cleanupConnection(Connection connection) {
		try {
			if(con!=connection){
				connection.close();
				Log.info(this, "Connection closed");
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}
}
