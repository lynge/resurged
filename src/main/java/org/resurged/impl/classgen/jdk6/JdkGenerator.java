package org.resurged.impl.classgen.jdk6;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.resurged.impl.classgen.AbstractGenerator;
import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.SQLRuntimeException;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public class JdkGenerator extends AbstractGenerator {

	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds) throws SQLException {
		return createQueryObject(ifc, (Object)ds, DataSource.class);
	}

	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con) throws SQLException {
		return createQueryObject(ifc, (Object)con, Connection.class);
	}
	
	private <T extends BaseQuery> T createQueryObject(Class<T> ifc, Object o, Class<?> t) throws SQLException {
		String cr = "\r\n", tb="\t";
		try {
			StringBuilder sb = new StringBuilder();
			if(ifc.getPackage() != null)
				sb.append("package ").append(ifc.getPackage().getName()).append(";").append(cr);
			sb.append("public class ").append(ifc.getSimpleName()).append("Resurged extends org.resurged.impl.AbstractBaseQuery implements ").append(ifc.getName()).append(" {").append(cr);
			
			sb.append(tb).append("public ").append(ifc.getSimpleName()).append("Resurged(java.sql.Connection con){").append(cr);
			sb.append(tb).append(tb).append("super(con);").append(cr);
			sb.append(tb).append("}").append(cr);

			sb.append(tb).append("public ").append(ifc.getSimpleName()).append("Resurged(javax.sql.DataSource ds){").append(cr);
			sb.append(tb).append(tb).append("super(ds);").append(cr);
			sb.append(tb).append("}").append(cr);
			
			Method[] declaredMethods = traverseMethods(ifc);
			for (int i = 0; i < declaredMethods.length; i++) {
				String annotationName = null;
				if(declaredMethods[i].isAnnotationPresent(Select.class))
					annotationName="org.resurged.jdbc.Select.class";
				else if(declaredMethods[i].isAnnotationPresent(Update.class))
					annotationName="org.resurged.jdbc.Update.class";

				if(annotationName != null){
					sb.append(tb).append("public ");
					
					String methodName = declaredMethods[i].getName();
					StringBuilder castTo = new StringBuilder();
					
					if(declaredMethods[i].getReturnType().isPrimitive()){
						castTo.append(Integer.class.getName());
						sb.append("int");
					} else {
						castTo.append(declaredMethods[i].getReturnType().getName()).append('<');
						
						Type returnType = declaredMethods[i].getGenericReturnType();
						if(returnType instanceof ParameterizedType){
						    ParameterizedType type = (ParameterizedType) returnType;
						    Type[] typeArguments = type.getActualTypeArguments();
						    for(Type typeArgument : typeArguments){
						        Class<?> typeArgClass = (Class<?>) typeArgument;
						        castTo.append(typeArgClass.getName()).append('>');
						        break;
						    }
						}
						sb.append(castTo.toString());
					}
					
					sb.append(" ").append(methodName).append("(");

					StringBuilder forwardArgTypes = new StringBuilder();
					forwardArgTypes.append("new Class[]{");
					
					ArrayList<String> methodParameters = new ArrayList<String>();
					Class<?>[] parameterTypes = declaredMethods[i].getParameterTypes();
					for (int j = 0; j < parameterTypes.length; j++) {
						methodParameters.add(parameterTypes[j].getName());
						if(!parameterTypes[j].isPrimitive())
							forwardArgTypes.append(parameterTypes[j].getName()).append(".class,");
						else {
							String type = parameterTypes[j].getName();
							if(type.equals("byte"))
								forwardArgTypes.append("Byte.TYPE,");
							else if(type.equals("short"))
								forwardArgTypes.append("Short.TYPE,");
							else if(type.equals("int"))
								forwardArgTypes.append("Integer.TYPE,");
							else if(type.equals("long"))
								forwardArgTypes.append("Long.TYPE,");
							else if(type.equals("float"))
								forwardArgTypes.append("Float.TYPE,");
							else if(type.equals("double"))
								forwardArgTypes.append("Double.TYPE,");
							else if(type.equals("boolean"))
								forwardArgTypes.append("Boolean.TYPE,");
							else if(type.equals("char"))
								forwardArgTypes.append("Char.TYPE,");
						}
					}
					if(forwardArgTypes.charAt(forwardArgTypes.length()-1)==',')
						forwardArgTypes.deleteCharAt(forwardArgTypes.length()-1);
					forwardArgTypes.append("}");
					
					StringBuilder forwardArgs = new StringBuilder();
					forwardArgs.append("new Object[]{");
					
					for (int j = 0; j < methodParameters.size(); j++) {
						sb.append(methodParameters.get(j)).append(" arg").append(j).append(",");
						forwardArgs.append("arg").append(j).append(",");
					}
					
					if(forwardArgs.charAt(forwardArgs.length()-1)==',')
						forwardArgs.deleteCharAt(forwardArgs.length()-1);
					forwardArgs.append("}");
					
					if(sb.charAt(sb.length()-1)==',')
						sb.deleteCharAt(sb.length()-1);
					
					sb.append("){").append(cr);
					
					sb.append(tb).append(tb).append("return (").append(castTo.toString()).append(") executeQuery(" + ifc.getName() + ".class, " + annotationName + ", \"" + methodName + "\", " + forwardArgTypes.toString() + ", " + forwardArgs.toString() + ");").append(cr);
					
					sb.append(tb).append("}").append(cr);
				}
			}
			sb.append("}");
			
//			Log.trace(this, "Generated QueryClass:\r\n" + sb.toString());
			
			CharSequenceCompiler<T> compiler = new CharSequenceCompiler<T>(this.getClass().getClassLoader(), null);
			final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
			
			String qualifiedName = (ifc.getPackage() != null)? ifc.getName() : ifc.getSimpleName();
			Class<T> compiledFunction = compiler.compile(qualifiedName + "Resurged", sb.toString(), errs, new Class<?>[] { Object.class });
			log(errs);

			Constructor<T> generatedConstructor = compiledFunction.getConstructor(t);
			return (T) generatedConstructor.newInstance(o);
		}catch (Exception e) {
			throw new SQLRuntimeException(e);
		}
	}

	private static void log(
			final DiagnosticCollector<JavaFileObject> diagnostics) {
		final StringBuilder msgs = new StringBuilder();
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			msgs.append(diagnostic.getMessage(null)).append("\n");
		}
//		Log.info(JdkGenerator.class, msgs.toString());

	}
}
