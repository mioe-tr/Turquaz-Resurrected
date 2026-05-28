/*
 * Created on May 23, 2005
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.turquaz.client;

import client.web.HttpSessionClient;

import com.turquaz.common.HttpServiceRequest;
import com.turquaz.common.HttpServiceResponse;
import com.turquaz.engine.EngConfiguration;

/**
 * @author Cem
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Services
{	
	
	public static Object CallService(HttpServiceRequest request)throws Throwable
	{
		
		HttpSessionClient client = HttpSessionClient.getInstance(EngConfiguration.getString("default-app"));
			
		HttpServiceResponse response=(HttpServiceResponse)client.invokeHttp(request);
		
		if (response.isExceptionThrown())
		{
			throw response.getThrowable();
		}
		else
		{
			return response.getResult();
		}
			
	}
}
