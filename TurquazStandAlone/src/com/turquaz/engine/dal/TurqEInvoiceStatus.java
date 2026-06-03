package com.turquaz.engine.dal;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * GÝB e-belge (e-Arþiv / e-Fatura / e-Ýrsaliye) gönderim durumunu, bir
 * {@link TurqCurrentTransaction} faturasýna baðlý olarak tutan kayýt.
 *
 * Fatura uygulamada normal þekilde oluþturulur (DRAFT); kullanýcý "Kes"
 * aksiyonuyla seçili özel entegratöre gönderince burada ETTN / durum /
 * entegratör / seri / GÝB yanýtý saklanýr. Çoklu entegratör senaryosunda her
 * belge hangi entegratör + seri ile kesildiyse o bilgi burada kalýr.
 */
public class TurqEInvoiceStatus implements Serializable {

    /** identifier field */
    private Integer id;

    /** persistent field - baðlý fatura baþlýðý */
    private TurqCurrentTransaction turqCurrentTransaction;

    /** persistent field - EARSIV / EFATURA / EIRSALIYE */
    private String documentType;

    /** persistent field - kullanýlan özel entegratör adý (nilvera, izibiz, ...) */
    private String provider;

    /** nullable persistent field - entegratör baþýna ayrý fatura serisi */
    private String invoiceSeries;

    /** nullable persistent field - 36 karakterlik ETTN (UUID) */
    private String ettn;

    /** nullable persistent field - entegratörün döndürdüðü belge kimliði */
    private String providerDocId;

    /** persistent field - DRAFT / SENT / ACCEPTED / REJECTED / CANCELLED / ERROR */
    private String status;

    /** nullable persistent field - GÝB / entegratör yanýt veya hata metni */
    private String gibResponse;

    /** nullable persistent field - kesilen belgenin PDF / görüntü adresi */
    private String pdfUrl;

    /** persistent field */
    private String createdBy;

    /** persistent field */
    private Date creationDate;

    /** persistent field */
    private String updatedBy;

    /** persistent field */
    private Date lastModified;

    /** default constructor */
    public TurqEInvoiceStatus() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TurqCurrentTransaction getTurqCurrentTransaction() {
        return this.turqCurrentTransaction;
    }

    public void setTurqCurrentTransaction(TurqCurrentTransaction turqCurrentTransaction) {
        this.turqCurrentTransaction = turqCurrentTransaction;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getInvoiceSeries() {
        return this.invoiceSeries;
    }

    public void setInvoiceSeries(String invoiceSeries) {
        this.invoiceSeries = invoiceSeries;
    }

    public String getEttn() {
        return this.ettn;
    }

    public void setEttn(String ettn) {
        this.ettn = ettn;
    }

    public String getProviderDocId() {
        return this.providerDocId;
    }

    public void setProviderDocId(String providerDocId) {
        this.providerDocId = providerDocId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGibResponse() {
        return this.gibResponse;
    }

    public void setGibResponse(String gibResponse) {
        this.gibResponse = gibResponse;
    }

    public String getPdfUrl() {
        return this.pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("documentType", getDocumentType())
            .append("provider", getProvider())
            .append("status", getStatus())
            .append("ettn", getEttn())
            .toString();
    }

}
