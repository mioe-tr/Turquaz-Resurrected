package server.util;

import java.io.Serializable;
import java.util.HashMap;

public class SessionInfo implements Serializable{
	
	HashMap info = new HashMap();
	
	
	public SessionInfo()
	{		
	
	}
	
	public void put(Object key,Object value)
	{
		info.put(key,value);
	}
	
	public Object get(Object key)
	{
		return info.get(key);
	}
	
}
