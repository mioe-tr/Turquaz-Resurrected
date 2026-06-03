package com.turquaz.einvoice.provider;

/**
 * Bir özel entegratöre baðlanmak için gereken kimlik/uç-nokta bilgileri.
 * Çoklu entegratör senaryosunda her entegratör profili kendi kimlik bilgisini
 * ve GB-PK etiketini (senderAlias) taþýr.
 */
public class ProviderCredentials {

    public enum Environment {
        TEST, LIVE
    }

    private Environment environment = Environment.TEST;
    private String baseUrl;        // entegratör API kök adresi (boþsa saðlayýcý varsayýlaný)
    private String apiKey;         // OAuth2 client / API anahtarý veya kullanýcý adý
    private String apiSecret;      // parola / secret
    private String senderAlias;    // GB-PK etiketi (e-Fatura yönlendirmesi için)

    public ProviderCredentials() {
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isTest() {
        return environment == Environment.TEST;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getSenderAlias() {
        return senderAlias;
    }

    public void setSenderAlias(String senderAlias) {
        this.senderAlias = senderAlias;
    }
}
