package com.turquaz.einvoice.model;

/**
 * Bir e-belge iþleminin (gönder / durum sorgula / iptal) sonucu. Saðlayýcýdan
 * baðýmsýz; {@code EinvoiceBLIssue} bunu {@code TurqEInvoiceStatus} kaydýna yazar.
 */
public class EInvoiceResult {

    private boolean success;
    private EDocStatus status;
    private String ettn;
    private String providerDocId;
    private String pdfUrl;
    private String message;       // hata veya bilgi mesajý
    private String rawResponse;   // entegratörün ham yanýtý (loglama/teþhis için)

    public static EInvoiceResult ok(EDocStatus status) {
        EInvoiceResult r = new EInvoiceResult();
        r.success = true;
        r.status = status;
        return r;
    }

    public static EInvoiceResult fail(String message) {
        EInvoiceResult r = new EInvoiceResult();
        r.success = false;
        r.status = EDocStatus.ERROR;
        r.message = message;
        return r;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public EDocStatus getStatus() {
        return status;
    }

    public void setStatus(EDocStatus status) {
        this.status = status;
    }

    public String getEttn() {
        return ettn;
    }

    public void setEttn(String ettn) {
        this.ettn = ettn;
    }

    public String getProviderDocId() {
        return providerDocId;
    }

    public void setProviderDocId(String providerDocId) {
        this.providerDocId = providerDocId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
}
