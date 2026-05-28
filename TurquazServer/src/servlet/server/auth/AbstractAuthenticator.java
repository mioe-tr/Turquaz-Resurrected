package server.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticator implements Authenticator, Serializable{
   
	
	public abstract Principal getUser(HttpServletRequest request, HttpServletResponse response);
	

    public abstract boolean login(HashMap argMap) throws AuthenticationException;

    public abstract boolean logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;


}
