package server.auth;

import java.security.Principal;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


	public interface Authenticator 
	{
	    public final String DEFAULT_AUTHENTICATOR = "com.turquaz.auth.TurquazAuthenticator";
		
		
		public Principal getUser(HttpServletRequest request, HttpServletResponse response);
		
	    public boolean login(HashMap argMap) throws AuthenticationException;

	    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
	
	}

	

