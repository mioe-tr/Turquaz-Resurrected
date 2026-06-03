package com.turquaz.einvoice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Saðlayýcý-baðýmsýz e-belge modeli. Turquaz fatura kayýtlarýndan
 * (TurqCurrentTransaction + satýrlar) {@code TurqInvoiceMapper} tarafýndan
 * üretilir; her {@link com.turquaz.einvoice.provider.EInvoiceProvider} bu
 * modeli kendi API biçimine (Nilvera JSON, UBL-TR, ...) çevirir.
 */
public class EInvoice {

    private EInvoiceType type = EInvoiceType.EARSIV;
    private String ettn;            // 36 karakterlik UUID (boþsa provider/mapper üretir)
    private String series;          // fatura serisi (örn. "TUR")
    private String documentNo;      // tam belge no (seri + sýra), opsiyonel
    private Date issueDate = new Date();
    private String currency = "TRY";

    /** e-Arþiv için satýþ kanalý: KAGIT / ELEKTRONIK (internet satýþý). */
    private String sendType = "ELEKTRONIK";

    private EInvoiceParty seller;
    private EInvoiceParty buyer;
    private final List<EInvoiceLine> lines = new ArrayList<EInvoiceLine>();

    private String note;

    public EInvoiceType getType() {
        return type;
    }

    public void setType(EInvoiceType type) {
        this.type = type;
    }

    public String getEttn() {
        return ettn;
    }

    public void setEttn(String ettn) {
        this.ettn = ettn;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public EInvoiceParty getSeller() {
        return seller;
    }

    public void setSeller(EInvoiceParty seller) {
        this.seller = seller;
    }

    public EInvoiceParty getBuyer() {
        return buyer;
    }

    public void setBuyer(EInvoiceParty buyer) {
        this.buyer = buyer;
    }

    public List<EInvoiceLine> getLines() {
        return lines;
    }

    public void addLine(EInvoiceLine line) {
        this.lines.add(line);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /** KDV hariç toplam (mal/hizmet tutarý, satýr indirimi düþülmüþ). */
    public BigDecimal getLineExtensionTotal() {
        BigDecimal t = BigDecimal.ZERO;
        for (EInvoiceLine l : lines) {
            if (l.getLineTotal() != null) {
                t = t.add(l.getLineTotal());
            }
        }
        return t;
    }

    /** Toplam hesaplanan KDV. */
    public BigDecimal getTaxTotal() {
        BigDecimal t = BigDecimal.ZERO;
        for (EInvoiceLine l : lines) {
            if (l.getVatAmount() != null) {
                t = t.add(l.getVatAmount());
            }
        }
        return t;
    }

    /** Ödenecek toplam (matrah + KDV). */
    public BigDecimal getPayableTotal() {
        return getLineExtensionTotal().add(getTaxTotal());
    }
}
