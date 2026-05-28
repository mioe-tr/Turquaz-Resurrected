/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_inventory_warehouses")
public class InventoryWarehouse extends CompanyScopedEntity {

    @Column(name = "warehouses_name", nullable = false, length = 50)
    private String warehousesName;

    @Column(name = "warehouses_address", length = 250)
    private String warehousesAddress;

    @Column(name = "warehouses_description", length = 250)
    private String warehousesDescription;

    @Column(name = "warehouses_city", length = 25)
    private String warehousesCity;

    @Column(name = "warehouses_telephone", length = 25)
    private String warehousesTelephone;

    @Column(name = "warehouses_code", nullable = false, length = 25)
    private String warehousesCode;

    public String getWarehousesName() { return warehousesName; }
    public void setWarehousesName(String v) { this.warehousesName = v; }

    public String getWarehousesAddress() { return warehousesAddress; }
    public void setWarehousesAddress(String v) { this.warehousesAddress = v; }

    public String getWarehousesDescription() { return warehousesDescription; }
    public void setWarehousesDescription(String v) { this.warehousesDescription = v; }

    public String getWarehousesCity() { return warehousesCity; }
    public void setWarehousesCity(String v) { this.warehousesCity = v; }

    public String getWarehousesTelephone() { return warehousesTelephone; }
    public void setWarehousesTelephone(String v) { this.warehousesTelephone = v; }

    public String getWarehousesCode() { return warehousesCode; }
    public void setWarehousesCode(String v) { this.warehousesCode = v; }

}
