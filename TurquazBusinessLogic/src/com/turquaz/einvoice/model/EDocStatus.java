package com.turquaz.einvoice.model;

/**
 * Bir e-belgenin yaþam döngüsü durumu. {@code TurqEInvoiceStatus.status}
 * kolonunda string olarak saklanýr.
 */
public enum EDocStatus {

    /** Uygulamada oluþturuldu, henüz "Kes" ile gönderilmedi. */
    DRAFT,
    /** Entegratöre gönderildi, GÝB onayý bekleniyor. */
    SENT,
    /** GÝB tarafýndan kabul edildi / kesinleþti. */
    ACCEPTED,
    /** Reddedildi. */
    REJECTED,
    /** Ýptal edildi. */
    CANCELLED,
    /** Gönderim sýrasýnda hata oluþtu. */
    ERROR
}
