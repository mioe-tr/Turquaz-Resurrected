package com.turquaz.einvoice.model;

/**
 * Fatura tarafı (satıcı veya alıcı). Turquaz {@code TurqCurrentCard} ve
 * {@code TurqCompany} kayıtlarından doldurulur.
 */
public class EInvoiceParty {

    private String taxNumber;   // VKN (10) veya TCKN (11)
    private String name;        // unvan / ad soyad
    private String taxOffice;   // vergi dairesi
    private String address;
    private String district;    // ilçe
    private String city;        // il
    private String country = "Türkiye";
    private String postalCode;
    private String email;
    private String phone;

    public EInvoiceParty() {
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * TCKN 11 haneli; VKN 10 haneli. Alıcı bireysel mi (e-Arşiv için tipik)
     * yoksa kurumsal mı ayrımında kullanılır.
     */
    public boolean isIndividual() {
        return taxNumber != null && taxNumber.trim().length() == 11;
    }
}
