package com.turquaz.utility.reporting;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

import com.turquaz.accounting.AccKeys;
import com.turquaz.accounting.bl.AccBLTransactionSearch;
import com.turquaz.admin.AdmKeys;
import com.turquaz.admin.bl.AdmBLCompanyInfo;
import com.turquaz.bill.BillKeys;
import com.turquaz.bill.bl.BillBLSearchBill;
import com.turquaz.common.HashBag;
import com.turquaz.consignment.ConsKeys;
import com.turquaz.consignment.bl.ConBLSearchConsignment;
import com.turquaz.consignment.dal.ConDALSearchConsignment;
import com.turquaz.current.CurKeys;
import com.turquaz.current.bl.CurBLCurrentCardSearch;
import com.turquaz.engine.EngKeys;
import com.turquaz.engine.bl.EngBLCommon;
import com.turquaz.engine.bl.EngBLCurrencyToWords;
import com.turquaz.engine.dal.TurqAccountingTransaction;
import com.turquaz.engine.dal.TurqBill;
import com.turquaz.engine.dal.TurqBillInEngineSequence;
import com.turquaz.engine.dal.TurqConsignment;
import com.turquaz.engine.dal.TurqCurrentCard;
import com.turquaz.engine.dal.TurqViewBillTransTotal;
import com.turquaz.engine.dal.TurqViewCurrentAmountTotal;
import com.turquaz.engine.dal.TurqViewInvPriceTotal;
import net.sf.jasperreports.engine.JasperPrint;

public class PrintUtility
{
	public static HashBag printBill(HashMap argMap) throws Exception
	{
		Integer billId = (Integer) argMap.get(BillKeys.BILL_ID);
		Boolean printBalance = (Boolean) argMap.get(BillKeys.BILL_PRINT_BALANCE);
		String template = (String) argMap.get(BillKeys.BILL_TEMPLATE);
		
		
		Session session=EngDALSessionFactory.getSession();
		TurqBill bill=(TurqBill)session.load(TurqBill.class,billId);
		
		List list = BillBLSearchBill.getBillInfo(bill);
		
		SimpleDateFormat dformat = new SimpleDateFormat("dd-MM-yyyy"); 
		
		
		Map parameters = new HashMap();
		TurqViewBillTransTotal billview = BillBLSearchBill.getBillView(billId);
		
		BigDecimal discount = billview.getDiscountamount();
		BigDecimal VAT = billview.getVatamount();
		BigDecimal specialVATAmount = billview.getSpecialvatamount();
		BigDecimal invoiceSum = billview.getTotalprice();
		BigDecimal invoiceTotal = invoiceSum.subtract(discount);
		BigDecimal grandTotal = invoiceTotal.add(VAT).add(specialVATAmount);
		parameters.put("invoiceSum", invoiceSum);
		parameters.put("invoiceTotal", invoiceTotal.add(specialVATAmount));
		parameters.put("invoiceDiscount", discount);
		parameters.put("invoiceVAT", VAT);
		parameters.put("invoiceGrandTotal", grandTotal);
		parameters.put("invoiceGrandTotalText", EngBLCurrencyToWords.getTurkishCurrencyInWords(grandTotal));
		parameters.put("invoiceDate", dformat.format(bill.getBillsDate()));
		parameters.put("dueDate", dformat.format(bill.getDueDate()));
		TurqCurrentCard curCard = bill.getTurqCurrentCard();
		parameters.put("currentName", curCard.getCardsName());
		parameters.put("currentAddress", curCard.getCardsAddress());
		parameters.put("currentTaxNumber", curCard.getCardsTaxNumber());
		parameters.put("currentTaxDepartment", curCard.getCardsTaxDepartment());
		parameters.put("currentId", curCard.getCardsCurrentCode());
		parameters.put("totalSpecVAT", specialVATAmount);
		Iterator iter = bill.getTurqBillInEngineSequences().iterator();
		if (iter.hasNext())
		{
			Iterator iter2 = ((TurqBillInEngineSequence) iter.next()).getTurqEngineSequence()
					.getTurqConsignments().iterator();
			TurqConsignment cons = null;
			if (iter2.hasNext())
			{
				cons = (TurqConsignment) iter2.next();
				parameters.put("despatchNoteDate", dformat.format(cons.getConsignmentsDate()));
				parameters.put("despatchNoteId", cons.getConsignmentDocumentNo());
			}
			else
			{
				parameters.put("despatchNoteDate", "");
				parameters.put("despatchNoteId", "");
			}
		}
		else
		{
			parameters.put("despatchNoteDate", "");
			parameters.put("despatchNoteId", "");
		}
		parameters.put(	"billType",
						(bill.getBillsType() == EngBLCommon.COMMON_BUY_INT || bill.getBillsType() == EngBLCommon.COMMON_RETURN_SELL_INT)
								? new Integer(1)
								: new Integer(0));
		
		
		
		argMap = new HashMap();
		argMap.put(CurKeys.CUR_CARD_ID, curCard.getId());
		
		TurqViewCurrentAmountTotal currentView = CurBLCurrentCardSearch.getCurrentCardView(argMap);
		
		BigDecimal allTotal = currentView.getTransactionsBalanceNow();
		allTotal = allTotal.multiply(new BigDecimal(-1));
		BigDecimal oldAllTotal = allTotal.subtract(grandTotal);
		parameters.put("showBalance", printBalance);
		parameters.put("currentBalance", oldAllTotal);
		parameters.put("currentNewBalance", allTotal);
		parameters.put("definition", bill.getBillsDefinition());
		NumberFormat formatter = DecimalFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		parameters.put("formatter", formatter);
		NumberFormat formatter2 = DecimalFormat.getInstance();
		formatter2.setMaximumFractionDigits(4);
		formatter2.setMinimumFractionDigits(4);
		parameters.put("formatter2", formatter2);
		
		
		String[] fields = new String[]{"trans_id", "card_units_factor", "card_inventory_code", "card_name",
				"units_name", "amount", "unit_price", "total_price", "warehouses_name","transactions_vat","discount_amount" };
		
		String reportName="/reports/invoice/" +template;
		
		JasperPrint jasperPrint=ReportGenerator.generateReport(reportName,list,fields,parameters);
		
				
		HashBag printBag = new HashBag();
		printBag.put(EngKeys.REPORT,jasperPrint);
		return printBag;
	}
	
	
	
