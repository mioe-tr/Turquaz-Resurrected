FROM eclipse-temurin:8-jdk-jammy

LABEL org.opencontainers.image.title="Turquaz Standalone — Resurrected build environment"
LABEL org.opencontainers.image.description="2005-2010 Turquaz kaynak kodunu modern SWT 3.124 ile cross-platform paketleyen Docker yigini. Build env: Java 8 LTS (orijinal kodun javax.rmi.PortableRemoteObject vd. eski J2EE bagimliliklari Java 11+'da kaldirildi). Runtime: kullanici Java 17+ kuracak (modern SWT icin)."
LABEL org.opencontainers.image.source="https://github.com/mioe-tr/Turquaz-Resurrected"
LABEL org.opencontainers.image.licenses="GPL-2.0-or-later"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
        ant \
        ca-certificates \
        curl \
        python3 \
        unzip \
        zip \
 && rm -rf /var/lib/apt/lists/*

# JDK 17 yaninda — stub class'lari modern SWT 3.131 classpath'i ile derlemek
# icin (SWT class file v61, JDK 8 javac okumaz). Ana derleme hala JDK 8.
ENV JDK17_HOME=/opt/jdk17
RUN mkdir -p /opt && \
    curl -fsSL "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_linux_hotspot_17.0.13_11.tar.gz" \
    | tar -xz -C /opt && \
    mv /opt/jdk-17* "$JDK17_HOME"

COPY build.sh /usr/local/bin/turquaz-build
RUN chmod +x /usr/local/bin/turquaz-build

# Convention: source tree mounted read-only at /src, output at /out.
VOLUME ["/src", "/out"]
WORKDIR /work

ENTRYPOINT ["/usr/local/bin/turquaz-build"]
