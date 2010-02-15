package org.resurged;

import org.resurged.impl.Log;
import org.resurged.impl.Log.LogStrategy;
import org.resurged.impl.classgen.asm.AsmGenerator;
import org.resurged.impl.classgen.jdk6.JdkGenerator;
import org.resurged.jdbc.QueryObjectGenerator;
import org.resurged.jdbc.SQLRuntimeException;

public class Config {
	private QueryObjectGenerator generator=null, defaultGenerator=null;

	QueryObjectGenerator getBytecodeGenerator() {
		if(generator!=null)
			return generator;
		else if(defaultGenerator!=null)
			return defaultGenerator;
		
		Log.info(this, "No bytecode generator specified, loading default generator");
		
		try {
			Class.forName("org.objectweb.asm.ClassWriter");
			Log.info(this, "ASM found in classpath, bytecode will be generated using AMS");
			defaultGenerator=new AsmGenerator();
			return defaultGenerator;
		} catch (ClassNotFoundException e) {}
		
		try {
			Class.forName("javax.tools.JavaCompiler");
			Log.info(this, "javax.tools.JavaCompiler found in classpath, bytecode will be generated using javax.tools.JavaCompiler");
			Log.warn(this, "javax.tools.JavaCompiler must only be used for sandbox testing, adding asm.jar to your classpath will improve performance");
			defaultGenerator=new JdkGenerator();
			return defaultGenerator;
		} catch (ClassNotFoundException e) {}
		
		throw new SQLRuntimeException("Missing dependency, asm.jar or javax.tools.JavaCompiler must be in classpath");
	}
	
	public void setGenerator(QueryObjectGenerator generator) {
		this.generator = generator;
	}
	public QueryObjectGenerator getGenerator() {
		return generator;
	}

	public static void setLoggingStrategy(LogStrategy loggingStrategy) {
		Log.setLoggingStrategy(loggingStrategy);
	}
	public static LogStrategy getLoggingStrategy() {
		return Log.getLoggingStrategy();
	}
}
