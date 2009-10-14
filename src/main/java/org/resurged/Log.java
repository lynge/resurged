package org.resurged;

public class Log {
	private static boolean traceEnabled = false;
	private static boolean debugEnabled = true;
	
	public static void trace(Object source, String message){
		if(isTraceEnabled())
			System.out.println("[TRACE] " + message);
	}
	public static void debug(Object source, String message){
		if(isDebugEnabled())
			System.out.println("[DEBUG] " + message);
	}
	public static void info(Object source, String message){
		System.out.println("[INFO ] " + message);
	}
	
	public static void setTraceEnabled(boolean traceEnabled) {
		Log.traceEnabled = traceEnabled;
	}
	public static boolean isTraceEnabled() {
		return traceEnabled;
	}
	public static void setDebugEnabled(boolean debugEnabled) {
		Log.debugEnabled = debugEnabled;
	}
	public static boolean isDebugEnabled() {
		return debugEnabled;
	}
}
