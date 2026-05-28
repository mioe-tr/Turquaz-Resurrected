package com.turquaz.accounting.dal;

/************************************************************************/
/* TURQUAZ: Higly Modular Accounting/ERP Program                        */
/* ============================================                         */
/* Copyright (c) 2004 by Turquaz Software Development Group			    */
/*																		*/
/* This program is free software. You can redistribute it and/or modify */
/* it under the terms of the GNU General Public License as published by */
/* the Free Software Foundation; either version 2 of the License, or    */
/* (at your option) any later version.       							*/
/* 																		*/
/* This program is distributed in the hope that it will be useful,		*/
/* but WITHOUT ANY WARRANTY; without even the implied warranty of		*/
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*/
/* GNU General Public License for more details.         				*/
/************************************************************************/
/**
 * @author Onsel Armagan
 * @version $Id: AccDALTransactionUpdate.java,v 1.1 2007/01/22 16:36:35 huseyiner Exp $
 */
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

import com.turquaz.engine.bl.EngBLCommon;
import com.turquaz.engine.dal.TurqAccountingTransaction;

public class AccDALTransactionUpdate
{
	public static void initializeTransactionRows(TurqAccountingTransaction accTrans) throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			
			session.refresh(accTrans);
			Hibernate.initialize(accTrans.getTurqAccountingTransactionColumns());
			
		
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static TurqAccountingTransaction getInitialTransaction() throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "select accTrans from TurqAccountingTransaction as accTrans"
					+ " where accTrans.turqAccountingTransactionType.id =" + EngBLCommon.ACCOUNTING_TRANS_OPENING;
			Query q = session.createQuery(query);
			List list = q.list();
		
			if (list.size() > 0)
			{
				return (TurqAccountingTransaction) list.get(0);
			}
			return null;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}