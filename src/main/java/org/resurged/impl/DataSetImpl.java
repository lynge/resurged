package org.resurged.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.resurged.impl.marshalling.Marshaller;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.SQLRuntimeException;

public class DataSetImpl<T> extends ArrayList<T> implements DataSet<T> {
	private static final long serialVersionUID = 4319688550002453504L;

	public DataSetImpl(ResultSet rs, Marshaller<T> ms){
		try {
			while (rs.next()) {
				T object = ms.getObject(rs);
				add(object);
			}
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}
}
