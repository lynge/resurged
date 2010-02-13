package org.resurged;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Log {
	private static LogStrategy logger=new JavaUtilLogger();
	
	public static void trace(Object source, String message){
		getLogger().trace(source, message);
	}
	public static void debug(Object source, String message){
		getLogger().debug(source, message);
	}
	public static void info(Object source, String message){
		getLogger().info(source, message);
	}
	public static void warn(Object source, String message){
		getLogger().warn(source, message);
	}
	public static void warn(Object source, String message, Throwable t){
		getLogger().warn(source, message, t);
	}
	public static void error(Object source, String message){
		getLogger().error(source, message);
	}
	public static void error(Object source, String message, Throwable t){
		getLogger().error(source, message, t);
	}

	public static void setLogger(LogStrategy logger) {
		Log.logger = logger;
	}
	public static LogStrategy getLogger() {
		return logger;
	}

	public interface LogStrategy{
		void trace(Object source, String message);
		void debug(Object source, String message);
		void info(Object source, String message);
		void warn(Object source, String message);
		void warn(Object source, String message, Throwable t);
		void error(Object source, String message);
		void error(Object source, String message, Throwable t);
	}
	public static class ConsoleLogger implements LogStrategy{
		public void trace(Object source, String message){
			log(source, message, "TRACE", null);
		}
		public void debug(Object source, String message){
			log(source, message, "DEBUG", null);
		}
		public void info(Object source, String message){
			log(source, message, "INFO ", null);
		}
		public void warn(Object source, String message){
			log(source, message, "WARN ", null);
		}
		public void warn(Object source, String message, Throwable t){
			log(source, message, "WARN ", t);
		}
		public void error(Object source, String message){
			log(source, message, "ERROR", null);
		}
		public void error(Object source, String message, Throwable t){
			log(source, "ERROR", message, t);
		}
		
		private SimpleDateFormat FORMAT=new SimpleDateFormat("HH:mm:ss");
		private void log(Object source, String message, String severity, Throwable t) {
			StringBuffer sb=new StringBuffer();
			sb.append('[').append(FORMAT.format(new Date())).append(']');
			sb.append('[').append(severity).append(']').append(' ');
			sb.append(message);
			
			if(source != null){
				sb.append(' ').append('-').append(' ');
				if(source instanceof String)
					sb.append(source);
				else if(source instanceof Class<?>)
					sb.append(((Class<?>)source).getName());
				else
					sb.append(source.getClass().getName());
			}
				
			if(!severity.equals("WARN ") && !severity.equals("ERROR"))
				System.out.println(sb.toString());
			else
				System.err.println(sb.toString());

			if(t!=null)
				t.printStackTrace();
		}
	}
	public static class JavaUtilLogger implements LogStrategy{
		private Logger logger = Logger.getLogger("org.resurged");
		
		public void trace(Object source, String message){
			logger.log(Level.FINEST, message);
		}
		public void debug(Object source, String message){
			logger.log(Level.FINE, message);
		}
		public void info(Object source, String message){
			logger.log(Level.INFO, message);
		}
		public void warn(Object source, String message){
			logger.log(Level.WARNING, message);
		}
		public void warn(Object source, String message, Throwable t){
			logger.log(Level.WARNING, message, t);
		}
		public void error(Object source, String message){
			logger.log(Level.SEVERE, message);
		}
		public void error(Object source, String message, Throwable t){
			logger.log(Level.SEVERE, message, t);
		}
	}
}
