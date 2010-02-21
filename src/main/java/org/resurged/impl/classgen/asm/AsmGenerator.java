package org.resurged.impl.classgen.asm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.resurged.impl.classgen.AbstractGenerator;
import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.SQLRuntimeException;
import org.resurged.jdbc.Select;

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
			ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//			AsmAdapter adapter = new AsmAdapter(cv, ifc);
			
			cv.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, Type.getInternalName(ifc) + SUFFIX, null, "org/resurged/impl/AbstractBaseQuery", new String[] { Type.getInternalName(ifc) });

			// generate constructors
			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/sql/Connection;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/resurged/impl/AbstractBaseQuery", "<init>", "(Ljava/sql/Connection;)V");
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
			
			MethodVisitor mv2 = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljavax/sql/DataSource;)V", null, null);
			mv2.visitCode();
			mv2.visitVarInsn(Opcodes.ALOAD, 0);
			mv2.visitVarInsn(Opcodes.ALOAD, 1);
			mv2.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/resurged/impl/AbstractBaseQuery", "<init>", "(Ljavax/sql/DataSource;)V");
			mv2.visitInsn(Opcodes.RETURN);
			mv2.visitMaxs(2, 2);
			mv2.visitEnd();
			
			generateMethods(ifc, new Object[]{cv});

//			Class<? extends BaseQuery>[] interfaces = traverseParentInterfaces(ifc);
//			for (int i = 0; i < interfaces.length; i++) {
//				System.out.println(interfaces[i].getName());
//				byte[] queryInterfaceBytes = new ClassReader(interfaces[i].getName()).b;
//				ClassReader queryInterfaceReader = new ClassReader(queryInterfaceBytes);
//				queryInterfaceReader.accept(adapter, 0);
//			}
			byte[] queryObjectBytes = cv.toByteArray();
			
			AsmClassLoader<T> loader = new AsmClassLoader<T>();
			Class<T> queryObjectClass = loader.defineClass(ifc.getName() + SUFFIX, queryObjectBytes);
			
			Constructor<T> constructor = queryObjectClass.getConstructor(type);
			return constructor.newInstance(o);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	protected void generateMethod(Class<? extends BaseQuery> queryInterface, Method method, Annotation annotation, Object[] args) {
//		System.out.println("visitMethod(access: "+access+", name: "+name+", desc: "+desc+", signature: "+signature+",exceptions: " + exceptions);
		ClassWriter cv = (ClassWriter) args[0];
		
		// generate method signature
		String signature=(method.getReturnType().isPrimitive())? null : Type.getInternalName(String.class);
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), signature, null);
		
		// generate method body
		mv.visitCode();
		
		//////////////////////////////////////
		//// PREPARE OPERANT STACK VALUES ////
		//////////////////////////////////////
		
		// stack.push "this"
		mv.visitVarInsn(Opcodes.ALOAD, 0);

		// stack.push interface class
		mv.visitLdcInsn(Type.getType(getTypeString(queryInterface)));

		// stack.push annotation class
		if(annotation instanceof Select)
			mv.visitLdcInsn(Type.getType("Lorg/resurged/jdbc/Select;"));
		else
			mv.visitLdcInsn(Type.getType("Lorg/resurged/jdbc/Update;"));

		// stack.push method name
		mv.visitLdcInsn(method.getName());
		
