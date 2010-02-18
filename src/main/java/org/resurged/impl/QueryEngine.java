package org.resurged.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.resurged.impl.marshalling.MarshallingFactory;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.GeneratedKeys;
import org.resurged.jdbc.SQLRuntimeException;
import org.resurged.jdbc.Update;

public class QueryEngine {

	public static <L> DataSet<L> executeQuery(Class<L> returnType, Connection con, String query, Object[] params) {
		Log.debug(QueryEngine.class, "executeQuery(" + query + ", " + params + ")");
		
		PreparedStatement stmt = null;
		try {
			ParameterizedQuery pq = new ParameterizedQuery();
			String sql = pq.getParsedSQL(query);
			
			stmt = con.prepareStatement(sql);
			pq.applyParameters(stmt, params);
			ResultSet srs = stmt.executeQuery();
			
			return new DataSetImpl<L>(srs, MarshallingFactory.getMarshaller(returnType));
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				throw new SQLRuntimeException(e);
			}
		}
	}
	
	public static <L> DataSet<L> executeUpdate(Class<L> returnType, Update annotation, Connection con, String query, Object[] params) {
		Log.info(QueryEngine.class, "executeUpdate(" + query + ", " + params + ")");
		
		if(annotation.keys()==GeneratedKeys.NO_KEYS_RETURNED)
			throw new SQLRuntimeException("Auto generated keys can only be returned, if the QueryInterface method is annotated with GeneratedKeys.RETURNED_KEYS_DRIVER_DEFINED or GeneratedKeys.RETURNED_KEYS_COLUMNS_SPECIFIED");

		try {
			if(!con.getMetaData().supportsGetGeneratedKeys())
				throw new SQLRuntimeException("Returning auto generated keys is not supported by the database");
		} catch (SQLException e) {
			throw new SQLRuntimeException("A message was thrown while calling getMetaData().supportsGetGeneratedKeys() on the database connection", e);
		}
		
		PreparedStatement stmt = null;
		try {
			ParameterizedQuery pq = new ParameterizedQuery();
			String sql = pq.getParsedSQL(query);

			Field[] keyFields = returnType.getDeclaredFields();
			
			if(annotation.keys()==GeneratedKeys.RETURNED_KEYS_DRIVER_DEFINED)
				stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			else{
				String[] returnKeys = new String[keyFields.length];
				for (int i = 0; i < keyFields.length; i++) {
					returnKeys[i]=keyFields[i].getName();
				}
				stmt = con.prepareStatement(sql, returnKeys);
			}
				
			pq.applyParameters(stmt, params);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			
			return new DataSetImpl<L>(rs, MarshallingFactory.getMarshaller(returnType));
//			while (keys.next()) {
//				ResultSetMetaData meta = keys.getMetaData();
//				int colCount = meta.getColumnCount();
//				
//				if(keyFields.length!=colCount)
//					throw new SQLRuntimeException("Expected number of auto generated keys was " + keyFields.length + ", but actual number of keys was " + colCount);
//
//	            L keyObject = returnType.newInstance();
//	            
//				for (int i = 1; i <= colCount; i++) {
//					String key = meta.getColumnName(i);
//					String type = meta.getColumnClassName(i);
//		            Object value = keys.getObject(i);
//		            
//		            if(colCount==1)
//		            	keyFields[0].set(keyObject, value);
//		            else {
//		            	
//		            }
//				}
//				
//				return keyObject;
//			}
			
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				throw new SQLRuntimeException(e);
			}
		}
	}

	public static int executeUpdate(Connection con, String query, Object[] params) {
		Log.info(QueryEngine.class, "executeUpdate(" + query + ", " + params + ")");
		
		PreparedStatement stmt = null;
		try {
			ParameterizedQuery pq = new ParameterizedQuery();
			String sql = pq.getParsedSQL(query);
			
			stmt = con.prepareStatement(sql);
			pq.applyParameters(stmt, params);
			
			int result = stmt.executeUpdate();
			return result;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				throw new SQLRuntimeException(e);
			}
		}
	}
}

class ParameterizedQuery {
	private static final int UNDEFINED_SQL_TYPE = -9999;
	private static final int MAX_PARAMS = 50 + 1;

	private String sepChar = "?";
	private int paramOrder[] = new int[MAX_PARAMS];

	private int paramCount = 0;
	private String parsedSQL;

	public String getParsedSQL(String rawSql) {
		parsedSQL = null;

		if ((parsedSQL == null) && (rawSql != null)) {
			StringBuffer expStmt = new StringBuffer(rawSql.length());
			int idx = 0, last = 0;
			while ((idx = rawSql.indexOf(sepChar, last)) >= 0) {
				if (last < idx)
					expStmt.append(rawSql.substring(last, idx));
				idx++;
				int paramNo = 0;
				while (idx < rawSql.length()) {
					char numb = rawSql.charAt(idx);
					if ((numb >= '0') && (numb <= '9')) {
						paramNo = ((paramNo * 10) + (numb - 48));
						idx++;
					} else
						break;
				}
				if (paramNo > 0) {
					// We have a parameter, now store it as '?' in the ending
					// sql-text
					expStmt.append('?');
					// and remember the parameter number
					paramOrder[paramCount++] = paramNo;
				} else {
					// We did NOT find a parameter - it was just a sep. char as
					// stand-alone
					expStmt.append(sepChar);
				}
				if (idx < rawSql.length())
					last = idx;
				else
					last = rawSql.length();
			}
			if (last < rawSql.length())
				expStmt.append(rawSql.substring(last));
			parsedSQL = expStmt.toString();
			Log.trace(this, "Parsed SQL: " + parsedSQL);
		}

		return parsedSQL;
	}

	public void applyParameters(PreparedStatement stmt, Object[] params) throws SQLException {
		int idx = 0;

		StringBuffer prms = new StringBuffer(1024);
		if ((params != null) && (paramCount > 0)){
			for (int i = 0; i < paramCount; i++) {
				idx = paramOrder[i] - 1;
				if ((idx >= 0) && (idx < params.length)) {
					prms.append("Parameter no. ");
					prms.append(i + 1);
					prms.append(" (");
					prms.append(sepChar);
					prms.append(paramOrder[i]);
					prms.append(") : ");
					prms.append(params[idx]);
					Log.trace(this, prms.toString());
					prms = new StringBuffer(1024);
					
					Object paramValue = params[idx];
					if (paramValue == null) {
						int sqlType = getNullParameterType(idx);
						stmt.setNull(i + 1, sqlType);
					} else {
						Log.info(this, params[idx].toString());
						
						if(params[idx] instanceof java.util.Date)
							params[idx]=new java.sql.Date(((java.util.Date)params[idx]).getTime());
						
						int sqlType = getParameterType(idx);
						if (sqlType != UNDEFINED_SQL_TYPE)
							stmt.setObject(i + 1, params[idx], sqlType);
						else
							stmt.setObject(i + 1, params[idx]);
					}
				} else
					throw new SQLException("SQL parameter :" + paramOrder[i] + " exceeds the number of parameters.");
			}
		}
	}

	public int getNullParameterType(int parameterNo) {
		int result = getParameterType(parameterNo);
		if (result == UNDEFINED_SQL_TYPE)
			result = Types.VARCHAR;

		return (result);
	}

	public int getParameterType(int parameterNo) {
		return (UNDEFINED_SQL_TYPE);
	}
}
