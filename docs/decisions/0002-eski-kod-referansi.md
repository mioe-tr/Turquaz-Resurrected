# ADR 0002 — Eski Kodun Referans Olarak Saklanması

- **Durum:** Kabul edildi (Faz 0)
- **Tarih:** 2026-05-27

## Bağlam

Eski kaynak kod, CVS arşivinden GitHub'a aktarılmış ve dosyalar RCS revizyon
formatındadır (`*,v`). Açıldığında ~669 Java dosyası / ~151.000 satır kod elde
edilir. Arşiv ayrıca derlenmiş `.jar`, Windows `.dll` ve ikili veritabanı
yedeği gibi büyük dosyalar içerir. Şartname, `legacy/` dizinini "salt-okunur
referans" olarak konumlandırıyor.

## Karar

`legacy/` dizini **sürüm kontrolüne dahil edilmez** (`.gitignore`). Geliştirme
ortamında şu komutlarla yeniden üretilir:

```bash
git clone https://github.com/GirayArtugMutlu/TURQUAZ-FINANCIAL-ACCOUNTING.git legacy/
apt-get install -y rcs
cd legacy && find . -name "*,v" -type f | while read v; do
  dir=$(dirname "$v"); base=$(basename "$v" ",v")
  (cd "$dir" && co -q -f "$base,v" 2>/dev/null) || true
done
```

Referans değeri yüksek olan **tek kalıcı yapıt**, eski PostgreSQL şemasının
çıkarılmış DDL'idir: `docs/legacy-schema.sql`. Bu dosya repoda saklanır.

## Gerekçe

- İkili dosyaları (jar/dll/backup) ve 151k satır GPLv2 kaynağı modern repoya
  koymak depoyu şişirir ve modern kod tabanını kirletir.
- Eski kod istendiğinde tek komutla yeniden üretilebilir; kaybolma riski yok.
- Şema DDL'i, Faz 1 veri modeli çalışmasının doğrudan girdisidir; bu yüzden
  repoda tutulur.

## Sonuçlar

- Eski koda başvurmak isteyen geliştiricinin yukarıdaki adımları çalıştırması
  gerekir (README'de ve bu ADR'de belgelendi).
- Şartnamedeki iskelet `legacy/` dizinini repoda gösterse de, mühendislik
  gerekçesiyle bu sapma bilinçli yapılmıştır ve onaya açıktır.
