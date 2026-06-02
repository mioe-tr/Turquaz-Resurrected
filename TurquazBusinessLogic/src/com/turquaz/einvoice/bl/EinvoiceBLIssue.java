package com.turquaz.einvoice.bl;

import java.util.Date;

import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.dal.EinvoiceDAL;
import com.turquaz.einvoice.model.EDocStatus;
import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.EInvoiceException;
import com.turquaz.einvoice.routing.EInvoiceRouter;
import com.turquaz.engine.dal.TurqCompany;
import com.turquaz.engine.dal.TurqCurrentTransaction;
import com.turquaz.engine.dal.TurqEInvoiceStatus;

/**
 * "Kes" iş kuralı: bir Turquaz faturasını seçili entegratöre gönderip
 * kesinleştirir ve sonucu {@link TurqEInvoiceStatus} kaydına yazar.
 *
 * <p>Akış: yükle → mükerrer kontrolü → yönlendir (entegratör + seri) → maple
 * → gönder → durumu sakla. Fatura otomatik kesilmez; bu metot kullanıcının
 * UI'daki "e-Arşiv Kes" aksiyonuyla tetiklenir.
 */
public class EinvoiceBLIssue {

    private static final int RESPONSE_MAX = 2000;

    /**
     * Verilen faturayı keser.
     *
     * @param transactionId {@code TurqCurrentTransaction} id'si
     * @param type          belge tipi (şimdilik EARSIV)
     * @param settings      entegratör profilleri + yönlendirme + satıcı kimliği
     * @param currentUser   işlemi yapan kullanıcı (audit alanları için)
     */
    public static EInvoiceResult issue(Integer transactionId, EInvoiceType type,
                                       EInvoiceSettings settings, String currentUser)
            throws EInvoiceException {
        try {
            TurqCurrentTransaction header = EinvoiceDAL.loadTransaction(transactionId);
            if (header == null) {
                throw new EInvoiceException("Fatura bulunamadı: id=" + transactionId);
            }

            TurqEInvoiceStatus status = EinvoiceDAL.findStatus(transactionId);
            if (status != null && isFinal(status.getStatus())) {
                throw new EInvoiceException("Bu fatura zaten kesilmiş (durum: "
                        + status.getStatus() + ", ETTN: " + status.getEttn()
                        + "). Tekrar kesilemez.");
            }

            EInvoiceRouter router = new EInvoiceRouter(settings);
            EInvoiceRouter.Route route = router.resolve(type);

            TurqCompany company = EinvoiceDAL.loadCompany();
            EInvoice doc = com.turquaz.einvoice.map.TurqInvoiceMapper.toEArchive(
                    header, company, settings, route.series);

            EInvoiceResult result = route.provider.submit(doc, route.profile.getCredentials());

            persist(status, header, type, route, doc, result, currentUser);
            return result;
        } catch (EInvoiceException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EInvoiceException("e-belge kesme sırasında beklenmeyen hata: " + e.getMessage(), e);
        }
    }

    private static boolean isFinal(String status) {
        return EDocStatus.SENT.name().equals(status)
                || EDocStatus.ACCEPTED.name().equals(status);
    }

    private static void persist(TurqEInvoiceStatus status, TurqCurrentTransaction header,
                                EInvoiceType type, EInvoiceRouter.Route route,
                                EInvoice doc, EInvoiceResult result, String currentUser)
            throws Exception {
        Date now = new Date();
        String user = currentUser == null ? "system" : currentUser;
        if (status == null) {
            status = new TurqEInvoiceStatus();
            status.setTurqCurrentTransaction(header);
            status.setCreatedBy(user);
            status.setCreationDate(now);
        }
        status.setDocumentType(type.name());
        status.setProvider(route.profile.getProviderName());
        status.setInvoiceSeries(route.series);
        status.setEttn(result.getEttn() != null ? result.getEttn() : doc.getEttn());
        status.setProviderDocId(result.getProviderDocId());
        status.setStatus(result.getStatus() != null
                ? result.getStatus().name() : EDocStatus.ERROR.name());
        status.setGibResponse(truncate(buildResponseText(result)));
        status.setPdfUrl(result.getPdfUrl());
        status.setUpdatedBy(user);
        status.setLastModified(now);
        EinvoiceDAL.save(status);
    }

    private static String buildResponseText(EInvoiceResult result) {
        StringBuilder b = new StringBuilder();
        if (result.getMessage() != null) {
            b.append(result.getMessage());
        }
        if (result.getRawResponse() != null) {
            if (b.length() > 0) {
                b.append(" | ");
            }
            b.append(result.getRawResponse());
        }
        return b.toString();
    }

    private static String truncate(String s) {
        if (s == null) {
            return null;
        }
        return s.length() <= RESPONSE_MAX ? s : s.substring(0, RESPONSE_MAX);
    }
}
