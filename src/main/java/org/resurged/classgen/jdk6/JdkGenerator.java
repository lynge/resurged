package org.resurged.classgen.jdk6;

import java.lang.annotation.Annotation;
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

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.QueryObjectGenerator;
import org.resurged.jdbc.SQLRuntimeException;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public class JdkGenerator implements QueryObjectGenerator {

	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, DataSource ds)
			throws SQLException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseQuery> T createQueryObject(Class<T> ifc, Connection con) throws SQLException {
		String cr = "\r\n", tb="\t";
		try {
			StringBuilder sb = new StringBuilder();
			if(ifc.getPackage() != null)
				sb.append("package ").append(ifc.getPackage().getName()).append(";").append(cr);
			sb.append("public class ").append(ifc.getSimpleName()).append("Resurged implements ").append(ifc.getName()).append(" {").append(cr);
			sb.append(tb).append("private final java.sql.Connection con;").append(cr);
			sb.append(tb).append("public ").append(ifc.getSimpleName()).append("Resurged(java.sql.Connection con){").append(cr);
			sb.append(tb).append(tb).append("this.con=con;").append(cr);
			sb.append(tb).append("}").append(cr);
			
			Method[] declaredMethods = ifc.getDeclaredMethods();
			for (int i = 0; i < declaredMethods.length; i++) {
				Annotation annotation=null;
				String annotationValue="", annotationSql="";
				
				if(declaredMethods[i].isAnnotationPresent(Select.class)){
					annotation=declaredMethods[i].getAnnotation(Select.class);
					annotationValue=((Select)annotation).value();
					annotationSql=((Select)annotation).sql();
				}else if(declaredMethods[i].isAnnotationPresent(Update.class)){
					annotation=declaredMethods[i].getAnnotation(Update.class);
					annotationValue=((Update)annotation).value();
					annotationSql=((Update)annotation).sql();
				}
				
				if(annotationValue.trim().length()==0 && annotationSql.trim().length()==0)
					throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Either the sql or value attribute must be provided");
				else if(annotationValue.trim().length()>0 && annotationSql.trim().length()>0)
					throw new SQLRuntimeException("@" + annotation.getClass().getSimpleName() + " Only the sql or value attribute must be provided");
				
				String query=(annotationValue.trim().length()>0)?annotationValue:annotationSql;
				
				if(query != null){
					String methodName = declaredMethods[i].getName();
					String methodReturnType = declaredMethods[i].getReturnType().getName();
					ArrayList<String> methodReturnTypeGenericTypes = new ArrayList<String>();
					ArrayList<String> methodParameters = new ArrayList<String>();
					
					Type returnType = declaredMethods[i].getGenericReturnType();
					if(returnType instanceof ParameterizedType){
					    ParameterizedType type = (ParameterizedType) returnType;
					    Type[] typeArguments = type.getActualTypeArguments();
					    for(Type typeArgument : typeArguments){
					        Class typeArgClass = (Class) typeArgument;
					        methodReturnTypeGenericTypes.add(typeArgClass.getName());
					    }
					}
					
					Class<?>[] parameterTypes = declaredMethods[i].getParameterTypes();
					for (int j = 0; j < parameterTypes.length; j++) {
						methodParameters.add(parameterTypes[j].getName());
					}
					
					sb.append(tb).append("public ").append(methodReturnType);
					
					if(methodReturnTypeGenericTypes.size() > 0){
						sb.append("<");
						for (int j = 0; j < methodReturnTypeGenericTypes.size(); j++) {
							sb.append(methodReturnTypeGenericTypes.get(j)).append(",");
						}
						if(sb.charAt(sb.length()-1)==',')
							sb.deleteCharAt(sb.length()-1);
						sb.append(">");
					}
					
					sb.append(" ").append(methodName).append("(");
					
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
					
					if(annotation instanceof Select){
						sb.append(tb).append(tb).append("return org.resurged.QueryEngine.executeQuery(" + methodReturnTypeGenericTypes.get(0) + ".class, con, \"" + query + "\", " + forwardArgs.toString() + ");").append(cr);
					}else{
						sb.append(tb).append(tb).append("return org.resurged.QueryEngine.executeUpdate(con, \"" + query + "\", " + forwardArgs.toString() + ");").append(cr);
					}
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

			Constructor generatedConstructor = compiledFunction.getConstructor(Connection.class);
			return (T) generatedConstructor.newInstance(con);
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
