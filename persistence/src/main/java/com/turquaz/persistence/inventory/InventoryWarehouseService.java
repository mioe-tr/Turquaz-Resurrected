/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.core.BusinessRuleException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Depo yönetim servisi. Eski {@code InvBLWarehouseAdd/Search/Update}'in modern
 * karşılığı. Aynı şirkette aynı {@code warehouses_code} tekildir.
 */
@Service
public class InventoryWarehouseService {

    private final InventoryWarehouseRepository repo;

    public InventoryWarehouseService(InventoryWarehouseRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public InventoryWarehouse create(
            UUID companyId, String code, String name, String address,
            String city, String telephone, String description, String currentUser) {
        if (repo.existsByCompanyIdAndWarehousesCode(companyId, code)) {
            throw new BusinessRuleException(
                    "Bu depo koduyla zaten kayıtlı bir depo var: " + code);
        }
        InventoryWarehouse w = new InventoryWarehouse();
        w.setCompanyId(companyId);
        w.setWarehousesCode(code);
        w.setWarehousesName(name);
        w.setWarehousesAddress(address);
        w.setWarehousesCity(city);
        w.setWarehousesTelephone(telephone);
        w.setWarehousesDescription(description);
        w.setCreatedBy(currentUser);
        w.setUpdatedBy(currentUser);
        return repo.save(w);
    }

    @Transactional(readOnly = true)
    public InventoryWarehouse findByCode(UUID companyId, String code) {
        return repo.findByCompanyIdAndWarehousesCode(companyId, code)
                .orElseThrow(() -> new BusinessRuleException(
                        "Depo bulunamadı: " + code));
    }

    @Transactional(readOnly = true)
    public List<InventoryWarehouse> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }
}
