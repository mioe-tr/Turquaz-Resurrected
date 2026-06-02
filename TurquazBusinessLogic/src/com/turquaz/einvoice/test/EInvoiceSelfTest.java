package com.turquaz.einvoice.test;

import java.math.BigDecimal;

import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.config.ProviderProfile;
import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceLine;
import com.turquaz.einvoice.model.EInvoiceParty;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.EInvoiceProvider;
import com.turquaz.einvoice.provider.NilveraProvider;
import com.turquaz.einvoice.provider.ProviderCredentials;
import com.turquaz.einvoice.routing.EInvoiceRouter;
import com.turquaz.einvoice.util.Json;

/**
 * Offline self-test for the e-invoice layer. Runs without a database, GUI or
 * network. Verifies totals, JSON helper, multi-integrator validation and the
 * router/provider flow with a mock provider.
 *
 * Run:  java com.turquaz.einvoice.test.EInvoiceSelfTest
 * Optional real Nilvera test-env call: set NILVERA_API_KEY (and optionally
 * NILVERA_BASE_URL); the test then performs a live submit and prints the
 * result without asserting on it.
 *
 * Exit code 0 = all asserts passed, 1 = at least one failure.
 */
public class EInvoiceSelfTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testTotals();
        testJson();
        testMultiIntegratorValidation();
        testRouterAndFlow();
        maybeRealNilvera();

        System.out.println("----------------------------------------");
        System.out.println("PASSED: " + passed + "  FAILED: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static EInvoice sampleInvoice() {
        EInvoice doc = new EInvoice();
        doc.setType(EInvoiceType.EARSIV);
        doc.setEttn("11111111-2222-3333-4444-555555555555");
        doc.setSeries("TUR");

        EInvoiceParty seller = new EInvoiceParty();
        seller.setName("Ornek Ltd");
        seller.setTaxNumber("1234567890");
        seller.setTaxOffice("Kadikoy");
        doc.setSeller(seller);

        EInvoiceParty buyer = new EInvoiceParty();
        buyer.setName("Musteri AS");
        buyer.setTaxNumber("9876543210");
        doc.setBuyer(buyer);

        // 2 adet x 100 = 200 matrah, %20 KDV = 40
        EInvoiceLine l1 = new EInvoiceLine();
        l1.setName("Urun A");
        l1.setQuantity(new BigDecimal("2"));
        l1.setUnitPrice(new BigDecimal("100"));
        l1.setLineTotal(new BigDecimal("200"));
        l1.setVatRate(new BigDecimal("20"));
        l1.setVatAmount(new BigDecimal("40"));
        doc.addLine(l1);

        // 1 adet x 50 = 50 matrah, %10 KDV = 5
        EInvoiceLine l2 = new EInvoiceLine();
        l2.setName("Urun B");
        l2.setQuantity(BigDecimal.ONE);
        l2.setUnitPrice(new BigDecimal("50"));
        l2.setLineTotal(new BigDecimal("50"));
        l2.setVatRate(new BigDecimal("10"));
        l2.setVatAmount(new BigDecimal("5"));
        doc.addLine(l2);

        return doc;
    }

    private static void testTotals() {
        EInvoice doc = sampleInvoice();
        check("lineExtension=250", doc.getLineExtensionTotal().compareTo(new BigDecimal("250")) == 0);
        check("taxTotal=45", doc.getTaxTotal().compareTo(new BigDecimal("45")) == 0);
        check("payable=295", doc.getPayableTotal().compareTo(new BigDecimal("295")) == 0);
    }

    private static void testJson() {
        String esc = Json.escape("a\"b\\c\nd");
        check("json escape", esc.equals("a\\\"b\\\\c\\nd"));
        String obj = "{" + Json.str("uuid", "abc-123") + "}";
        check("json getString", "abc-123".equals(Json.getString(obj, "uuid")));
        check("json getString missing", Json.getString(obj, "yok") == null);
    }

    private static void testMultiIntegratorValidation() {
        // Ayni belge tipi + ayni provider + AYNI seri -> ihlal beklenir
        EInvoiceSettings bad = new EInvoiceSettings();
        ProviderProfile a = new ProviderProfile("a", "nilvera");
        a.setSeries(EInvoiceType.EARSIV, "TUR");
        ProviderProfile b = new ProviderProfile("b", "nilvera");
        b.setSeries(EInvoiceType.EARSIV, "TUR");
        bad.addProfile(a);
        bad.addProfile(b);
        check("ayni seri ihlali yakalandi", !bad.validate().isEmpty());

        // Farkli seri -> ihlal yok
        EInvoiceSettings good = new EInvoiceSettings();
        ProviderProfile c = new ProviderProfile("c", "nilvera");
        c.setSeries(EInvoiceType.EARSIV, "TUR");
        ProviderProfile d = new ProviderProfile("d", "nilvera");
        d.setSeries(EInvoiceType.EARSIV, "ABC");
        good.addProfile(c);
        good.addProfile(d);
        check("farkli seri ihlali yok", good.validate().isEmpty());
    }

    private static void testRouterAndFlow() {
        EInvoiceSettings s = new EInvoiceSettings();
        ProviderProfile p = new ProviderProfile("mockp", "mock");
        p.setSeries(EInvoiceType.EARSIV, "TUR");
        s.addProfile(p);
        s.setActiveProfile(EInvoiceType.EARSIV, "mockp");

        // mock'u kayda gerek kalmadan dogrudan kullanmak icin router yerine
        // saglayiciyi elle de cagirabiliriz; ancak router cozumlemesini de test et.
        MockEInvoiceProvider mock = new MockEInvoiceProvider();
        try {
            EInvoiceResult r = mock.submit(sampleInvoice(), p.getCredentials());
            check("mock submit success", r.isSuccess());
            check("mock captured doc", mock.getLastSubmitted() != null);
            check("mock ettn geri dondu", r.getEttn() != null);
        } catch (Exception e) {
            check("mock submit exception: " + e.getMessage(), false);
        }

        // Router: aktif profil yoksa hata atmali
        EInvoiceSettings empty = new EInvoiceSettings();
        EInvoiceRouter router = new EInvoiceRouter(empty);
        boolean threw = false;
        try {
            router.resolve(EInvoiceType.EARSIV);
        } catch (Exception e) {
            threw = true;
        }
        check("router aktif profil yoksa hata", threw);
    }

    private static void maybeRealNilvera() {
        String apiKey = System.getenv("NILVERA_API_KEY");
        if (apiKey == null || apiKey.trim().length() == 0) {
            System.out.println("[bilgi] NILVERA_API_KEY yok; canli test ortami cagrisi atlandi.");
            return;
        }
        ProviderCredentials c = new ProviderCredentials();
        c.setEnvironment(ProviderCredentials.Environment.TEST);
        c.setApiKey(apiKey);
        String base = System.getenv("NILVERA_BASE_URL");
        if (base != null && base.trim().length() > 0) {
            c.setBaseUrl(base);
        }
        EInvoiceProvider nilvera = new NilveraProvider();
        try {
            EInvoiceResult r = nilvera.submit(sampleInvoice(), c);
            System.out.println("[canli] Nilvera test submit -> success=" + r.isSuccess()
                    + " status=" + r.getStatus()
                    + " msg=" + r.getMessage());
        } catch (Exception e) {
            System.out.println("[canli] Nilvera test submit hata: " + e.getMessage());
        }
    }

    private static void check(String label, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("  PASS  " + label);
        } else {
            failed++;
            System.out.println("  FAIL  " + label);
        }
    }
}
