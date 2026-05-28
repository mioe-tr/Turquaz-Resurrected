# Turquaz Resurrected — JDK + Maven sürüm denetimi.
# Dot-source ile çağrılır:
#   . .\scripts\check-jdk.ps1
#   Assert-Jdk21
#
# JDK < 21 veya Maven yoksa anlamlı hata mesajı + çıkış.

function Get-JavaMajorVersion {
    [CmdletBinding()]
    param([string]$Command = "java")
    try {
        $raw = & $Command -version 2>&1 | Out-String
    } catch {
        return $null
    }
    # "openjdk version "21.0.5"" ya da "java version "17.0.10""
    $m = [regex]::Match($raw, 'version\s+"?(\d+)(?:\.\d+)?')
    if ($m.Success) { return [int]$m.Groups[1].Value }
    return $null
}

function Get-MavenJavaMajor {
    try {
        $raw = & mvn -version 2>&1 | Out-String
    } catch {
        return $null
    }
    $m = [regex]::Match($raw, 'Java version:\s+(\d+)')
    if ($m.Success) { return [int]$m.Groups[1].Value }
    return $null
}

function Show-JdkInstallHelp {
    Write-Host ""
    Write-Host "Eclipse Temurin JDK 21 kurmak için:" -ForegroundColor Cyan
    Write-Host "  winget install EclipseAdoptium.Temurin.21.JDK"
    Write-Host ""
    Write-Host "Veya manuel:"
    Write-Host "  https://adoptium.net/temurin/releases/?version=21"
    Write-Host ""
    Write-Host "Kurulumdan sonra yeni PowerShell aç ve `java -version` ile doğrula." -ForegroundColor Yellow
    Write-Host "Gerekirse JAVA_HOME değişkenini ayarla:" -ForegroundColor Yellow
    Write-Host '  [Environment]::SetEnvironmentVariable("JAVA_HOME",'
    Write-Host '    "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot", "User")'
}

function Assert-Jdk21 {
    [CmdletBinding()]
    param()

    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        Write-Host "✗ java bulunamadı (PATH'te değil)." -ForegroundColor Red
        Show-JdkInstallHelp
        exit 1
    }
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
        Write-Host "✗ mvn bulunamadı. Apache Maven 3.9+ kurun:" -ForegroundColor Red
        Write-Host "  winget install Apache.Maven"
        exit 1
    }

    $jv = Get-JavaMajorVersion
    $mv = Get-MavenJavaMajor
    if ($null -eq $jv -or $jv -lt 21) {
        Write-Host "✗ JDK 21 gerekli, mevcut: $jv" -ForegroundColor Red
        Show-JdkInstallHelp
        exit 1
    }
    if ($null -eq $mv -or $mv -lt 21) {
        Write-Host "✗ Maven, JDK $mv kullanıyor (21 gerekli)." -ForegroundColor Red
        Write-Host "  java -version doğru sürümü gösteriyorsa JAVA_HOME ayarlı değil olabilir." -ForegroundColor Yellow
        Show-JdkInstallHelp
        exit 1
    }
    Write-Host "✓ JDK $jv, Maven Java $mv — uygun." -ForegroundColor Green
}
