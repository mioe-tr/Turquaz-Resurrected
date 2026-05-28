package server.util;

import org.apache.log4j.Logger;

public class EngBLLogger {
	
	private static Logger loger = Logger.getRootLogger();

	public static void log(Class cls,Exception ex)
	{
		Logger loger = Logger.getLogger(cls);
		loger.error("Exception Caught", ex);
		ex.printStackTrace();		
	}
	
	public static void log_error(String message)
	{
		loger.error(message);
	}
	
	public static void log_warning(String message)
	{
		loger.warn(message);
	}
	
	
	

}
