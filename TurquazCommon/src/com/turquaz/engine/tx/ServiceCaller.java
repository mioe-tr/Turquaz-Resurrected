package com.turquaz.engine.tx;

import java.util.HashMap;

public interface ServiceCaller {
	public Object doSelect(String serviceName, HashMap argMap)throws Exception;
	public Object doTransaction(String serviceName, HashMap argMap)throws Exception;

}
