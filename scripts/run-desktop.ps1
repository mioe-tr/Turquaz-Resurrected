# Turquaz Resurrected — Windows masaüstü istemci çalıştırma yardımcısı.
#
# Kullanım:
#   .\scripts\run-desktop.ps1                                   # localhost:8080
#   .\scripts\run-desktop.ps1 -ApiUrl https://muhasebe.firma.com

param(
    [string]$ApiUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

$jar = Get-ChildItem -Path "desktop-swt\target" -Filter "turquaz-desktop-swt-*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notlike "*original*" } |
    Select-Object -First 1

if ($null -eq $jar) {
    Write-Host "Jar bulunamadı. Önce derle:" -ForegroundColor Yellow
    Write-Host "  .\scripts\build-desktop.ps1" -ForegroundColor Yellow
    exit 1
}

Write-Host "==> Turquaz Desktop başlatılıyor (API: $ApiUrl)" -ForegroundColor Cyan
$env:TURQUAZ_API_URL = $ApiUrl
& java -jar $jar.FullName
