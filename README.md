# Turquaz Resurrected

**Turquaz Financial Accounting** yazılımının modern bir portu. Özgün Turquaz,
2003-2018 yılları arasında geliştirilen, açık kaynaklı (GPLv2) bir Türk muhasebe
yazılımıdır. Bu proje, kanıtlanmış iş kurallarını koruyarak yazılımı modern bir
Java yığınına ve web tabanlı bir istemciye taşımayı amaçlar.

## Özgün Ekibe Saygı

Turquaz'ı yaratan ve yıllarca emek veren ekibe minnettarız. Proje 2003'te
**Önsel Armağan**, **Hüseyin Ergün** ve **Murat Kumaç** tarafından başlatıldı.
Bu modernizasyon, onların ortaya koyduğu 100.000+ satırlık muhasebe motoru ve
domain bilgisi üzerine inşa edilmektedir. Özgün kaynak kodu CVS arşivinden
GitHub'a aktarılmıştır:
<https://github.com/GirayArtugMutlu/TURQUAZ-FINANCIAL-ACCOUNTING>

## Vizyon

- Türk muhasebe domain'ini (çift taraflı kayıt, mizan, KDV, e-Dönüşüm) doğru
  modelleyen, çok kiracılı (multi-tenant) modern bir muhasebe çekirdeği.
- REST API + React web istemcisi; masaüstü SWT bağımlılığı yok.
- Yüksek test kapsamı, özellikle muhasebe matematiğinde (borç = alacak).

## Teknoloji Yığını

| Katman        | Teknoloji                                            |
|---------------|------------------------------------------------------|
| Dil           | Java 21 LTS (Eclipse Temurin)                        |
| Çerçeve       | Spring Boot 3.3                                      |
| Kalıcılık     | Hibernate 6 / Jakarta Persistence 3, Spring Data JPA |
| Veritabanı    | PostgreSQL 16                                        |
| Migration     | Flyway                                               |
| Yapı (build)  | Maven (çok modüllü)                                  |
| API dokümanı  | OpenAPI / Swagger (springdoc)                        |
| Web istemcisi | React 18 + TypeScript + Vite (sonraki faz)           |

## Modül Yapısı

```
turquaz-parent (pom)
├── core         → İş kuralları / domain (eski TurquazBusinessLogic)
├── persistence  → JPA entity'leri, repository'ler, Flyway migration'ları
└── api          → Spring Boot REST API (çalıştırılabilir uygulama)
```

`legacy/` dizini, eski kaynak kodun salt-okunur referansıdır ve sürüm
kontrolüne dahil edilmez (bkz. `docs/decisions/0002-eski-kod-referansi.md`).

## Geliştirme

```bash
# Derle ve testleri çalıştır
mvn clean verify

# API'yi çalıştır (PostgreSQL gerektirir)
mvn -pl api spring-boot:run
```

Çevre değişkenleri: `TURQUAZ_DB_URL`, `TURQUAZ_DB_USER`, `TURQUAZ_DB_PASSWORD`.

## Lisans

Bu proje **GPLv3** ile lisanslanmıştır (özgün GPLv2 ile ileri-uyumlu). Tam metin
için [LICENSE](LICENSE) dosyasına bakınız.

## Yol Haritası

- [x] **Faz 0** — Hazırlık: eski kod analizi, proje iskeleti, CI.
- [ ] **Faz 1** — Veri modeli: 77 tablo → modern şema, Flyway, JPA entity'leri.
- [ ] **Faz 2** — Business logic portu (`com.turquaz.engine.bl.*`).
- [ ] **Faz 3** — REST API + OpenAPI + kimlik doğrulama.
- [ ] **Faz 4** — React web istemcisi (Cari, Stok, Fatura, Yevmiye).
- [ ] **Faz 5** — e-Fatura / e-Arşiv entegrasyonu.
