package org.resurged;

import org.resurged.impl.Log;
import org.resurged.impl.Log.LogStrategy;
import org.resurged.impl.classgen.jdk6.JdkGenerator;
import org.resurged.jdbc.QueryObjectGenerator;

public class Config {
	private QueryObjectGenerator generator=new JdkGenerator();

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
