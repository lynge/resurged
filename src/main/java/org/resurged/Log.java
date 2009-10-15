package org.resurged;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	private static Logger logger = Logger.getLogger("org.resurged");
	
	public static void trace(Object source, String message){
		logger.log(Level.FINEST, message);
	}
	public static void debug(Object source, String message){
		logger.log(Level.FINE, message);
	}
	public static void info(Object source, String message){
		logger.log(Level.INFO, message);
	}
	
}
