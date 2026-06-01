package com.turquaz.einvoice.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Kullanılabilir entegratör adaptörlerinin merkezi kaydı. Uygulama açılışında
 * bilinen sağlayıcılar burada register edilir; UI ayar ekranı listeyi buradan
 * çeker, router seçili sağlayıcıyı buradan alır.
 */
public final class EInvoiceProviderRegistry {

    private static final Map<String, EInvoiceProvider> PROVIDERS =
            new LinkedHashMap<String, EInvoiceProvider>();

    static {
        // İlk teslimat: Nilvera. Diğer entegratörler aynı arayüzle eklenecek
        // (İzibiz, Uyumsoft, Foriba/Sovos, EDM, Paraşüt, Turkcell e-Şirket, ...).
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

    /** Kayıtlı tüm sağlayıcılar (ekleme sırasına göre). */
    public static List<EInvoiceProvider> all() {
        return Collections.unmodifiableList(new ArrayList<EInvoiceProvider>(PROVIDERS.values()));
    }
}
