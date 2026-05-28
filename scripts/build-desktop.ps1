# Turquaz Resurrected — Windows masaüstü istemci derleme yardımcısı.
#
# Kullanım (PowerShell):
#   .\scripts\build-desktop.ps1
#
# Gereksinim: JDK 21 + Maven 3.9+ kurulu olmalı.

$ErrorActionPreference = "Stop"

# JDK ve Maven sürümünü önce denetle — yanlış sürüm Java sessiz bir
# "release version 21 not supported" hatasına neden olur.
. (Join-Path $PSScriptRoot "check-jdk.ps1")
Assert-Jdk21

Write-Host "==> SWT masaüstü istemcisi derleniyor (Windows native)..." -ForegroundColor Cyan
mvn -pl desktop-swt -am -DskipTests package

if ($LASTEXITCODE -ne 0) {
    Write-Host "Derleme başarısız." -ForegroundColor Red
    exit 1
}

$jar = Get-ChildItem -Path "desktop-swt\target" -Filter "turquaz-desktop-swt-*.jar" |
    Where-Object { $_.Name -notlike "*original*" } |
    Select-Object -First 1

if ($null -eq $jar) {
    Write-Host "Çalıştırılabilir jar bulunamadı." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✓ Hazır:" -ForegroundColor Green
Write-Host "  $($jar.FullName)"
Write-Host ""
Write-Host "Çalıştırmak için:" -ForegroundColor Cyan
Write-Host "  .\scripts\run-desktop.ps1"
