package com.turquaz.einvoice.routing;

import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.config.ProviderProfile;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.EInvoiceException;
import com.turquaz.einvoice.provider.EInvoiceProvider;
import com.turquaz.einvoice.provider.EInvoiceProviderRegistry;

/**
 * Bir belge tipini, ayarlardaki aktif profile göre doğru entegratör adaptörüne
 * ve fatura serisine yönlendirir. "Kes" akışının kalbi: BL hangi entegratörle
 * çalışılacağını bilmek zorunda kalmadan buradan {@link Route} alır.
 */
public class EInvoiceRouter {

    private final EInvoiceSettings settings;

    public EInvoiceRouter(EInvoiceSettings settings) {
        this.settings = settings;
    }

    /** Çözülmüş yönlendirme: adaptör + kimlik bilgisi + seri. */
    public static final class Route {
        public final EInvoiceProvider provider;
        public final ProviderProfile profile;
        public final String series;

        Route(EInvoiceProvider provider, ProviderProfile profile, String series) {
            this.provider = provider;
            this.profile = profile;
            this.series = series;
        }
    }

    public Route resolve(EInvoiceType type) throws EInvoiceException {
        ProviderProfile profile = settings.getActiveProfile(type);
        if (profile == null) {
            throw new EInvoiceException(type.getLabel()
                    + " için aktif entegratör profili tanımlı değil. Ayarlardan bir profil seçin.");
        }
        if (!profile.isEnabled()) {
            throw new EInvoiceException("Seçili profil pasif: " + profile.getId());
        }
        EInvoiceProvider provider = EInvoiceProviderRegistry.get(profile.getProviderName());
        if (provider == null) {
            throw new EInvoiceException("Bilinmeyen entegratör adaptörü: " + profile.getProviderName());
        }
        if (!provider.supports(type)) {
            throw new EInvoiceException(provider.displayName() + " bu belge tipini desteklemiyor: "
                    + type.getLabel());
        }
        return new Route(provider, profile, profile.getSeries(type));
    }
}
