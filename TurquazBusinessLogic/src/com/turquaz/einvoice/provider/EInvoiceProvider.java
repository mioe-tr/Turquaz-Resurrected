package com.turquaz.einvoice.provider;

import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;

/**
 * Tüm özel entegratör adaptörlerinin uyguladýðý saðlayýcý-baðýmsýz arayüz.
 * Yeni bir entegratör eklemek = bu arayüzü uygulayan yeni bir sýnýf + ayar
 * formunda bir satýr. Böylece uygulamanýn geri kalaný (router, BL, UI) hangi
 * entegratörle çalýþýldýðýný bilmek zorunda kalmaz.
 */
public interface EInvoiceProvider {

    /** Kayýt/seçim için benzersiz, kýsa ad (örn. "nilvera", "izibiz"). */
    String name();

    /** Kullanýcýya gösterilecek görünen ad (örn. "Nilvera"). */
    String displayName();

    /** Bu entegratörün verilen belge tipini destekleyip desteklemediði. */
    boolean supports(EInvoiceType type);

    /**
     * Belgeyi entegratöre gönderir ("Kes"). Baþarýlýysa sonuçta ETTN /
     * entegratör belge kimliði / durum döner.
     */
    EInvoiceResult submit(EInvoice doc, ProviderCredentials credentials) throws EInvoiceException;

    /** Daha önce gönderilmiþ bir belgenin güncel durumunu sorgular. */
    EInvoiceResult queryStatus(String ettn, ProviderCredentials credentials) throws EInvoiceException;

    /** Belgeyi iptal eder (destekleniyorsa). */
    EInvoiceResult cancel(String ettn, String reason, ProviderCredentials credentials) throws EInvoiceException;
}
