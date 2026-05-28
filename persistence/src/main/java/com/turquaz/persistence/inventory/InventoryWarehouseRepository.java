/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryWarehouseRepository extends JpaRepository<InventoryWarehouse, UUID> {

    Optional<InventoryWarehouse> findByCompanyIdAndWarehousesCode(UUID companyId, String code);

    boolean existsByCompanyIdAndWarehousesCode(UUID companyId, String code);

    List<InventoryWarehouse> findByCompanyId(UUID companyId);
}
