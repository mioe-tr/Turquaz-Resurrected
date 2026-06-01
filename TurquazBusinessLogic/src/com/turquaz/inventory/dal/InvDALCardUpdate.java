package com.turquaz.inventory.dal;

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
 * @version $Id: InvDALCardUpdate.java,v 1.1 2007/01/22 16:36:34 huseyiner Exp $
 */
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

import com.turquaz.engine.bl.EngBLCommon;
import com.turquaz.engine.dal.TurqInventoryCard;


public class InvDALCardUpdate
{
	public static boolean hasTransactions(Integer cardId) throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "Select trans.amountIn,trans.amountOut from TurqInventoryTransaction as trans "
					+ " where trans.turqInventoryCard.id ="+ cardId;
			Query q = session.createQuery(query);
			List list = q.list();
			if (list.size() == 0)
			{
				return false;
			}
			else
			{
				Object[] amounts=(Object[])list.get(0);
				BigDecimal amountIn=(BigDecimal)amounts[0];
				BigDecimal amountOut=(BigDecimal)amounts[1];
				if (amountIn.doubleValue() == 0 && amountOut.doubleValue() == 0)
				{
					return false;
				}
				return true;
				
			}			
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static boolean hasInitialTransaction(TurqInventoryCard card) throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "Select transactions from TurqInventoryTransaction as transactions "
					+ "where transactions.turqInventoryCard = :invCard" + " and transactions.turqInventoryTransactionType.id ="
					+ EngBLCommon.INV_TRANS_INITIAL;
			Query q = session.createQuery(query);
			q.setParameter("invCard", card);
			List list = q.list();
			if (list.size() > 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	public static void deleteInitialTransactions(TurqInventoryCard invCard) throws Exception
	{
		try
		{
			Session session = EngDALSessionFactory.getSession();
			String query = "Select invTrans from TurqInventoryTransaction as invTrans "
					+ " where invTrans.turqInventoryTransactionType.id = " + EngBLCommon.INV_TRANS_INITIAL
					+ " and invTrans.turqInventoryCard = :invCard ";
			Query q = session.createQuery(query);
			q.setParameter("invCard", invCard);
			List list = q.list();
			for (int i = 0; i < list.size(); i++)
			{
				session.delete(list.get(i));
			}
			session.flush();
			session.clear();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}