	public static HashBag printConsignment(HashMap argMap) throws Exception
	{
		
		Integer consId=(Integer)argMap.get(ConsKeys.CONS_ID);
		
		Session session=EngDALSessionFactory.getSession();
		
		TurqConsignment cons=(TurqConsignment)session.load(TurqConsignment.class,consId);
		
		List list=ConDALSearchConsignment.getConsignmentInfo(cons);
		
		Map parameters = new HashMap();
		SimpleDateFormat dformat = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$
		TurqViewInvPriceTotal billView=ConBLSearchConsignment.getViewInvTotal(cons.getTurqEngineSequence().getId());
		BigDecimal invoiceSum = billView.getTotalprice().add(billView.getSpecialvatamount());
		BigDecimal discount = billView.getDiscountamount();
		BigDecimal specialVAT = billView.getVatamount();
		BigDecimal invoiceTotal = invoiceSum.subtract(discount);
		BigDecimal grandTotal = invoiceTotal.add(specialVAT);
		parameters.put("invoiceSum", invoiceSum);
		parameters.put("invoiceTotal", invoiceTotal);
		parameters.put("invoiceDiscount", discount);
		parameters.put("invoiceVAT", specialVAT);
		parameters.put("invoiceGrandTotal", grandTotal);
		parameters.put("invoiceGrandTotalText", EngBLCurrencyToWords.getTurkishCurrencyInWords(grandTotal));
		TurqCurrentCard curCard = cons.getTurqCurrentCard();
		parameters.put("currentName", curCard.getCardsName());
		parameters.put("currentAddress", curCard.getCardsAddress());
		parameters.put("currentTaxNumber", curCard.getCardsTaxNumber());
		parameters.put("currentTaxDepartment", curCard.getCardsTaxDepartment());
		parameters.put("currentId", curCard.getCardsCurrentCode());
		parameters.put("despatchNoteDate", dformat.format(cons.getConsignmentsDate()));
		parameters.put("despatchNoteId", cons.getConsignmentDocumentNo());
		
		argMap = new HashMap();
		argMap.put(CurKeys.CUR_CARD_ID,curCard.getId());

		
		TurqViewCurrentAmountTotal currentView =CurBLCurrentCardSearch.getCurrentCardView(argMap);
		
				
		BigDecimal allTotal = (currentView.getTransactionsBalanceNow() == null) ? new BigDecimal(0) : currentView
				.getTransactionsBalanceNow();
		BigDecimal oldAllTotal = allTotal.subtract(grandTotal);
		parameters.put("currentBalance", oldAllTotal);
		parameters.put("currentNewBalance", allTotal);
		parameters.put("definition", cons.getConsignmentsDefinition());
		NumberFormat formatter = DecimalFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		parameters.put("formatter", formatter);
		
		
		String[] fields = new String[]{"trans_id", "card_units_factor", "card_inventory_code",
				"card_name", "units_name", "amount", "unit_price", "total_price"};
		
		
		JasperPrint jasperPrint=ReportGenerator.generateReport("/reports/consignment/template1.jrxml",list,fields,parameters);
		
		HashBag printBag=new HashBag();
		
		printBag.put(EngKeys.REPORT,jasperPrint);
		
		return printBag;
	}
	
