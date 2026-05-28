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

# Web istemcisi (Vite dev sunucusu — proxy ile API'ye bağlanır)
cd web && npm install && npm run dev

# Masaüstü SWT istemcisi (REST'e bağlanır)
mvn -pl desktop-swt -am package
java -jar desktop-swt/target/turquaz-desktop-swt-0.9.0-SNAPSHOT.jar
# (TURQUAZ_API_URL ile farklı sunucu gösterilebilir; varsayılan localhost:8080)
```

Çevre değişkenleri: `TURQUAZ_DB_URL`, `TURQUAZ_DB_USER`, `TURQUAZ_DB_PASSWORD`,
`TURQUAZ_JWT_SECRET`, `TURQUAZ_API_URL` (desktop).

## Lisans

Bu proje **GPLv3** ile lisanslanmıştır (özgün GPLv2 ile ileri-uyumlu). Tam metin
için [LICENSE](LICENSE) dosyasına bakınız.

## Yol Haritası

- [x] **Faz 0** — Hazırlık: eski kod analizi, proje iskeleti, CI.
- [x] **Faz 1** — Veri modeli: 79 tablo → modern şema, Flyway, JPA entity'leri.
- [x] **Faz 2** — Business logic portu (`com.turquaz.engine.bl.*`).
- [x] **Faz 3** — REST API + OpenAPI + JWT.
- [x] **Faz 4** — React web istemcisi (15 sayfa).
- [x] **Faz 4+** — Modern SWT masaüstü istemcisi (REST'e bağlı).
- [ ] **Faz 5** — e-Fatura / e-Arşiv entegrasyonu.
