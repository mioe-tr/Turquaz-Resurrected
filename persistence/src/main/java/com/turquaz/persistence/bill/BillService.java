/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.core.BusinessRuleException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fatura yönetim servisi. Eski {@code BillBLAddBill}/{@code BillBLUpdateBill}/
 * {@code BillBLSearchBill} sınıflarının modern karşılığı.
 *
 * <p>Bu sürüm faturayı başlık olarak yönetir; kalem (satır) detayları —
 * stok hareketi ve yevmiye postlama ile entegrasyon — Stage E'de gelir.
 * {@code is_open}: fatura kapanmamış ise açık; {@code bills_printed}: yazdırıldı.
 */
@Service
public class BillService {

    public static final int TYPE_SALES = 1;
    public static final int TYPE_PURCHASE = 2;

    private final BillRepository repo;

    public BillService(BillRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Bill create(NewBill cmd) {
        if (cmd.type() != TYPE_SALES && cmd.type() != TYPE_PURCHASE) {
            throw new BusinessRuleException(
                    "Geçersiz fatura türü: " + cmd.type() + " (1=satış, 2=alış)");
        }
        if (cmd.dueDate().isBefore(cmd.billDate())) {
            throw new BusinessRuleException("Vade tarihi fatura tarihinden önce olamaz");
        }
        if (repo.existsByCompanyIdAndBillDocumentNo(cmd.companyId(), cmd.documentNo())) {
            throw new BusinessRuleException(
                    "Bu fatura belge numarası zaten kayıtlı: " + cmd.documentNo());
        }

        Bill bill = new Bill();
        bill.setCompanyId(cmd.companyId());
        bill.setBillsType(cmd.type());
        bill.setBillsDate(cmd.billDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        bill.setDueDate(cmd.dueDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        bill.setBillsDefinition(cmd.definition());
        bill.setBillDocumentNo(cmd.documentNo());
        bill.setCurrentCardsId(cmd.currentCardId());
        bill.setExchangeRateId(cmd.exchangeRateId());
        bill.setEngineSequencesId(cmd.engineSequenceId());
        bill.setBillsPrinted(false);
        bill.setIsOpen(true);
        bill.setCreatedBy(cmd.currentUser());
        bill.setUpdatedBy(cmd.currentUser());
        return repo.save(bill);
    }

    @Transactional
    public Bill markPrinted(UUID billId, String currentUser) {
        Bill bill = get(billId);
        bill.setBillsPrinted(true);
        bill.setUpdatedBy(currentUser);
        bill.setLastModified(java.time.Instant.now());
        return repo.save(bill);
    }

    @Transactional
    public Bill close(UUID billId, String currentUser) {
        Bill bill = get(billId);
        if (!bill.getIsOpen()) {
            throw new BusinessRuleException("Fatura zaten kapalı: " + bill.getBillDocumentNo());
        }
        bill.setIsOpen(false);
        bill.setUpdatedBy(currentUser);
        bill.setLastModified(java.time.Instant.now());
        return repo.save(bill);
    }

    @Transactional(readOnly = true)
    public Bill get(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Fatura bulunamadı: " + id));
    }

    @Transactional(readOnly = true)
    public List<Bill> listByCurrentCard(UUID companyId, UUID currentCardId) {
        return repo.findByCompanyIdAndCurrentCardsId(companyId, currentCardId);
    }

    @Transactional(readOnly = true)
    public List<Bill> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }
}