	public static HashBag printAccTransaction(HashMap argMap) throws Exception
	{
		Integer transId=(Integer)argMap.get(AccKeys.ACC_TRANS_ID);
		
		
		Session session=EngDALSessionFactory.getSession();
		
		TurqAccountingTransaction trans=(TurqAccountingTransaction)session.load(TurqAccountingTransaction.class,transId);
		SimpleDateFormat dformat = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$	
		List list=AccBLTransactionSearch.getAccTransInfo(transId);
		HashBag companyBag = AdmBLCompanyInfo.getCompany();
		Map parameters = new HashMap();
		parameters.put("companyName", companyBag.get(AdmKeys.ADM_COMPANY_NAME).toString()); 
		parameters.put("transDate", dformat.format(trans.getTransactionsDate())); 
		parameters.put("transNo", trans.getTransactionDocumentNo());
		NumberFormat formatter = DecimalFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		parameters.put("formatter", formatter); 

		String[] fields = new String[]{"accountName", "accountCode", "topAccountName",
				"topAccountCode", "dept_amount", "credit_amount", "transaction_definition", "columnId"};
		
		BigDecimal total = new BigDecimal (0);
		
		
		 for (Iterator i = list.iterator(); i.hasNext(); ) {
		        Object [] row = ((Object[]) i.next());
		        total = total.add( (BigDecimal)row [4]);
		    }
		 parameters.put("transGrandTotalText", EngBLCurrencyToWords.getTurkishCurrencyInWords(total));
		JasperPrint jasperPrint=ReportGenerator.generateReport("/reports/accounting/AccountingTransaction.jrxml",list,fields,parameters);
		
			
		HashBag printBag=new HashBag();
		
		printBag.put(EngKeys.REPORT,jasperPrint);
		
		return printBag;
	}
	
	
	public static HashBag getInvoiceTemplates()	
	{
		HashBag returnBag = new HashBag();
		returnBag.put(EngKeys.BILL_TEMPLATES, new HashMap());

		File reportDir = new File("reports/invoice/");
		
		if (reportDir.exists())
		{
			File fileList[] = reportDir.listFiles(new FilenameFilter()
			{
				public boolean accept(File arg0, String arg1)
				{
					if (arg1.endsWith(".jrxml"))
						return true;
					return false;
				}
			});
			for (int i = 0; i < fileList.length; i++)
			{
				returnBag.put(EngKeys.BILL_TEMPLATES, i, fileList[i].getName());
			}
		}
		return returnBag;
	}
	
}
