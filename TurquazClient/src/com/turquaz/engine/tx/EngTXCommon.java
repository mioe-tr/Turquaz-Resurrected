/*
 * Created on Mar 29, 2005
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.turquaz.engine.tx;

import java.util.HashMap;

/**
 * @author Cem
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EngTXCommon
{
	private static ServiceCaller serviceCaller=null;
	

	
	
	
	public static Object doSelectTX(String serviceName, HashMap argMap)throws Exception
	{
		return serviceCaller.doSelect(serviceName,argMap);
	}
	
	public static Object doTransactionTX(String serviceName, HashMap argMap)throws Exception
	{
		return serviceCaller.doSelect(serviceName,argMap);
	}

	public static ServiceCaller getServiceCaller() {
		return serviceCaller;
	}

	public static void setServiceCaller(ServiceCaller serviceCaller) {
		EngTXCommon.serviceCaller = serviceCaller;
	}
	
}
