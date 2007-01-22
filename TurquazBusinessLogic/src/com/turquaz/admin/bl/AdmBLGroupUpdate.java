package com.turquaz.admin.bl;

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
 * @author Huseyin Ergun
 * @version $Id: AdmBLGroupUpdate.java,v 1.1 2007/01/22 16:36:35 huseyiner Exp $
 */
import java.util.Calendar;

import server.util.MyThreadLocal;
import server.util.SessionInfo;

import com.turquaz.engine.dal.EngDALCommon;
import com.turquaz.engine.dal.TurqGroup;

public class AdmBLGroupUpdate
{
	public static void updateGroup(String name, String description, TurqGroup group) throws Exception
	{
	
			Calendar cal = Calendar.getInstance();
			group.setGroupsName(name);
			group.setGroupsDescription(description);
			group.setUpdateDate(cal.getTime());
            
            SessionInfo info = MyThreadLocal.getSessionInfo();            
            String username =(String)info.get("username");
            
			group.setUpdatedBy(username);
			EngDALCommon.updateObject(group);
	
	}
}