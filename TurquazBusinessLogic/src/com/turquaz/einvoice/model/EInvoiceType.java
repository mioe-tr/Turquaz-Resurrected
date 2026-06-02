package com.turquaz.einvoice.model;

/**
 * Desteklenen GÝB e-belge tipleri. Ýlk teslimat e-Arþiv ile sýnýrlýdýr; e-Fatura
 * ve e-Ýrsaliye için yer ayrýlmýþtýr (ayný saðlayýcý arayüzünden geçerler).
 */
public enum EInvoiceType {

    EARSIV("e-Arþiv Fatura"),
    EFATURA("e-Fatura"),
    EIRSALIYE("e-Ýrsaliye");

    private final String label;

    EInvoiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
