package com.turquaz.einvoice.test;

import com.turquaz.einvoice.model.EDocStatus;
import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.EInvoiceException;
import com.turquaz.einvoice.provider.EInvoiceProvider;
import com.turquaz.einvoice.provider.ProviderCredentials;

/**
 * Test double provider: captures the submitted document and returns a canned
 * ACCEPTED result. Lets the routing / settings / flow be exercised offline
 * (no network, no DB) so the provider abstraction can be verified.
 */
public class MockEInvoiceProvider implements EInvoiceProvider {

    private EInvoice lastSubmitted;
    private ProviderCredentials lastCredentials;

    public String name() {
        return "mock";
    }

    public String displayName() {
        return "Mock (test)";
    }

    public boolean supports(EInvoiceType type) {
        return true;
    }

    public EInvoiceResult submit(EInvoice doc, ProviderCredentials credentials) throws EInvoiceException {
        this.lastSubmitted = doc;
        this.lastCredentials = credentials;
        EInvoiceResult r = EInvoiceResult.ok(EDocStatus.ACCEPTED);
        r.setEttn(doc.getEttn());
        r.setProviderDocId("MOCK-1");
        r.setRawResponse("{\"status\":\"ACCEPTED\"}");
        return r;
    }

    public EInvoiceResult queryStatus(String ettn, ProviderCredentials credentials) throws EInvoiceException {
        EInvoiceResult r = EInvoiceResult.ok(EDocStatus.ACCEPTED);
        r.setEttn(ettn);
        return r;
    }

    public EInvoiceResult cancel(String ettn, String reason, ProviderCredentials credentials) throws EInvoiceException {
        EInvoiceResult r = EInvoiceResult.ok(EDocStatus.CANCELLED);
        r.setEttn(ettn);
        return r;
    }

    public EInvoice getLastSubmitted() {
        return lastSubmitted;
    }

    public ProviderCredentials getLastCredentials() {
        return lastCredentials;
    }
}
