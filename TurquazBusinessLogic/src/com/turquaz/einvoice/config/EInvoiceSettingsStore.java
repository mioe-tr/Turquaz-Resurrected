package com.turquaz.einvoice.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.ProviderCredentials;

/**
 * {@link EInvoiceSettings}'i basit bir properties dosyasýna kaydeder/okur.
 * Dosya, HSQLDB ile ayný {@code database/} dizininde tutulur
 * ({@code database/einvoice.properties}). API anahtarý vb. burada düz metin
 * saklanýr; üretimde þifreleme eklenebilir (bkz. PLAN riskler).
 */
public final class EInvoiceSettingsStore {

    private static final String DEFAULT_PATH = "database/einvoice.properties";

    private EInvoiceSettingsStore() {
    }

    private static File file() {
        String custom = System.getProperty("turquaz.einvoice.config");
        return custom != null ? new File(custom) : new File(DEFAULT_PATH);
    }

    public static EInvoiceSettings load() {
        EInvoiceSettings s = new EInvoiceSettings();
        File f = file();
        if (!f.exists()) {
            return s;
        }
        Properties p = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(f);
            p.load(in);
        } catch (Exception e) {
            return s;
        } finally {
            close(in);
        }

        s.setSellerTaxNumber(p.getProperty("seller.taxNumber"));
        s.setSellerTaxOffice(p.getProperty("seller.taxOffice"));
        s.setSellerCity(p.getProperty("seller.city"));
        s.setSellerDistrict(p.getProperty("seller.district"));

        String ids = p.getProperty("profiles", "");
        for (String id : split(ids)) {
            ProviderProfile prof = new ProviderProfile(id, p.getProperty(key(id, "provider")));
            prof.setEnabled(!"false".equals(p.getProperty(key(id, "enabled"))));
            ProviderCredentials c = prof.getCredentials();
            c.setBaseUrl(p.getProperty(key(id, "baseUrl")));
            c.setApiKey(p.getProperty(key(id, "apiKey")));
            c.setApiSecret(p.getProperty(key(id, "apiSecret")));
            c.setSenderAlias(p.getProperty(key(id, "senderAlias")));
            if ("LIVE".equals(p.getProperty(key(id, "environment")))) {
                c.setEnvironment(ProviderCredentials.Environment.LIVE);
            }
            for (EInvoiceType t : EInvoiceType.values()) {
                String series = p.getProperty(key(id, "series." + t.name()));
                if (series != null) {
                    prof.setSeries(t, series);
                }
            }
            s.addProfile(prof);
        }

        for (EInvoiceType t : EInvoiceType.values()) {
            String pid = p.getProperty("routing." + t.name());
            if (pid != null) {
                s.setActiveProfile(t, pid);
            }
        }
        return s;
    }

    public static void save(EInvoiceSettings s) throws Exception {
        Properties p = new Properties();
        putIf(p, "seller.taxNumber", s.getSellerTaxNumber());
        putIf(p, "seller.taxOffice", s.getSellerTaxOffice());
        putIf(p, "seller.city", s.getSellerCity());
        putIf(p, "seller.district", s.getSellerDistrict());

        List<String> ids = new ArrayList<String>();
        for (ProviderProfile prof : s.getProfiles()) {
            ids.add(prof.getId());
            putIf(p, key(prof.getId(), "provider"), prof.getProviderName());
            p.setProperty(key(prof.getId(), "enabled"), String.valueOf(prof.isEnabled()));
            ProviderCredentials c = prof.getCredentials();
            putIf(p, key(prof.getId(), "baseUrl"), c.getBaseUrl());
            putIf(p, key(prof.getId(), "apiKey"), c.getApiKey());
            putIf(p, key(prof.getId(), "apiSecret"), c.getApiSecret());
            putIf(p, key(prof.getId(), "senderAlias"), c.getSenderAlias());
            p.setProperty(key(prof.getId(), "environment"), c.getEnvironment().name());
            for (EInvoiceType t : EInvoiceType.values()) {
                putIf(p, key(prof.getId(), "series." + t.name()), prof.getSeries(t));
            }
        }
        p.setProperty("profiles", join(ids));

        for (EInvoiceType t : EInvoiceType.values()) {
            ProviderProfile active = s.getActiveProfile(t);
            if (active != null) {
                p.setProperty("routing." + t.name(), active.getId());
            }
        }

        File f = file();
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(f);
            p.store(out, "Turquaz e-belge entegrasyon ayarlari");
        } finally {
            close(out);
        }
    }

    // ---- yardýmcýlar -------------------------------------------------------

    private static String key(String id, String suffix) {
        return "profile." + id + "." + suffix;
    }

    private static void putIf(Properties p, String k, String v) {
        if (v != null && v.length() > 0) {
            p.setProperty(k, v);
        }
    }

    private static List<String> split(String csv) {
        List<String> out = new ArrayList<String>();
        if (csv != null) {
            for (String s : csv.split(",")) {
                String t = s.trim();
                if (t.length() > 0) {
                    out.add(t);
                }
            }
        }
        return out;
    }

    private static String join(List<String> ids) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                b.append(",");
            }
            b.append(ids.get(i));
        }
        return b.toString();
    }

    private static void close(java.io.Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignore) {
            }
        }
    }
}
