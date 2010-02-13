package org.resurged;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	private static Logger logger = Logger.getLogger("org.resurged");
	
	public static void trace(Object source, String message){
		getLogger().log(Level.FINEST, message);
	}
	public static void debug(Object source, String message){
		getLogger().log(Level.FINE, message);
	}
	public static void info(Object source, String message){
		getLogger().log(Level.INFO, message);
	}
	public static void warn(Object source, String message){
		getLogger().log(Level.WARNING, message);
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
}
