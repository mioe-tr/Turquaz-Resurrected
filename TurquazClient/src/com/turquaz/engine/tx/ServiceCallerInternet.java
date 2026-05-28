package com.turquaz.engine.tx;

import java.util.HashMap;

import com.turquaz.client.Services;
import com.turquaz.common.HttpServiceRequest;
import com.turquaz.engine.config.EngCompression;
import com.turquaz.engine.exceptions.TurquazAuthenticationException;
import com.turquaz.engine.exceptions.TurquazException;
import com.turquaz.engine.ui.EngUIReAuthenticator;

public class ServiceCallerInternet implements ServiceCaller {

	public Object doSelect(String serviceName, HashMap argMap) throws Exception {
		try
		{
			HttpServiceRequest request = new HttpServiceRequest(EngCompression.getCompressionMethod());
			request.addService(serviceName, argMap);
			HashMap result = (HashMap) Services.CallService(request);
			return result.get(serviceName);
		}
		catch (Throwable th)
		{
			if(th instanceof TurquazAuthenticationException)
			{			
			   if(EngUIReAuthenticator.showGUI())
			   {
				   return doSelect(serviceName,argMap);
			   }
			   else
			   {
				   throw new Exception(th);
			   }
			   
			}
			if (th instanceof TurquazException)
			{
				throw (TurquazException)th;
			}
			else
			{
				throw new Exception(th);
			}
		}
	}

	public Object doTransaction(String serviceName, HashMap argMap)
			throws Exception {
		try
		{
			HttpServiceRequest request = new HttpServiceRequest(EngCompression.getCompressionMethod());
			request.addService(serviceName, argMap);
			HashMap result = (HashMap) Services.CallService(request);
			return result.get(serviceName);
		}
		catch (Throwable th)
		{
			if(th instanceof TurquazAuthenticationException)
			{			
			   if(EngUIReAuthenticator.showGUI())
			   {
				   return doTransaction(serviceName,argMap);
			   }
			   else
			   {
				   throw new TurquazException(th);
			   }
			   
			}
			if (th instanceof TurquazException)
			{
				throw (TurquazException)th;
			}
			else
			{
				throw new Exception(th);
			}
		}
		
		
	}

}
