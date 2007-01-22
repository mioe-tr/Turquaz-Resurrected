package com.turquaz.engine.ui.editors;

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
 * @author Onsel
 * @version $Id: InventoryNameCellEditor.java,v 1.1 2007/01/22 17:02:51 huseyiner Exp $
 */
import org.eclipse.jface.contentassist.TextContentAssistSubjectAdapter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import com.turquaz.engine.ui.contentassist.TurquazContentAssistant;

public class InventoryNameCellEditor extends TextCellEditor
{
	TextContentAssistSubjectAdapter adapter;
	TurquazContentAssistant asistant;
	TableViewer viewer = null;
	String columnName = "";

	public InventoryNameCellEditor(Composite parent,String columName)
	{
		super(parent);
		setValueValid(true);
		 this.columnName = columName;
	}
	
	 public void setTableViewer(TableViewer tableViewer)
	 {
	        this.viewer = tableViewer;
	 }
	 
	public Control createControl(Composite parent)
	{
		Text text = (Text) super.createControl(parent);
		adapter = new TextContentAssistSubjectAdapter(text);
		asistant = new TurquazContentAssistant(adapter, 10);
		return text;
	}
}