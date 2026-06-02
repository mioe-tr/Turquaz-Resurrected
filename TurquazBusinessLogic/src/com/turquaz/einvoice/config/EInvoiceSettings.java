package com.turquaz.einvoice.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.turquaz.einvoice.model.EInvoiceType;

/**
 * e-belge entegrasyon ayarlarý: tanýmlý entegratör profilleri ve belge-tipi
 * baþýna aktif profil yönlendirmesi.
 *
 * <p>Çoklu entegratör mevzuat olarak mümkündür (VUK 509); farklý belge tipleri
 * farklý entegratörlere yönlendirilebilir. Ayný belge tipinde birden fazla
 * profil kullanýlacaksa her profilin <b>ayrý fatura serisi</b> olmalýdýr -
 * {@link #validate()} bunu denetler.
 *
 * <p>Kalýcýlýk (TurqSetting tablosu / properties dosyasý) ve UI baðlama Faz 4'te
 * eklenecektir; burada bellek-içi model ve doðrulama tutulur.
 */
public class EInvoiceSettings {

    private final List<ProviderProfile> profiles = new ArrayList<ProviderProfile>();

    /** Belge tipi -> aktif profil id (yönlendirme tablosu). */
    private final Map<EInvoiceType, String> routing = new EnumMap<EInvoiceType, String>(EInvoiceType.class);

    // --- satýcý (mükellef) kimliði: e-belge gönderiminde "satýcý" tarafý -----
    // TurqCompany unvan/adres tutar ama VKN/vergi dairesi tutmaz; bunlar burada
    // tanýmlanýr (UI'da þirket ayarlarýyla doldurulur).
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
     * Çoklu entegratör uyumluluðunu denetler: ayný belge tipi için ayný
     * entegratör adaptörü birden fazla profilde kullanýlýyorsa, bu profillerin
     * serileri birbirinden farklý olmalýdýr (GÝB hata 1104/1163'ü önlemek için).
     *
     * @return ihlal mesajlarý; boþsa ayarlar uyumludur.
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<String>();
        for (EInvoiceType type : EInvoiceType.values()) {
            // (provider + series) çiftlerinin tipi içinde benzersizliði
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
                            + "' serisi birden fazla profilde kullanýlýyor; "
                            + "her profil ayrý seri kullanmalý.");
                } else {
                    seen.add(key);
                }
            }
        }
        return errors;
    }
}
