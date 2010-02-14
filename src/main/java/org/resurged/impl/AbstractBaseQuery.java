package org.resurged.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.SQLRuntimeException;

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

	protected <L> DataSet<L> executeQuery(Class<L> returnType, String query) throws SQLRuntimeException {
		Connection connection=null;
		try{
			connection = getConnection();
			return QueryEngine.executeQuery(returnType, connection, query);
		} finally {
			if(connection!=null)
				cleanupConnection(connection);
		}
		
	}
	protected <L> DataSet<L> executeQuery(Class<L> returnType, String query, Object[] params) {
		Connection connection=null;
		try{
			connection = getConnection();
			return QueryEngine.executeQuery(returnType, connection, query, params);
		} finally {
			if(connection!=null)
				cleanupConnection(connection);
		}
	}
	protected int executeUpdate(String query) throws SQLRuntimeException {
		Connection connection=null;
		try{
			connection = getConnection();
			return QueryEngine.executeUpdate(connection, query);
		} finally {
			if(connection!=null)
				cleanupConnection(connection);
		}
	}
	protected int executeUpdate(String query, Object[] params) {
		Connection connection=null;
		try{
			connection = getConnection();
			return QueryEngine.executeUpdate(connection, query, params);
		} finally {
			if(connection!=null)
				cleanupConnection(connection);
		}
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
