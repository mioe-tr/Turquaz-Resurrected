/*
 * Turquaz Resurrected — GPLv3
 */
import i18n from "i18next";
import { initReactI18next } from "react-i18next";

const tr = {
  app: {
    title: "Turquaz Resurrected",
    welcome: "Hoş geldiniz",
  },
  nav: {
    dashboard: "Pano",
    currentCards: "Cari Kartlar",
    inventory: "Stok",
    accounting: "Muhasebe",
    bank: "Banka",
    cash: "Kasa",
    cheques: "Çek/Senet",
    bills: "Faturalar",
    orders: "Siparişler",
    reports: "Raporlar",
    logout: "Çıkış",
  },
  auth: {
    login: "Giriş Yap",
    username: "Kullanıcı Adı",
    password: "Parola",
    invalidCredentials: "Kullanıcı adı veya parola hatalı",
    loginButton: "Giriş",
    loggedInAs: "Giriş yapan: {{name}}",
  },
  currentCards: {
    title: "Cari Kartlar",
    newCard: "Yeni Cari Kart",
    code: "Kod",
    name: "Ad",
    taxNumber: "Vergi No",
    taxDepartment: "Vergi Dairesi",
    address: "Adres",
    creditLimit: "Kredi Limiti",
    riskLimit: "Risk Limiti",
    discountRate: "İskonto Oranı",
    discountPayment: "İskonto Tutarı",
    daysToValue: "Vade Günü",
    definition: "Açıklama",
    save: "Kaydet",
    cancel: "İptal",
    created: "Cari kart oluşturuldu",
  },
  common: {
    save: "Kaydet",
    cancel: "İptal",
    delete: "Sil",
    edit: "Düzenle",
    loading: "Yükleniyor...",
    error: "Hata",
    empty: "Henüz kayıt yok",
    required: "Zorunlu alan",
  },
};

i18n.use(initReactI18next).init({
  resources: { tr: { translation: tr } },
  lng: "tr",
  fallbackLng: "tr",
  interpolation: { escapeValue: false },
});

export default i18n;
