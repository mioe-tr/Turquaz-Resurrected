package com.turquaz.einvoice.provider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.turquaz.einvoice.model.EDocStatus;
import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceLine;
import com.turquaz.einvoice.model.EInvoiceParty;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.util.HttpJson;
import com.turquaz.einvoice.util.Json;

/**
 * Nilvera özel entegratör adaptörü (ilk implementasyon). REST + Bearer
 * kimlik doðrulama kullanýr; e-Arþiv gönderimi, durum sorgu ve iptal saðlar.
 *
 * NOT: Uç-nokta yollarý ve JSON alan adlarý Nilvera geliþtirici
 * dokümantasyonuna (developer.nilvera.com) göre canlýya geçiþte teyit
 * edilmelidir; bu sýnýf saðlayýcý arayüzünün referans uygulamasýdýr ve
 * test ortamýna (apitest.nilvera.com) karþý denenecek þekilde yazýlmýþtýr.
 */
public class NilveraProvider implements EInvoiceProvider {

    private static final String TEST_BASE = "https://apitest.nilvera.com";
    private static final String LIVE_BASE = "https://api.nilvera.com";

    private static final SimpleDateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public String name() {
        return "nilvera";
    }

    public String displayName() {
        return "Nilvera";
    }

    public boolean supports(EInvoiceType type) {
        // Nilvera e-Arþiv, e-Fatura ve e-Ýrsaliye'yi destekler.
        return true;
    }

    public EInvoiceResult submit(EInvoice doc, ProviderCredentials creds) throws EInvoiceException {
        if (doc.getType() != EInvoiceType.EARSIV) {
            throw new EInvoiceException("Bu sürümde Nilvera adaptörü yalnýzca e-Arþiv gönderimini uygular: "
                    + doc.getType());
        }
        String body = buildEArchiveJson(doc);
        try {
            HttpJson.Response resp = HttpJson.post(
                    baseUrl(creds) + "/earchive/send/model",
                    body,
                    authHeaders(creds));
            return interpret(resp, "gönderim");
        } catch (IOException e) {
            throw new EInvoiceException("Nilvera e-Arþiv gönderiminde að hatasý: " + e.getMessage(), e);
        }
    }

    public EInvoiceResult queryStatus(String ettn, ProviderCredentials creds) throws EInvoiceException {
        try {
            HttpJson.Response resp = HttpJson.get(
                    baseUrl(creds) + "/earchive/status/" + ettn,
                    authHeaders(creds));
            return interpret(resp, "durum sorgu");
        } catch (IOException e) {
            throw new EInvoiceException("Nilvera durum sorgusunda að hatasý: " + e.getMessage(), e);
        }
    }

    public EInvoiceResult cancel(String ettn, String reason, ProviderCredentials creds)
            throws EInvoiceException {
        String body = "{" + Json.str("UUID", ettn) + "," + Json.str("CancelReason", reason) + "}";
        try {
            HttpJson.Response resp = HttpJson.post(
                    baseUrl(creds) + "/earchive/cancel",
                    body,
                    authHeaders(creds));
            EInvoiceResult r = interpret(resp, "iptal");
            if (r.isSuccess()) {
                r.setStatus(EDocStatus.CANCELLED);
            }
            return r;
        } catch (IOException e) {
            throw new EInvoiceException("Nilvera iptalde að hatasý: " + e.getMessage(), e);
        }
    }

    // ---- yardýmcýlar -------------------------------------------------------

    private String baseUrl(ProviderCredentials creds) {
        if (creds.getBaseUrl() != null && creds.getBaseUrl().trim().length() > 0) {
            return creds.getBaseUrl().trim();
        }
        return creds.isTest() ? TEST_BASE : LIVE_BASE;
    }

    private Map<String, String> authHeaders(ProviderCredentials creds) {
        Map<String, String> h = new HashMap<String, String>();
        h.put("Authorization", "Bearer " + (creds.getApiKey() == null ? "" : creds.getApiKey()));
        return h;
    }

