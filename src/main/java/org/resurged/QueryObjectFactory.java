package org.resurged;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;

import javax.sql.DataSource;

import org.resurged.impl.DataSetImpl;
import org.resurged.impl.marshalling.Marshaller;
import org.resurged.impl.marshalling.MarshallingFactory;
import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public class QueryObjectFactory {

	public static <T extends BaseQuery> T createQueryObject(final Class<T> clazz, final Connection connection) {
		System.out.println(clazz.getName());
		ClassLoader loader = clazz.getClassLoader();
		Class<?>[] interfaces = {clazz};
		System.out.println(Arrays.toString(interfaces));
		InvocationHandler h = new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Update annotation = method.getAnnotation(Update.class);
				if(annotation != null) {
					String sql = getSqlFromAnnotation(args, annotation.value());
					System.out.println("SQL: "+sql);
					Statement statement = connection.createStatement();
					int affectedRows = statement.executeUpdate(sql); 
					statement.close();
					return affectedRows;
				}
				else {
					Select select = method.getAnnotation(Select.class);
					if(select != null) {
						String sql = getSqlFromAnnotation(args, select.value());
						Class<?> c = Class.forName(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].toString().split(" ")[1]);
						System.out.println(c);
						Marshaller<?> marshaller = MarshallingFactory.getMarshaller(c);
						Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery(sql);
						@SuppressWarnings({ "rawtypes", "unchecked" })
						DataSetImpl result = new DataSetImpl(resultSet, marshaller);
						resultSet.close();
						statement.close();
						return result;
					}
				}
				return null;
			}

			private String getSqlFromAnnotation(Object[] args, String sql) {
				if(args != null) {
					for(int i=args.length-1;i>=0;i--) {
						String argument = "";
						if(args[i].getClass() == String.class) {
							argument = "'"+args[i]+"'";
						} else if(Date.class.isAssignableFrom(args[i].getClass())) {
							if(args[i].getClass() == Date.class) {
								java.sql.Date date = new java.sql.Date(((Date)args[i]).getTime());
								argument = "'"+date.toString()+"'";
							} else {
								argument = "'"+args[i].toString()+"'";
							}
						} else {
							argument = args[i].toString();
						}
						sql = sql.replace("?"+(i+1), argument);
					}
				}
				return sql;
			}
		};
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(loader, interfaces, h);
		return proxy;
	}

	public static <T extends BaseQuery> T createQueryObject(final Class<T> clazz, final DataSource dataSource) throws SQLException {
		return createQueryObject(clazz, dataSource.getConnection());
	}
}
