/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.engine;

import com.turquaz.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_engine_menu")
public class EngineMenu extends BaseEntity {

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "menu_image", length = 100)
    private String menuImage;

    @Column(name = "menu_type", nullable = false)
    private Integer menuType;

    @Column(name = "menu_module_component", nullable = false)
    private UUID menuModuleComponent;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    public String getMenuName() { return menuName; }
    public void setMenuName(String v) { this.menuName = v; }

    public String getMenuImage() { return menuImage; }
    public void setMenuImage(String v) { this.menuImage = v; }

    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer v) { this.menuType = v; }

    public UUID getMenuModuleComponent() { return menuModuleComponent; }
    public void setMenuModuleComponent(UUID v) { this.menuModuleComponent = v; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID v) { this.parentId = v; }

}
