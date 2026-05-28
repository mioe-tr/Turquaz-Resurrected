package server.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class EngDALSessionFactory {
	static EngDALSessionFactory _instance;
	public SessionFactory factory;
	Configuration cfg;

	
	public EngDALSessionFactory()
	{
		
		cfg = new Configuration();
		cfg.configure();
		factory = cfg.buildSessionFactory();		
	}
	
	public static Session getSession() throws Exception
	{
			if (_instance == null)
			{
				_instance = new EngDALSessionFactory();
			}
			return _instance.factory.getCurrentSession();
			
		
	}
	
	public static String getDataSource()
	{
		if (_instance == null)
		{
			_instance = new EngDALSessionFactory();
		}
		return _instance.cfg.getProperties().get("hibernate.connection.datasource").toString();
		
	}
	
	

}
