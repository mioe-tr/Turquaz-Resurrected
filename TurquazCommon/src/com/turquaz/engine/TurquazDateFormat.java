package com.turquaz.engine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TurquazDateFormat {
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	public static String format(Date date)
	{
		return formatter.format(date);
	}
	public static Date getFirstDayOfYear()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), 0, 1);
		return cal.getTime();
	}

	public static Date getLastDayOfYear()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), 11, 31);
		return cal.getTime();
	}
}
