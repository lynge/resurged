package org.resurged.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public interface QueryObjectGenerator {
	/**
	 * Creates a concrete implementation of a Query interface using the JDBC
	 * drivers QueryObjectGenerator implementation.
	 * 
	 * @param ifc
	 *            - The Query interface that will be created
	 * @param ds
	 *            - The DataSource that will be used when invoking methods that
	 *            access the data source. The QueryObjectGenerator
	 *            implementation will use this DataSource without any unwrapping
	 *            or modications to create connections to the data source.
	 * @return An concrete implementation of a Query interface
	 * @throws SQLException
	 *             - if a database access error occurs.
	 */
	<T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds)
			throws SQLException;

	/**
	 * Creates a concrete implementation of a Query interface using the JDBC
	 * drivers QueryObjectGenerator implementation.
	 * 
	 * @param ifc
	 *            - The Query interface that will be created
	 * @param con
	 *            - The Connection that will be used when invoking methods that
	 *            access the data source. The QueryObjectGenerator
	 *            implementation will use this Connection without any unwrapping
	 *            or modications to create statements from the data source.
	 * @return An concrete implementation of a Query interface
	 * @throws SQLException
	 *             - if a database access error occurs.
	 */
	<T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con)
			throws SQLException;
}
