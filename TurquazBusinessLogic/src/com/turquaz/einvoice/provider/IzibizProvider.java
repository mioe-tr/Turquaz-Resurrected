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
 * Izibiz (Logo) ozel entegrator adaptoru - ikinci REST implementasyonu.
 *
 * Amaci, {@link EInvoiceProvider} arayuzunun gercekten saglayici-bagimsiz
 * oldugunu gostermektir: Nilvera ile ayni model/router/BL/UI uzerinden calisir,
 * yalnizca bu sinif degisir.
 *
 * NOT: Uc-nokta yollari ve JSON alan adlari Izibiz dokumantasyonuna gore
 * canliya geciste teyit edilmelidir (Postman koleksiyonu: izibiz-api-v1).
 */
public class IzibizProvider implements EInvoiceProvider {

    private static final String TEST_BASE = "https://earsivtest.izibiz.com.tr";
    private static final String LIVE_BASE = "https://earsiv.izibiz.com.tr";

    private static final SimpleDateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public String name() {
        return "izibiz";
    }

    public String displayName() {
        return "Izibiz";
    }

    public boolean supports(EInvoiceType type) {
        return type == EInvoiceType.EARSIV || type == EInvoiceType.EFATURA;
    }

    public EInvoiceResult submit(EInvoice doc, ProviderCredentials creds)
            throws EInvoiceException {
        if (doc.getType() != EInvoiceType.EARSIV) {
            throw new EInvoiceException("Izibiz adaptoru bu surumde yalnizca e-Arsiv gonderir: "
                    + doc.getType());
        }
        String body = buildJson(doc);
        try {
            HttpJson.Response resp = HttpJson.post(
                    baseUrl(creds) + "/v1/earchive/invoices",
                    body, authHeaders(creds));
            return interpret(resp, "gonderim");
        } catch (IOException e) {
            throw new EInvoiceException("Izibiz e-Arsiv gonderiminde ag hatasi: " + e.getMessage(), e);
        }
    }

    public EInvoiceResult queryStatus(String ettn, ProviderCredentials creds)
            throws EInvoiceException {
        try {
            HttpJson.Response resp = HttpJson.get(
                    baseUrl(creds) + "/v1/earchive/invoices/" + ettn,
                    authHeaders(creds));
            return interpret(resp, "durum sorgu");
        } catch (IOException e) {
            throw new EInvoiceException("Izibiz durum sorgusunda ag hatasi: " + e.getMessage(), e);
        }
    }

    public EInvoiceResult cancel(String ettn, String reason,
            ProviderCredentials creds) throws EInvoiceException {
        String body = "{" + Json.str("uuid", ettn) + "," + Json.str("reason", reason) + "}";
        try {
            HttpJson.Response resp = HttpJson.post(
                    baseUrl(creds) + "/v1/earchive/cancel", body, authHeaders(creds));
            EInvoiceResult r = interpret(resp, "iptal");
            if (r.isSuccess()) {
                r.setStatus(EDocStatus.CANCELLED);
            }
            return r;
        } catch (IOException e) {
            throw new EInvoiceException("Izibiz iptalde ag hatasi: " + e.getMessage(), e);
        }
    }

    // ---- yardimcilar -------------------------------------------------------

    private String baseUrl(ProviderCredentials creds) {
        if (creds.getBaseUrl() != null && creds.getBaseUrl().trim().length() > 0) {
            return creds.getBaseUrl().trim();
        }
        return creds.isTest() ? TEST_BASE : LIVE_BASE;
    }

    private Map<String, String> authHeaders(ProviderCredentials creds) {
        Map<String, String> h = new HashMap<String, String>();
        // Izibiz REST: oturum/token bazli; basitlestirilmis Bearer kullanimi.
        h.put("Authorization", "Bearer " + (creds.getApiKey() == null ? "" : creds.getApiKey()));
        return h;
    }

    private EInvoiceResult interpret(HttpJson.Response resp, String op) {
        if (resp.isSuccess()) {
            EInvoiceResult r =
                    EInvoiceResult.ok(EDocStatus.SENT);
            r.setRawResponse(resp.body);
            String uuid = Json.getString(resp.body, "uuid");
            if (uuid == null) {
                uuid = Json.getString(resp.body, "UUID");
            }
            r.setEttn(uuid);
            r.setProviderDocId(Json.getString(resp.body, "id"));
            return r;
        }
        EInvoiceResult r =
                EInvoiceResult.fail("Izibiz " + op + " basarisiz (HTTP " + resp.code + ")");
        r.setRawResponse(resp.body);
        return r;
    }

    private String buildJson(EInvoice doc) {
        EInvoiceParty s = doc.getSeller();
        EInvoiceParty b = doc.getBuyer();
        List<String> f = new ArrayList<String>();
        if (doc.getEttn() != null) {
            f.add(Json.str("uuid", doc.getEttn()));
        }
        if (doc.getSeries() != null) {
            f.add(Json.str("prefix", doc.getSeries()));
        }
        f.add(Json.str("issueDate", ISO_DATE.format(doc.getIssueDate())));
        f.add(Json.str("currency", doc.getCurrency()));
        f.add(Json.raw("supplier", partyJson(s)));
        f.add(Json.raw("customer", partyJson(b)));
        f.add(Json.raw("lines", linesJson(doc.getLines())));
        f.add(Json.raw("taxExclusiveAmount", doc.getLineExtensionTotal().toPlainString()));
        f.add(Json.raw("taxTotal", doc.getTaxTotal().toPlainString()));
        f.add(Json.raw("payableAmount", doc.getPayableTotal().toPlainString()));
        return "{" + join(f) + "}";
    }

    private String partyJson(EInvoiceParty p) {
        if (p == null) {
            return "null";
        }
        List<String> f = new ArrayList<String>();
        f.add(Json.str("taxNumber", p.getTaxNumber()));
        f.add(Json.str("name", p.getName()));
        if (p.getTaxOffice() != null) {
            f.add(Json.str("taxOffice", p.getTaxOffice()));
        }
        if (p.getAddress() != null) {
            f.add(Json.str("address", p.getAddress()));
        }
        f.add(Json.str("country", p.getCountry()));
        return "{" + join(f) + "}";
    }

    private String linesJson(List<EInvoiceLine> lines) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < lines.size(); i++) {
            EInvoiceLine l = lines.get(i);
            List<String> f = new ArrayList<String>();
            f.add(Json.str("name", l.getName()));
            f.add(Json.raw("quantity", l.getQuantity().toPlainString()));
            f.add(Json.str("unitCode", l.getUnit()));
            f.add(Json.raw("price", l.getUnitPrice().toPlainString()));
            f.add(Json.raw("vatRate", l.getVatRate().toPlainString()));
            f.add(Json.raw("vatAmount", l.getVatAmount().toPlainString()));
            f.add(Json.raw("lineAmount", l.getLineTotal().toPlainString()));
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
