package com.turquaz.bill.dal;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

public class BillDALAddGroups
{
	public BillDALAddGroups()
	{
	}

	public static List getBillGroups() throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "from TurqBillGroup as gr ";
			Query q = session.createQuery(query);
			List list = q.list();
			
			return list;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}