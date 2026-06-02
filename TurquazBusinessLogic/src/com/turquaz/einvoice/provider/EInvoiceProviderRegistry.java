package com.turquaz.einvoice.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Kullanýlabilir entegratör adaptörlerinin merkezi kaydý. Uygulama açýlýþýnda
 * bilinen saðlayýcýlar burada register edilir; UI ayar ekraný listeyi buradan
 * çeker, router seçili saðlayýcýyý buradan alýr.
 */
public final class EInvoiceProviderRegistry {

    private static final Map<String, EInvoiceProvider> PROVIDERS =
            new LinkedHashMap<String, EInvoiceProvider>();

    static {
        // Ýlk teslimat: Nilvera. Diðer entegratörler ayný arayüzle eklenecek
        // (Ýzibiz, Uyumsoft, Foriba/Sovos, EDM, Paraþüt, Turkcell e-Þirket, ...).
        register(new NilveraProvider());
    }

    private EInvoiceProviderRegistry() {
    }

    public static void register(EInvoiceProvider provider) {
        PROVIDERS.put(provider.name(), provider);
    }

    public static EInvoiceProvider get(String name) {
        return name == null ? null : PROVIDERS.get(name);
    }

    public static boolean has(String name) {
        return name != null && PROVIDERS.containsKey(name);
    }

    /** Kayýtlý tüm saðlayýcýlar (ekleme sýrasýna göre). */
    public static List<EInvoiceProvider> all() {
        return Collections.unmodifiableList(new ArrayList<EInvoiceProvider>(PROVIDERS.values()));
    }
}
