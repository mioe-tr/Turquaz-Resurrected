package com.turquaz.einvoice.provider;

import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;

/**
 * Tüm özel entegratör adaptörlerinin uyguladığı sağlayıcı-bağımsız arayüz.
 * Yeni bir entegratör eklemek = bu arayüzü uygulayan yeni bir sınıf + ayar
 * formunda bir satır. Böylece uygulamanın geri kalanı (router, BL, UI) hangi
 * entegratörle çalışıldığını bilmek zorunda kalmaz.
 */
public interface EInvoiceProvider {

    /** Kayıt/seçim için benzersiz, kısa ad (örn. "nilvera", "izibiz"). */
    String name();

    /** Kullanıcıya gösterilecek görünen ad (örn. "Nilvera"). */
    String displayName();

    /** Bu entegratörün verilen belge tipini destekleyip desteklemediği. */
    boolean supports(EInvoiceType type);

    /**
     * Belgeyi entegratöre gönderir ("Kes"). Başarılıysa sonuçta ETTN /
     * entegratör belge kimliği / durum döner.
     */
    EInvoiceResult submit(EInvoice doc, ProviderCredentials credentials) throws EInvoiceException;

    /** Daha önce gönderilmiş bir belgenin güncel durumunu sorgular. */
    EInvoiceResult queryStatus(String ettn, ProviderCredentials credentials) throws EInvoiceException;

    /** Belgeyi iptal eder (destekleniyorsa). */
    EInvoiceResult cancel(String ettn, String reason, ProviderCredentials credentials) throws EInvoiceException;
}
