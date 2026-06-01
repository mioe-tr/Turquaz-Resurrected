package com.turquaz.engine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class EngDatabase
{
	private static final String filename = "config/database.properties"; //$NON-NLS-1$
	private Properties props;
	private static EngDatabase _instance;

	private EngDatabase()
	{
		try
		{
			
			FileInputStream fis = new FileInputStream(filename);
			props = new Properties();
			props.load(fis);			
			
		}
		catch (Exception ex)
		{
           ex.printStackTrace();
		}
	}

	public static void setString(String key, String value)
	{
		
		_instance.props.setProperty(key, value);
		try
		{
			FileOutputStream fileout = new FileOutputStream(filename); //$NON-NLS-1$
			_instance.props.store(fileout, "Turquaz Properties File"); //$NON-NLS-1$
			fileout.flush();
			fileout.close();
		}
		catch (Exception ex)
		{
            ex.printStackTrace();
		}
	}

	public static String getString(String key)
	{
		if (_instance == null)
		{
			_instance = new EngDatabase();
		}
		return _instance.findString(key);
	}

	private String findString(String Key)
	{
		if (props == null)
		{
			return "";
		}
		return props.getProperty(Key);
	}
	
	public static void refreshConfig()
	{
		_instance = new EngDatabase();
	}
}