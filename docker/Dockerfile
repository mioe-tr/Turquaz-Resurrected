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
        unzip \
        zip \
 && rm -rf /var/lib/apt/lists/*

COPY build.sh /usr/local/bin/turquaz-build
RUN chmod +x /usr/local/bin/turquaz-build

# Convention: source tree mounted read-only at /src, output at /out.
VOLUME ["/src", "/out"]
WORKDIR /work

ENTRYPOINT ["/usr/local/bin/turquaz-build"]
