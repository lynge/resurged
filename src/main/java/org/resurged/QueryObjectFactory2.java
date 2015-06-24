package org.resurged;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.resurged.impl.DataSetImpl;
import org.resurged.impl.marshalling.Marshaller;
import org.resurged.impl.marshalling.MarshallingFactory;
import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public class QueryObjectFactory2 {

	public static <T extends BaseQuery> T createQueryObject(final Class<T> clazz, final Connection connection) {
		ClassLoader loader = clazz.getClassLoader();
		Class<?>[] interfaces = {clazz};
		InvocationHandler h = new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Update annotation = method.getAnnotation(Update.class);
				if(annotation != null) {
					String sql = annotation.value();
					if(args != null) {
						for(int i=0;i<args.length;i++) {
							String dataWrapper = "";
							if(args[i].getClass() == String.class) {
								dataWrapper = "'";
							}
							sql = sql.replace("?"+(i+1), dataWrapper + args[i].toString() + dataWrapper);
						}
					}
					Statement statement = connection.createStatement();
					int affectedRows = statement.executeUpdate(sql); 
					statement.close();
					return affectedRows;
				}
				else {
					Select select = method.getAnnotation(Select.class);
					if(select != null) {
						String sql = select.value();
						if(args != null) {
							for(int i=0;i<args.length;i++) {
								String dataWrapper = "";
								if(args[i].getClass() == String.class) {
									dataWrapper = "'";
								}
								sql = sql.replace("?"+(i+1), dataWrapper + args[i].toString() + dataWrapper);
							}
						}
						Class c = Class.forName(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].toString().split(" ")[1]);
						System.out.println(c);
						Marshaller<?> marshaller = MarshallingFactory.getMarshaller(c);
						Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery(sql);
						DataSetImpl result = new DataSetImpl(resultSet, marshaller);
						resultSet.close();
						statement.close();
						return result;
					}
				}
				return null;
			}
		};
		T proxy = (T) Proxy.newProxyInstance(loader, interfaces, h);
		return proxy;
	}

}
