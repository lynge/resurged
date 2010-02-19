package org.resurged.impl.classgen.asm;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.resurged.impl.classgen.AbstractGenerator;
import org.resurged.jdbc.BaseQuery;

public class AsmGenerator extends AbstractGenerator {

	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds) throws SQLException {
		return createQueryObject(ifc, (Object)ds, DataSource.class);
	}

	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con) throws SQLException {
		return createQueryObject(ifc, (Object)con, Connection.class);
	}
	
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, Object o, Class<?> type) throws SQLException {
		try {
			// new ClassWriter(0) 
			// Nothing is automatically computed. You have to compute yourself the frames 
			// and the local variables and operand stack sizes.
			// 
			// new ClassWriter(ClassWriter.COMPUTE_MAXS)
			// The sizes of the local variables and operand stack parts are computed for you. 
			// You must still call visitMaxs, but you can use any arguments: they will be ignored
			// and recomputed. With this option you still have to compute the frames yourself.
			// 
			// new ClassWriter(ClassWriter.COMPUTE_FRAMES)
			// Everything is computed automatically. You don’t have to call visitFrame, but you
			// must still call visitMaxs (arguments will be ignored and recomputed).
			ClassWriter queryObjectWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			AsmAdapter adapter = new AsmAdapter(queryObjectWriter, ifc);

//			Class klass = ifc.getClass();
//			while (klass != BaseQuery.class){
//				System.out.println(klass.getName());
//				Class[] interfaces = klass.getInterfaces();
//				for (int i = 0; i < interfaces.length; i++) {
//					if(interfaces[i]!=BaseQuery.class)
//						;
//				}
//			}
			byte[] queryInterfaceBytes = new ClassReader(ifc.getName()).b;
			ClassReader queryInterfaceReader = new ClassReader(queryInterfaceBytes);
			queryInterfaceReader.accept(adapter, 0);
			byte[] queryObjectBytes = queryObjectWriter.toByteArray();
			
			AsmClassLoader<T> loader = new AsmClassLoader<T>();
			Class<T> queryObjectClass = loader.defineClass(adapter.getClassName(), queryObjectBytes);
			
			Constructor<T> constructor = queryObjectClass.getConstructor(type);
			return constructor.newInstance(o);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
