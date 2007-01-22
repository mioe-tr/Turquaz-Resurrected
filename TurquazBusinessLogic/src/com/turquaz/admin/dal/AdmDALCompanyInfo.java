package com.turquaz.admin.dal;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

import com.turquaz.engine.dal.TurqCompany;

public class AdmDALCompanyInfo
{
	public static TurqCompany getCompany() throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "from TurqCompany as company";
			Query q = session.createQuery(query);
			List lst = q.list();
			
			if (lst.size() > 0)
			{
				return (TurqCompany) lst.get(0);
			}
			return null;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}