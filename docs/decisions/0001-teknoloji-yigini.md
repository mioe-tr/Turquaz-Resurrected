# ADR 0001 — Teknoloji Yığını ve Modül Yapısı

- **Durum:** Kabul edildi (Faz 0)
- **Tarih:** 2026-05-27

## Bağlam

Eski Turquaz; Java 1.6, Eclipse RCP, SWT 3.0, Hibernate 3 (XML mapping),
HSQLDB/PostgreSQL 8 ve EJB tabanlı istemci-sunucu mimarisi kullanıyordu.
"B Planı — Hibrit Port" gereği iş mantığını koruyup modern bir yığına taşıyoruz.

## Karar

- **Java 21 LTS** (Eclipse Temurin).
- **Spring Boot 3.3** (REST, DI, autoconfig).
- **Hibernate 6 / Jakarta Persistence 3** + Spring Data JPA; XML mapping yerine
  annotation tabanlı entity'ler.
- **PostgreSQL 16** tek hedef veritabanı (HSQLDB desteği bırakıldı).
- **Flyway** ile sürümlü şema migration'ları.
- **Maven çok modüllü** yapı: `core`, `persistence`, `api`.
- **springdoc-openapi** ile otomatik API dokümanı.
- **GPLv3** lisans (eski GPLv2 ile ileri-uyumlu).

## Modül Sınırları

`core` çerçeveden olabildiğince bağımsız (saf domain + iş kuralları), `persistence`
JPA/Flyway, `api` ise web/REST katmanı. Bağımlılık yönü tek yönlü:
`api → persistence → core`.

## Sonuçlar

- SWT/JFace, Eclipse RCP, cglib2, eski commons-* ve HSQLDB atıldı.
- Eski `.classpath`/`.project` tabanlı yapı yerine Maven.
- Web istemcisi (React) ayrı bir fazda, ayrı bir derleme zinciriyle gelecek.
