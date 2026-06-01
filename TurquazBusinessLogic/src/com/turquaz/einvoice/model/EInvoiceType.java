package com.turquaz.einvoice.model;

/**
 * Desteklenen GİB e-belge tipleri. İlk teslimat e-Arşiv ile sınırlıdır; e-Fatura
 * ve e-İrsaliye için yer ayrılmıştır (aynı sağlayıcı arayüzünden geçerler).
 */
public enum EInvoiceType {

    EARSIV("e-Arşiv Fatura"),
    EFATURA("e-Fatura"),
    EIRSALIYE("e-İrsaliye");

    private final String label;

    EInvoiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