    private EInvoiceResult interpret(HttpJson.Response resp, String op) {
        if (resp.isSuccess()) {
            EInvoiceResult r = EInvoiceResult.ok(EDocStatus.SENT);
            r.setRawResponse(resp.body);
            String uuid = Json.getString(resp.body, "UUID");
            if (uuid == null) {
                uuid = Json.getString(resp.body, "uuid");
            }
            r.setEttn(uuid);
            r.setProviderDocId(Json.getString(resp.body, "InvoiceId"));
            return r;
        }
        EInvoiceResult r = EInvoiceResult.fail("Nilvera " + op + " baþarýsýz (HTTP " + resp.code + ")");
        r.setRawResponse(resp.body);
        return r;
    }

    /**
     * e-Arþiv belgesini Nilvera "model" JSON gövdesine çevirir. Alan adlarý
     * Nilvera EArchiveModel þemasýna yakýn tutulmuþtur; canlýda doküman ile
     * birebir teyit edilmelidir.
     */
    private String buildEArchiveJson(EInvoice doc) {
        EInvoiceParty s = doc.getSeller();
        EInvoiceParty b = doc.getBuyer();

        List<String> fields = new ArrayList<String>();
        if (doc.getEttn() != null) {
            fields.add(Json.str("UUID", doc.getEttn()));
        }
        if (doc.getSeries() != null) {
            fields.add(Json.str("Prefix", doc.getSeries()));
        }
        fields.add(Json.str("IssueDate", ISO_DATE.format(doc.getIssueDate())));
        fields.add(Json.str("CurrencyCode", doc.getCurrency()));
        fields.add(Json.str("SendType", doc.getSendType()));
        fields.add(Json.raw("CompanyInfo", partyJson(s)));
        fields.add(Json.raw("CustomerInfo", partyJson(b)));
        fields.add(Json.raw("InvoiceLines", linesJson(doc.getLines())));
        fields.add(Json.raw("LineExtensionAmount", doc.getLineExtensionTotal().toPlainString()));
        fields.add(Json.raw("TaxTotalAmount", doc.getTaxTotal().toPlainString()));
        fields.add(Json.raw("PayableAmount", doc.getPayableTotal().toPlainString()));
        if (doc.getNote() != null) {
            fields.add(Json.str("Notes", doc.getNote()));
        }
        return "{" + join(fields) + "}";
    }

    private String partyJson(EInvoiceParty p) {
        if (p == null) {
            return "null";
        }
        List<String> f = new ArrayList<String>();
        f.add(Json.str("TaxNumber", p.getTaxNumber()));
        f.add(Json.str("Name", p.getName()));
        if (p.getTaxOffice() != null) {
            f.add(Json.str("TaxOffice", p.getTaxOffice()));
        }
        if (p.getAddress() != null) {
            f.add(Json.str("Address", p.getAddress()));
        }
        if (p.getDistrict() != null) {
            f.add(Json.str("District", p.getDistrict()));
        }
        if (p.getCity() != null) {
            f.add(Json.str("City", p.getCity()));
        }
        f.add(Json.str("Country", p.getCountry()));
        if (p.getPostalCode() != null) {
            f.add(Json.str("PostalCode", p.getPostalCode()));
        }
        if (p.getEmail() != null) {
            f.add(Json.str("Email", p.getEmail()));
        }
        return "{" + join(f) + "}";
    }

    private String linesJson(List<EInvoiceLine> lines) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < lines.size(); i++) {
            EInvoiceLine l = lines.get(i);
            List<String> f = new ArrayList<String>();
            f.add(Json.str("Name", l.getName()));
            f.add(Json.raw("Quantity", l.getQuantity().toPlainString()));
            f.add(Json.str("UnitType", l.getUnit()));
            f.add(Json.raw("Price", l.getUnitPrice().toPlainString()));
            f.add(Json.raw("AllowanceTotal", l.getDiscountAmount().toPlainString()));
            f.add(Json.raw("KDVPercent", l.getVatRate().toPlainString()));
            f.add(Json.raw("KDVAmount", l.getVatAmount().toPlainString()));
            f.add(Json.raw("LineExtensionAmount", l.getLineTotal().toPlainString()));
            b.append("{").append(join(f)).append("}");
            if (i < lines.size() - 1) {
                b.append(",");
            }
        }
        return b.append("]").toString();
    }

    private static String join(List<String> parts) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                b.append(",");
            }
            b.append(parts.get(i));
        }
        return b.toString();
    }
}
