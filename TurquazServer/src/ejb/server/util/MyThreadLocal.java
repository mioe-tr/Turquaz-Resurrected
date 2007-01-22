package server.util;


public class MyThreadLocal {
	
	static ThreadLocal local = new ThreadLocal();
	
	public static void setSessionInfo(SessionInfo name)
	{
		local.set(name);
	}
	public static SessionInfo getSessionInfo()
	{
		return (SessionInfo)local.get();
	}
	public static void release()
	{
		local.set(null);
	}

}