//		if(annotation instanceof Select){
//			java.lang.reflect.Type returnType = method.getGenericReturnType();
//			if(returnType instanceof ParameterizedType){
//			    ParameterizedType type = (ParameterizedType) returnType;
//			    java.lang.reflect.Type[] typeArguments = type.getActualTypeArguments();
//			    Class<?> typeArgClass = (Class<?>) typeArguments[0];
//				// stack.push generic return type
//			    mv.visitLdcInsn(Type.getType("L" + typeArgClass.getName().replaceAll("\\.", "/") + ";"));
//			}
//		}

		// stack.push size of parameter type array
		mv.visitIntInsn(Opcodes.BIPUSH, method.getParameterTypes().length);
		
		// stack.pop array size and stack.push new array of that size
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");
		
		// move parameter types to array
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			// stack.dublicate array reference
			mv.visitInsn(Opcodes.DUP);
			
			// stack.push array index
			mv.visitIntInsn(Opcodes.BIPUSH, i);
			
			// stack.push parameter type
			if(parameterTypes[i].isPrimitive())
				mv.visitFieldInsn(Opcodes.GETSTATIC, getTypeString(parameterTypes[i]), "TYPE", "Ljava/lang/Class;");
			else
				mv.visitLdcInsn(Type.getType(getTypeString(parameterTypes[i])));
			
			// stack.pop index+value and store these in duplicate array reference, which is popped
			mv.visitInsn(Opcodes.AASTORE);
		}
		
		// stack.push size of parameter value array
		mv.visitIntInsn(Opcodes.BIPUSH, method.getParameterTypes().length);
		
		// stack.pop array size and stack.push new array of that size
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
		
		// move parameter values to array
		int lvarIndex = 1;
		for (int i = 0; i < parameterTypes.length; i++) {
			// stack.dublicate array reference
			mv.visitInsn(Opcodes.DUP);
			
			// stack.push array index
			mv.visitIntInsn(Opcodes.BIPUSH, i);
			
			// stack.push parameter value
			lvarIndex = pushParameterValue(lvarIndex, parameterTypes[i], mv);
			
			// stack.pop index+value and store these in duplicate array reference, which is popped
			mv.visitInsn(Opcodes.AASTORE);
		}

		//////////////////////////////////////
		////        INVOKE METHOD         ////
		//////////////////////////////////////
		
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(queryInterface) + SUFFIX, "executeQuery", "(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;");

		//////////////////////////////////////
		////        RETURN RESULT         ////
		//////////////////////////////////////
		
		if(method.getReturnType().isPrimitive()){
			mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
			mv.visitInsn(Opcodes.IRETURN);
		}else{
			mv.visitTypeInsn(Opcodes.CHECKCAST, "org/resurged/jdbc/DataSet");
			mv.visitInsn(Opcodes.ARETURN);
		}
		
		// Register local variables and frame size. 
		// Numbers are currently ignored, as this is handled by the ClassWriter
		mv.visitMaxs(0, 0);
		
		mv.visitEnd();
	}
	
	private int pushParameterValue(int i, Class<?> klass, MethodVisitor mv) {
		if(klass.isPrimitive()){	
			if(klass==Boolean.TYPE || klass==Byte.TYPE || klass==Character.TYPE|| klass==Short.TYPE || klass==Integer.TYPE) {
				mv.visitVarInsn(Opcodes.ILOAD, i);
			} else if(klass==Long.TYPE) {
				mv.visitVarInsn(Opcodes.LLOAD, i);
				i++;
			} else if(klass==Float.TYPE) {
				mv.visitVarInsn(Opcodes.FLOAD, i);
			} else if(klass==Double.TYPE) {
				mv.visitVarInsn(Opcodes.DLOAD, i);
				i++;
			} 
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, getTypeString(klass), "valueOf", "(" + getPrimitiveString(klass) + ")" + getWrapperString(klass));
		} else {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		}
		return ++i;
	}
	
	private String getTypeString(Class<?> klass){
		if(klass==Boolean.TYPE)
			return "java/lang/Boolean";
		else if(klass==Byte.TYPE)
			return "java/lang/Byte";
		else if(klass==Character.TYPE)
			return "java/lang/Character";
		else if(klass==Short.TYPE)
			return "java/lang/Short";
		else if(klass==Integer.TYPE)
			return "java/lang/Integer";
		else if(klass==Long.TYPE)
			return "java/lang/Long";
		else if(klass==Float.TYPE)
			return "java/lang/Float";
		else if(klass==Double.TYPE)
			return "java/lang/Double";
		else
			return "L" + klass.getName().replaceAll("\\.", "/") + ";";
	}
	
	private String getPrimitiveString(Class<?> klass){
		if(klass==Boolean.TYPE)
			return "Z";
		else if(klass==Byte.TYPE)
			return "B";
		else if(klass==Character.TYPE)
			return "C";
		else if(klass==Short.TYPE)
			return "S";
		else if(klass==Integer.TYPE)
			return "I";
		else if(klass==Long.TYPE)
			return "J";
		else if(klass==Float.TYPE)
			return "F";
		else if(klass==Double.TYPE)
			return "D";
		
		throw new SQLRuntimeException(klass.getName() + " is not a primitive");
	}
	
	private String getWrapperString(Class<?> klass){
		if(klass==Boolean.TYPE)
			return getTypeString(Boolean.class);
		else if(klass==Byte.TYPE)
			return getTypeString(Byte.class);
		else if(klass==Character.TYPE)
			return getTypeString(Character.class);
		else if(klass==Short.TYPE)
			return getTypeString(Short.class);
		else if(klass==Integer.TYPE)
			return getTypeString(Integer.class);
		else if(klass==Long.TYPE)
			return getTypeString(Long.class);
		else if(klass==Float.TYPE)
			return getTypeString(Float.class);
		else if(klass==Double.TYPE)
			return getTypeString(Double.class);
		
		throw new SQLRuntimeException(klass.getName() + " is not a primitive");
	}
	
//getInternalName
//	int
//	java/lang/Integer
//getObjectType
//	Lint;
//	Ljava/lang/Integer;
//getType
//	I
//	Ljava/lang/Integer;

}
