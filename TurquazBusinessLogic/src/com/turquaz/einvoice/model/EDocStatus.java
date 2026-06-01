package com.turquaz.einvoice.model;

/**
 * Bir e-belgenin yaşam döngüsü durumu. {@code TurqEInvoiceStatus.status}
 * kolonunda string olarak saklanır.
 */
public enum EDocStatus {

    /** Uygulamada oluşturuldu, henüz "Kes" ile gönderilmedi. */
    DRAFT,
    /** Entegratöre gönderildi, GİB onayı bekleniyor. */
    SENT,
    /** GİB tarafından kabul edildi / kesinleşti. */
    ACCEPTED,
    /** Reddedildi. */
    REJECTED,
    /** İptal edildi. */
    CANCELLED,
    /** Gönderim sırasında hata oluştu. */
    ERROR
}
