package com.turquaz.einvoice.config;

import java.util.EnumMap;
import java.util.Map;

import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.ProviderCredentials;

/**
 * Kullanıcının tanımladığı bir entegratör profili: hangi adaptör (Nilvera,
 * İzibiz, ...), o adaptörün kimlik bilgileri ve belge tipi başına kullanılacak
 * fatura serisi. Çoklu entegratör senaryosunda birden fazla profil tanımlanır.
 */
public class ProviderProfile {

    private String id;             // kullanıcı tarafından verilen benzersiz ad
    private String providerName;   // registry anahtarı (örn. "nilvera")
    private ProviderCredentials credentials = new ProviderCredentials();
    private boolean enabled = true;

    /** Belge tipi başına fatura serisi (ETTN/numara çakışmasını önlemek için). */
    private final Map<EInvoiceType, String> seriesByType = new EnumMap<EInvoiceType, String>(EInvoiceType.class);

    public ProviderProfile() {
    }

    public ProviderProfile(String id, String providerName) {
        this.id = id;
        this.providerName = providerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public ProviderCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(ProviderCredentials credentials) {
        this.credentials = credentials;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSeries(EInvoiceType type) {
        return seriesByType.get(type);
    }

    public void setSeries(EInvoiceType type, String series) {
        seriesByType.put(type, series);
    }

    public Map<EInvoiceType, String> getSeriesByType() {
        return seriesByType;
    }
}
