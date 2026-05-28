/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.common;

import com.turquaz.persistence.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_companies")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class Company extends AuditableEntity {

    @Column(name = "company_name", nullable = false, length = 250)
    private String companyName;

    @Column(name = "company_address", nullable = false, length = 250)
    private String companyAddress;

    @Column(name = "company_telephone", nullable = false, length = 100)
    private String companyTelephone;

    @Column(name = "company_fax", nullable = false, length = 100)
    private String companyFax;

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { this.companyName = v; }

    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String v) { this.companyAddress = v; }

    public String getCompanyTelephone() { return companyTelephone; }
    public void setCompanyTelephone(String v) { this.companyTelephone = v; }

    public String getCompanyFax() { return companyFax; }
    public void setCompanyFax(String v) { this.companyFax = v; }

}
