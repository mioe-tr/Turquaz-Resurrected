package com.turquaz.inventory.bl;

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
 * @version $Id: InvBLProfitAnalysis.java,v 1.6 2005/03/30 17:09:40 cemdayanik Exp $
 */
import java.util.List;
import com.turquaz.inventory.dal.InvDALProfitAnalysis;

public class InvBLProfitAnalysis
{
	/**
	 * @param type
	 *             0 - Ortalama deger
	 * @return
	 */
	public static List getTransactionTotals() throws Exception
	{
		try
		{
			return InvDALProfitAnalysis.getInventoryTotalsAccordingToAvarage();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
}