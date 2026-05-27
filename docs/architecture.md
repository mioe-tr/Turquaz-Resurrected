# Mimari

## Genel Bakış

Turquaz Resurrected, eski istemci-sunucu (SWT istemcisi ↔ EJB/Hibernate sunucu)
mimarisini, modern bir **REST API + SPA (web istemcisi)** mimarisine taşır.
Eski iş mantığı korunur; sunum katmanı (SWT/JFace) tamamen atılır.

```
┌─────────────────┐      HTTPS / JSON      ┌──────────────────────────────┐
│  React + TS SPA  │  ───────────────────▶ │  turquaz-api (Spring Boot)    │
│  (Faz 4)         │                        │  REST controller + OpenAPI    │
└─────────────────┘                        └──────────────┬───────────────┘
                                                            │
                                          ┌─────────────────┴───────────────┐
                                          │  turquaz-core (iş kuralları)     │
                                          │  çift taraflı muhasebe, mizan    │
                                          └─────────────────┬───────────────┘
                                                            │
                                          ┌─────────────────┴───────────────┐
                                          │  turquaz-persistence (JPA)       │
                                          │  entity + repository + Flyway    │
                                          └─────────────────┬───────────────┘
                                                            │ JDBC
                                                   ┌────────┴────────┐
                                                   │  PostgreSQL 16   │
                                                   └─────────────────┘
```

## Modül Sorumlulukları

| Modül              | Sorumluluk                                                       | Eski karşılığı                          |
|--------------------|------------------------------------------------------------------|-----------------------------------------|
| `core`             | Domain modeli + iş kuralları, framework'ten bağımsız POJO/servis | `TurquazBusinessLogic`, `engine.bl.*`   |
| `persistence`      | JPA entity'leri, Spring Data repository'leri, Flyway migration'ları | `*.hbm.xml`, `engine.dal.*`          |
| `api`              | REST controller'ları, DTO'lar, OpenAPI, kimlik doğrulama         | `TurquazServer` (EJB/servlet)           |
| `legacy/` (ignore) | Salt-okunur referans                                             | Tüm eski kaynak                          |

## Katman Kuralları

- **Para her zaman `BigDecimal`** (DB'de `NUMERIC(19,4)`). `double`/`float` yasak.
- `core` modülü Spring/JPA'ya bağımlı olmamaya çalışır; saf domain mantığı test
  edilebilir kalır. Servisler `persistence` repository arayüzlerine bağlanır.
- Çift taraflı muhasebe değişmezi (her yevmiye fişinde Σborç = Σalacak) `core`
  içinde zorlanır ve birim testlerle korunur.

## Çok Kiracılılık (Multi-tenant)

Eski şema tek şirketlidir (`turq_companies` tablosu var ama satırlar şirketle
ilişkilendirilmemiştir). Modern şemada her iş tablosuna `company_id` eklenecek
(Faz 1 kararı — bkz. `schema-map.md`).

## Kimlik Anahtarları

Eski şema `integer` + tek bir `hibernate_sequence` kullanır. Modern şemada
`UUID` birincil anahtarlar tercih edilir (dağıtık üretim, tahmin edilemezlik).
Bu karar Faz 1'de ADR olarak kesinleştirilecek.

## Zaman Damgaları

Eski `creation_date`/`last_modified` alanları `date` tipindedir (saat yok).
Modern şemada `TIMESTAMPTZ` kullanılacak.
