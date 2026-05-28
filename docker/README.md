# Docker — Yerel Çalıştırma Rehberi

Bu rehber Windows üzerinde (Mac/Linux de aynı) Turquaz Resurrected'i tek
komutla ayağa kaldırmayı anlatır.

## Gereksinimler

- **Docker Desktop** (Windows için WSL2 backend ile) — son kararlı sürüm.
- En az 4 GB RAM ayrılmış olarak Docker'a verilmiş olmalı.
- Masaüstü (SWT) istemcisini Windows'ta yerelde derlemek için ek olarak:
  **JDK 21** (Eclipse Temurin önerilir) ve **Maven 3.9+**.

## Hızlı Başlangıç (Windows PowerShell)

```powershell
# 1. Repoyu klonla
git clone https://github.com/mioe-tr/Turquaz-Resurrected.git
cd Turquaz-Resurrected

# 2. Ortam değişkenlerini ayarla (opsiyonel — varsayılanlar geliştirme için OK)
Copy-Item .env.example .env
notepad .env   # TURQUAZ_JWT_SECRET'ı değiştirmek için

# 3. Yığını ayağa kaldır (ilk çalıştırma 3-5 dk: image build)
docker compose up --build -d

# 4. Tarayıcıyı aç
start http://localhost:8081
```

## Servisler

| URL                              | Servis                                |
|----------------------------------|---------------------------------------|
| http://localhost:8081            | Web istemcisi (React)                 |
| http://localhost:8080            | REST API (proxy üzerinden web'de de)  |
| http://localhost:8080/swagger-ui.html | OpenAPI/Swagger UI               |
| http://localhost:8080/actuator/health | Sağlık denetimi                  |
| localhost:5432                   | PostgreSQL (turquaz/turquaz)          |

## İlk Kullanıcı

API çalıştığında veritabanı `Faz 1` seed verisiyle dolar (varsayılan şirket
+ hesap planı + menüler). İlk kullanıcıyı kayıt etmek için:

```powershell
# Şirket ID'sini öğren
$companyId = curl -s `
  -u turquaz:turquaz `
  "http://localhost:5432" | Out-Null   # Bu pratik değil — DB sorgusu yerine swagger kullanın
```

**Daha kolay yöntem — Swagger UI:**

1. http://localhost:8080/swagger-ui.html aç
2. (Auth gerektirmeyen endpoint olarak) `/auth/register` aç
3. companyId için: `docker exec -it turquaz-postgres psql -U turquaz -d turquaz -tAc "select id from turq_companies limit 1;"`
4. Aldığın UUID'yi register isteğine koyup gönder:
   ```json
   {
     "username": "admin",
     "password": "Sifre1234!",
     "realName": "Yönetici",
     "companyId": "<yukarıdaki UUID>"
   }
   ```
5. http://localhost:8081/login adresine git, kullanıcı adıyla gir.

## Masaüstü (SWT) İstemcisi — Windows'ta Yerel

SWT GUI uygulaması Docker'da çalışmaz (display gerekli). Windows'ta JDK 21 +
Maven kurulu varsayıldığında:

```powershell
# Bir kerelik: bağımlılıkları çek ve runnable jar üret
mvn -pl desktop-swt -am -DskipTests package

# Çalıştır (TURQUAZ_API_URL varsayılan http://localhost:8080)
java -jar desktop-swt\target\turquaz-desktop-swt-0.9.0-SNAPSHOT.jar
```

Maven, Windows üzerinde OS profilini otomatik algılar ve
`org.eclipse.swt.win32.win32.x86_64` native artifact'ını çeker. Üretim jar'ı
`target/lib/` dizinindeki bağımlılıkları manifest üzerinden referans verir.

Farklı bir sunucu için:

```powershell
$env:TURQUAZ_API_URL = "https://muhasebe.firma.com"
java -jar desktop-swt\target\turquaz-desktop-swt-0.9.0-SNAPSHOT.jar
```

## Yığını Yönetme

```powershell
# Logları izle (tüm servisler)
docker compose logs -f

# Tek servis (örn. API)
docker compose logs -f api

# Yeniden başlat
docker compose restart api

# Durdur (veritabanını koruyarak)
docker compose down

# Veritabanını da sil (tüm veri gider)
docker compose down -v

# Sadece kodu değiştirdiyseniz hızlı rebuild
docker compose up --build -d api
```

## Sorun Giderme

**Web 502 veya boş döner**
- API hazır olmayabilir; `docker compose logs api` ile kontrol edin.
- Sağlık denetimi: `curl http://localhost:8080/actuator/health` → `{"status":"UP"}`.

**Veritabanı bağlantı hatası**
- `docker compose ps` → postgres healthy mi?
- `docker exec -it turquaz-postgres pg_isready -U turquaz`

**5432, 8080, 8081 portları kullanımda**
- `docker-compose.yml`'deki port mappingleri değiştirin
  (örn. `"8082:80"`).

**Windows + WSL2 dosya izinleri**
- Repo `\\wsl$\...` yerine `C:\Users\...` altında olsun.

## Üretim İpuçları

- `TURQUAZ_JWT_SECRET`'ı en az 48 byte rastgele ile değiştirin.
- PostgreSQL volume'ünü yedek alın (`docker run --rm -v turquaz_postgres_data:/data ...`).
- TLS için nginx önüne bir reverse proxy (Caddy/Traefik) koyun.
- `api` container'ına bellek sınırı ekleyin: `mem_limit: 1g`.
