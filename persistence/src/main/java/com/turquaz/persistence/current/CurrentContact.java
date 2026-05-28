/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_current_contacts")
public class CurrentContact extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "contacts_name", nullable = false, length = 100)
    private String contactsName;

    @Column(name = "contact_address", nullable = false, length = 250)
    private String contactAddress;

    @Column(name = "contacts_phone1", nullable = false, length = 30)
    private String contactsPhone1;

    @Column(name = "contacts_phone2", nullable = false, length = 30)
    private String contactsPhone2;

    @Column(name = "contacts_fax_number", nullable = false, length = 30)
    private String contactsFaxNumber;

    @Column(name = "contacts_email", nullable = false, length = 100)
    private String contactsEmail;

    @Column(name = "contacts_web_site", length = 250)
    private String contactsWebSite;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public String getContactsName() { return contactsName; }
    public void setContactsName(String v) { this.contactsName = v; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String v) { this.contactAddress = v; }

    public String getContactsPhone1() { return contactsPhone1; }
    public void setContactsPhone1(String v) { this.contactsPhone1 = v; }

    public String getContactsPhone2() { return contactsPhone2; }
    public void setContactsPhone2(String v) { this.contactsPhone2 = v; }

    public String getContactsFaxNumber() { return contactsFaxNumber; }
    public void setContactsFaxNumber(String v) { this.contactsFaxNumber = v; }

    public String getContactsEmail() { return contactsEmail; }
    public void setContactsEmail(String v) { this.contactsEmail = v; }

    public String getContactsWebSite() { return contactsWebSite; }
    public void setContactsWebSite(String v) { this.contactsWebSite = v; }

}
