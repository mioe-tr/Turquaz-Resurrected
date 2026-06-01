/*
 * Created on May 23, 2005
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.turquaz.common;

import java.io.Serializable;
import java.util.HashMap;

import com.turquaz.engine.EngKeys;

/**
 * @author Cem
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HttpServiceRequest implements Serializable
{
	private int compressionMethod=Compression.NO_COMPRESSION;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap serviceMap =new HashMap();
	/**
	 * @param requestList
	 */
	
	public HashMap getRequestMap()
	{
		return serviceMap;
	}
	
	
	/**
	 * 
	 */
	public HttpServiceRequest(int compression)
	{
		super();
		//if (compression == Compression.LZMA || compression == Compression.ZIP)
			//compressionMethod=compression;
		
			
	}
	
	public HttpServiceRequest()
	{
		super();
			
	}
	
	public void addService(String serviceName,HashMap argMap)
	{
		serviceMap.put(serviceName,argMap);
	}
	
	
	
	public static HttpServiceRequest getLoginServiceRequest(String login, String pass)
	{
	
		HttpServiceRequest request = new HttpServiceRequest();
		
		HashMap argMap = new HashMap();
		argMap.put(EngKeys.USER,login);
		argMap.put(EngKeys.PASSWORD,pass);	
		request.addService("authenticate",argMap);
		
		return request;
	}


	public int getCompressionMethod()
	{
		return compressionMethod;
	}


	public void setCompressionMethod(int compressionMethod)
	{
		this.compressionMethod = compressionMethod;
	}
	
	
}
