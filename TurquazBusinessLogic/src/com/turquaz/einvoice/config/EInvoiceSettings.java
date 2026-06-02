package com.turquaz.einvoice.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.turquaz.einvoice.model.EInvoiceType;

/**
 * e-belge entegrasyon ayarları: tanımlı entegratör profilleri ve belge-tipi
 * başına aktif profil yönlendirmesi.
 *
 * <p>Çoklu entegratör mevzuat olarak mümkündür (VUK 509); farklı belge tipleri
 * farklı entegratörlere yönlendirilebilir. Aynı belge tipinde birden fazla
 * profil kullanılacaksa her profilin <b>ayrı fatura serisi</b> olmalıdır —
 * {@link #validate()} bunu denetler.
 *
 * <p>Kalıcılık (TurqSetting tablosu / properties dosyası) ve UI bağlama Faz 4'te
 * eklenecektir; burada bellek-içi model ve doğrulama tutulur.
 */
public class EInvoiceSettings {

    private final List<ProviderProfile> profiles = new ArrayList<ProviderProfile>();

    /** Belge tipi -> aktif profil id (yönlendirme tablosu). */
    private final Map<EInvoiceType, String> routing = new EnumMap<EInvoiceType, String>(EInvoiceType.class);

    // --- satıcı (mükellef) kimliği: e-belge gönderiminde "satıcı" tarafı -----
    // TurqCompany unvan/adres tutar ama VKN/vergi dairesi tutmaz; bunlar burada
    // tanımlanır (UI'da şirket ayarlarıyla doldurulur).
    private String sellerTaxNumber;
    private String sellerTaxOffice;
    private String sellerCity;
    private String sellerDistrict;

    public String getSellerTaxNumber() {
        return sellerTaxNumber;
    }

    public void setSellerTaxNumber(String sellerTaxNumber) {
        this.sellerTaxNumber = sellerTaxNumber;
    }

    public String getSellerTaxOffice() {
        return sellerTaxOffice;
    }

    public void setSellerTaxOffice(String sellerTaxOffice) {
        this.sellerTaxOffice = sellerTaxOffice;
    }

    public String getSellerCity() {
        return sellerCity;
    }

    public void setSellerCity(String sellerCity) {
        this.sellerCity = sellerCity;
    }

    public String getSellerDistrict() {
        return sellerDistrict;
    }

    public void setSellerDistrict(String sellerDistrict) {
        this.sellerDistrict = sellerDistrict;
    }

    public List<ProviderProfile> getProfiles() {
        return profiles;
    }

    public void addProfile(ProviderProfile p) {
        profiles.add(p);
    }

    public ProviderProfile getProfile(String id) {
        for (ProviderProfile p : profiles) {
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void removeProfile(String id) {
        ProviderProfile p = getProfile(id);
        if (p != null) {
            profiles.remove(p);
        }
        // yönlendirmede bu profili kullanan girdileri temizle
        List<EInvoiceType> toClear = new ArrayList<EInvoiceType>();
        for (Map.Entry<EInvoiceType, String> e : routing.entrySet()) {
            if (e.getValue() != null && e.getValue().equals(id)) {
                toClear.add(e.getKey());
            }
        }
        for (EInvoiceType t : toClear) {
            routing.remove(t);
        }
    }

    /** Verilen belge tipi için aktif profili belirler. */
    public void setActiveProfile(EInvoiceType type, String profileId) {
        routing.put(type, profileId);
    }

    public ProviderProfile getActiveProfile(EInvoiceType type) {
        String id = routing.get(type);
        return id == null ? null : getProfile(id);
    }

    public Map<EInvoiceType, String> getRouting() {
        return routing;
    }

    /**
     * Çoklu entegratör uyumluluğunu denetler: aynı belge tipi için aynı
     * entegratör adaptörü birden fazla profilde kullanılıyorsa, bu profillerin
     * serileri birbirinden farklı olmalıdır (GİB hata 1104/1163'ü önlemek için).
     *
     * @return ihlal mesajları; boşsa ayarlar uyumludur.
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<String>();
        for (EInvoiceType type : EInvoiceType.values()) {
            // (provider + series) çiftlerinin tipi içinde benzersizliği
            List<String> seen = new ArrayList<String>();
            for (ProviderProfile p : profiles) {
                if (!p.isEnabled()) {
                    continue;
                }
                String series = p.getSeries(type);
                if (series == null) {
                    continue;
                }
                String key = p.getProviderName() + "/" + series;
                if (seen.contains(key)) {
                    errors.add(type.getLabel() + " için '" + p.getProviderName()
                            + "' entegratöründe '" + series
                            + "' serisi birden fazla profilde kullanılıyor; "
                            + "her profil ayrı seri kullanmalı.");
                } else {
                    seen.add(key);
                }
            }
        }
        return errors;
    }
}
