package com.turquaz.einvoice.dal;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import server.util.EngDALSessionFactory;

import com.turquaz.engine.dal.EngDALCommon;
import com.turquaz.engine.dal.TurqCompany;
import com.turquaz.engine.dal.TurqCurrentTransaction;
import com.turquaz.engine.dal.TurqEInvoiceStatus;

/**
 * e-belge durum/okuma için veri eriþimi. Mevcut DAL desenini
 * ({@link EngDALSessionFactory} + {@link EngDALCommon}) yeniden kullanýr.
 */
public class EinvoiceDAL {

    public static TurqCurrentTransaction loadTransaction(Integer id) throws Exception {
        Session session = EngDALSessionFactory.getSession();
        return (TurqCurrentTransaction) session.get(TurqCurrentTransaction.class, id);
    }

    public static TurqCompany loadCompany() throws Exception {
        Session session = EngDALSessionFactory.getSession();
        Query q = session.createQuery("from TurqCompany");
        List list = q.list();
        return list.isEmpty() ? null : (TurqCompany) list.get(0);
    }

    /** Bir faturaya ait mevcut e-belge durum kaydýný döndürür (yoksa null). */
    public static TurqEInvoiceStatus findStatus(Integer transactionId) throws Exception {
        Session session = EngDALSessionFactory.getSession();
        Query q = session.createQuery(
                "from TurqEInvoiceStatus s where s.turqCurrentTransaction.id = :tid");
        q.setInteger("tid", transactionId.intValue());
        List list = q.list();
        return list.isEmpty() ? null : (TurqEInvoiceStatus) list.get(0);
    }

    public static void save(TurqEInvoiceStatus status) throws Exception {
        EngDALCommon.saveOrUpdateObject(status);
    }

    /** Bir faturaya (TurqBill) bagli cari hareketin id'sini paylasilan engine
     *  sequence uzerinden bulur; yoksa null. */
    public static Integer findTransactionIdByBill(Integer billId) throws Exception {
        Session session = EngDALSessionFactory.getSession();
        Query q = session.createQuery(
                "select ct.id from TurqCurrentTransaction ct, TurqBill b "
                + "where b.id = :billId and ct.turqEngineSequence.id = b.turqEngineSequence.id");
        q.setInteger("billId", billId.intValue());
        List list = q.list();
        return list.isEmpty() ? null : (Integer) list.get(0);
    }

    /** Bir cari hareketin e-belge durumunu kisa etiket olarak dondurur. */
    public static String statusLabel(Integer transactionId) {
        try { return labelOf(findStatus(transactionId)); } catch (Exception e) { return "-"; }
    }

    /** Bir faturanin (TurqBill) e-belge durum etiketi (cari harekete cozer). */
    public static String statusLabelByBill(Integer billId) {
        try { Integer t = findTransactionIdByBill(billId); return t == null ? "-" : statusLabel(t); }
        catch (Exception e) { return "-"; }
    }

    private static String labelOf(TurqEInvoiceStatus st) {
        if (st == null) return "-";
        String doc = "EFATURA".equals(st.getDocumentType()) ? "e-Fatura" : "e-Ar\u015fiv";
        String s = st.getStatus();
        String tr;
        if ("SENT".equals(s)) tr = "G\u00f6nderildi";
        else if ("ACCEPTED".equals(s)) tr = "Kabul";
        else if ("REJECTED".equals(s)) tr = "Ret";
        else if ("CANCELLED".equals(s)) tr = "\u0130ptal";
        else if ("ERROR".equals(s)) tr = "Hata";
        else if ("DRAFT".equals(s)) tr = "Taslak";
        else tr = (s == null ? "-" : s);
        return doc + ": " + tr;
    }
}
