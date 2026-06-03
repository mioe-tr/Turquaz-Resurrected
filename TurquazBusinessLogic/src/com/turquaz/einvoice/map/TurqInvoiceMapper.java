package com.turquaz.einvoice.map;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.model.EInvoice;
import com.turquaz.einvoice.model.EInvoiceLine;
import com.turquaz.einvoice.model.EInvoiceParty;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.engine.dal.TurqCompany;
import com.turquaz.engine.dal.TurqCurrentCard;
import com.turquaz.engine.dal.TurqCurrentTransaction;
import com.turquaz.engine.dal.TurqInventoryCard;
import com.turquaz.engine.dal.TurqInventoryTransaction;

/**
 * Turquaz fatura kayýtlarýný saðlayýcý-baðýmsýz {@link EInvoice} modeline çevirir.
 *
 * <p>Ýliþki: fatura baþlýðý {@code TurqCurrentTransaction} bir
 * {@code TurqEngineSequence}'e baðlýdýr; o sýraya baðlý
 * {@code TurqInventoryTransaction} kayýtlarý KDV'li ürün satýrlarýdýr
 * (amountIn/Out, unitPrice, vatRate, vatAmount). Alýcý bilgisi baþlýðýn
 * {@code TurqCurrentCard}'ýndan (VKN = cardsTaxNumber, vergi dairesi =
 * cardsTaxDepartment) gelir; satýcý bilgisi {@code TurqCompany} + ayarlardaki
 * mükellef kimliðinden kurulur.
 *
 * <p>Bu metotlar açýk bir Hibernate session içinde çaðrýlmalýdýr (lazy
 * koleksiyon gezinimi için); {@code EinvoiceBLIssue} bunu saðlar.
 */
public final class TurqInvoiceMapper {

    private TurqInvoiceMapper() {
    }

    public static EInvoice toEArchive(TurqCurrentTransaction header,
                                      TurqCompany company,
                                      EInvoiceSettings settings,
                                      String series) {
        EInvoice doc = new EInvoice();
        doc.setType(EInvoiceType.EARSIV);
        doc.setEttn(UUID.randomUUID().toString());
        doc.setSeries(series);
        doc.setDocumentNo(header.getTransactionsDocumentNo());
        if (header.getTransactionsDate() != null) {
            doc.setIssueDate(header.getTransactionsDate());
        }
        doc.setNote(header.getTransactionsDefinition());

        doc.setSeller(buildSeller(company, settings));
        doc.setBuyer(buildBuyer(header.getTurqCurrentCard()));

        addLines(doc, header);
        return doc;
    }

    private static EInvoiceParty buildSeller(TurqCompany company, EInvoiceSettings settings) {
        EInvoiceParty s = new EInvoiceParty();
        if (company != null) {
            s.setName(company.getCompanyName());
            s.setAddress(company.getCompanyAddress());
            s.setPhone(company.getCompanyTelephone());
        }
        if (settings != null) {
            s.setTaxNumber(settings.getSellerTaxNumber());
            s.setTaxOffice(settings.getSellerTaxOffice());
            s.setCity(settings.getSellerCity());
            s.setDistrict(settings.getSellerDistrict());
        }
        return s;
    }

    private static EInvoiceParty buildBuyer(TurqCurrentCard card) {
        EInvoiceParty b = new EInvoiceParty();
        if (card != null) {
            b.setName(card.getCardsName());
            b.setTaxNumber(card.getCardsTaxNumber());
            b.setTaxOffice(card.getCardsTaxDepartment());
            b.setAddress(card.getCardsAddress());
        }
        return b;
    }

    private static void addLines(EInvoice doc, TurqCurrentTransaction header) {
        if (header.getTurqEngineSequence() == null) {
            return;
        }
        Set invTrans = header.getTurqEngineSequence().getTurqInventoryTransactions();
        if (invTrans == null) {
            return;
        }
        for (Iterator it = invTrans.iterator(); it.hasNext(); ) {
            TurqInventoryTransaction t = (TurqInventoryTransaction) it.next();
            doc.addLine(toLine(t));
        }
    }

    private static EInvoiceLine toLine(TurqInventoryTransaction t) {
        EInvoiceLine line = new EInvoiceLine();

        TurqInventoryCard card = t.getTurqInventoryCard();
        line.setName(card != null ? card.getCardName() : t.getDefinition());

        // Satýþ stoðu azaltýr (amountOut); yoksa amountIn kullan.
        BigDecimal qty = nz(t.getAmountOut());
        if (qty.signum() == 0) {
            qty = nz(t.getAmountIn());
        }
        if (qty.signum() == 0) {
            qty = BigDecimal.ONE;
        }
        line.setQuantity(qty);

        line.setUnitPrice(nz(t.getUnitPrice()));
        line.setDiscountAmount(nz(t.getDiscountAmount()));
        // totalPrice = KDV hariç satýr matrahý (miktar*birim - iskonto).
        line.setLineTotal(nz(t.getTotalPrice()));
        line.setVatRate(nz(t.getVatRate()));
        line.setVatAmount(nz(t.getVatAmount()));
        return line;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
