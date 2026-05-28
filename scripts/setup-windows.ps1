# Turquaz Resurrected — Windows tek seferlik kurulum.
#
# Kullanım (PowerShell, yönetici gerektirmez):
#   .\scripts\setup-windows.ps1
#
# Yapılanlar:
#   1. .env.example → .env (yoksa)
#   2. Docker Desktop'ın çalıştığını kontrol et
#   3. docker compose up --build -d
#   4. PostgreSQL hazır olunca seed durumunu raporla
#   5. İlk kullanıcı oluşturma için gerekli companyId'yi göster

$ErrorActionPreference = "Stop"

Write-Host "==> Turquaz Resurrected — Windows kurulum" -ForegroundColor Cyan

# 1. .env
if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "✓ .env oluşturuldu (varsayılan değerlerle)." -ForegroundColor Green
    Write-Host "  ÜRETİMDE TURQUAZ_JWT_SECRET'ı değiştirmeyi unutmayın." -ForegroundColor Yellow
}

# 2. Docker
try {
    docker info | Out-Null
} catch {
    Write-Host "Docker Desktop çalışmıyor görünüyor. Önce başlatın." -ForegroundColor Red
    exit 1
}

# 3. Compose up
Write-Host "==> Yığın ayağa kaldırılıyor (ilk seferde 3-5 dk sürebilir)..."
docker compose up --build -d
if ($LASTEXITCODE -ne 0) { exit 1 }

# 4. API sağlık denetimi (en fazla 90 saniye bekle)
Write-Host "==> API hazır olmasını bekliyor..."
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    try {
        $r = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 2
        if ($r.status -eq "UP") { $ready = $true; break }
    } catch { }
    Start-Sleep -Seconds 3
}

if (-not $ready) {
    Write-Host "API 90 sn içinde hazır olamadı. Logları kontrol edin:" -ForegroundColor Red
    Write-Host "  docker compose logs api" -ForegroundColor Yellow
    exit 1
}

# 5. companyId'yi al
$companyId = docker exec turquaz-postgres `
    psql -U turquaz -d turquaz -tAc "select id from turq_companies limit 1;" `
    2>$null
$companyId = $companyId.Trim()

Write-Host ""
Write-Host "✓ Kurulum tamamlandı!" -ForegroundColor Green
Write-Host ""
Write-Host "  Web:     http://localhost:8081"
Write-Host "  API:     http://localhost:8080"
Write-Host "  Swagger: http://localhost:8080/swagger-ui.html"
Write-Host "  DB:      localhost:5432 (turquaz/turquaz)"
Write-Host ""
Write-Host "İlk kullanıcı için (Swagger UI veya curl):" -ForegroundColor Cyan
Write-Host "  POST http://localhost:8080/auth/register"
Write-Host "  Body:"
Write-Host "  {"
Write-Host "    `"username`": `"admin`","
Write-Host "    `"password`": `"Sifre1234!`","
Write-Host "    `"realName`": `"Yönetici`","
Write-Host "    `"companyId`": `"$companyId`""
Write-Host "  }"
Write-Host ""
Write-Host "Masaüstü (SWT) istemcisi için:" -ForegroundColor Cyan
Write-Host "  .\scripts\build-desktop.ps1"
Write-Host "  .\scripts\run-desktop.ps1"
