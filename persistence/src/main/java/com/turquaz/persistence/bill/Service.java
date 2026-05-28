/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_services")
public class Service extends BaseCompanyScopedEntity {

    @Column(name = "service_name", nullable = false, length = 250)
    private String serviceName;

    @Column(name = "class_name", nullable = false, length = 250)
    private String className;

    @Column(name = "method_name", nullable = false, length = 250)
    private String methodName;

    public String getServiceName() { return serviceName; }
    public void setServiceName(String v) { this.serviceName = v; }

    public String getClassName() { return className; }
    public void setClassName(String v) { this.className = v; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String v) { this.methodName = v; }

}
