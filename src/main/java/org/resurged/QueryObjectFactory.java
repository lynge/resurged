package org.resurged;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.SQLRuntimeException;

public class QueryObjectFactory {
	private Connection con;
	private DataSource ds;
	private Config configuration=new Config();
	private static Config staticConfiguration=new Config();

	public QueryObjectFactory(Connection con) {
		this.con = con;
	}
	public QueryObjectFactory(Connection con, Config configuration) {
		this.con = con;
		this.configuration=configuration;
	}

	public QueryObjectFactory(DataSource ds) {
		this.ds = ds;
	}
	public QueryObjectFactory(DataSource ds, Config configuration) {
		this.ds = ds;
		this.configuration=configuration;
	}

	public void setConfiguration(Config configuration) {
		this.configuration = configuration;
	}
	public Config getConfiguration() {
		return configuration;
	}

	/**
	 * Creates a concrete implementation of a Query interface using the JDBC
	 * drivers QueryObjectGenerator implementation. If the JDBC driver does not
	 * provide its own QueryObjectGenerator, the QueryObjectGenerator provided
	 * with Java SE will be used.
	 * 
	 * @param ifc
	 *            - The Query interface that will be created
	 * @return A concrete implementation of a Query interface
	 * 
	 * @throws SQLRuntimeException
	 *             - if a database access error occurs or this method is called
	 *             on a closed connection
	 */
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc)
			throws SQLRuntimeException {
		try {
			if(con!=null)
				return configuration.getGenerator().createQueryObject(ifc, con);
			else
				return configuration.getGenerator().createQueryObject(ifc, ds);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	/**
	 * Creates a concrete implementation of a Query interface using the JDBC
	 * drivers QueryObjectGenerator implementation.<br>
	 * If the JDBC driver does not provide its own QueryObjectGenerator, the
	 * QueryObjectGenerator provided with Java SE will be used.<br>
	 * This method is primarly for developers of Wrappers to JDBC
	 * implementations. Application developers should use
	 * createQueryObject(Class<T> ifc).
	 * 
	 * @param ifc
	 *            - The Query interface that will be created
	 * @param con
	 *            - The Connection that will be used when invoking methods that
	 *            access the data source. The QueryObjectGenerator
	 *            implementation will use this Connection without any unwrapping
	 *            or modications to create statements from the data source.
	 * @return A concrete implementation of a Query interface
	 * @throws SQLRuntimeException
	 *             - if a database access error occurs.
	 */
	public static <T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con) throws SQLRuntimeException {
		return createQueryObject(ifc, con, staticConfiguration);
	}
	
	public static <T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con, Config configuration) throws SQLRuntimeException {
		try {
			return configuration.getGenerator().createQueryObject(ifc, con);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}
	
	/**
	 * Creates a concrete implementation of a Query interface using the JDBC drivers QueryObjectGenerator implementation.<br>
	 * * If the JDBC driver does not provide its own QueryObjectGenerator, the QueryObjectGenerator provided with Java SE will be used.<br>
	 * This method is primarly for developers of Wrappers to JDBC implementations. Application developers should use createQueryObject(Class<T> ifc).
	 * 
	 * @param ifc - The Query interface that will be created
	 * @param ds - The DataSource that will be used when invoking methods that access the data source. The QueryObjectGenerator implementation will use this DataSource without any unwrapping or modications to create connections to the data source.
	 * @return A concrete implementation of a Query interface
	 * @throws SQLRuntimeException - if a database access error occurs.
	 */
	public static <T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds) throws SQLRuntimeException {
		return createQueryObject(ifc, ds, staticConfiguration);
	}
	public static <T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds, Config configuration) throws SQLRuntimeException {
		try {
			return configuration.getGenerator().createQueryObject(ifc, ds);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} 
	}
}
