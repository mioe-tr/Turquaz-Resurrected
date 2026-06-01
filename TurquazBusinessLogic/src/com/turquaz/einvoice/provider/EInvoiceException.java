package com.turquaz.einvoice.provider;

/** e-belge gönderim/sorgu katmanı hatası. */
public class EInvoiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public EInvoiceException(String message) {
        super(message);
    }

    public EInvoiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
