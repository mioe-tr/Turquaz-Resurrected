package com.turquaz.engine.interfaces;

/**
 * Üst araç çubuðundaki "e-Arþiv" aksiyonunu destekleyen arama ekranlarý bu
 * arayüzü uygular (Cari Hareket Arama, Satýþ Faturasý Arama). EngUIMainFrame
 * aktif sekme bunu uyguluyorsa aksiyonu buraya yönlendirir.
 *
 * Opsiyoneldir: SearchComposite'i bozmamak için ayrý tutuldu; sadece e-belge
 * kesilebilen ekranlar uygular.
 */
public interface EInvoiceCapable {

    /** Seçili satýr için e-Arþiv kes / durum penceresini açar. */
    void issueEInvoiceForSelection();
}
