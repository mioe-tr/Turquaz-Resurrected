FROM eclipse-temurin:11-jdk-jammy

LABEL org.opencontainers.image.title="Turquaz Standalone — Resurrected build environment"
LABEL org.opencontainers.image.description="2005–2010 Turquaz Financial Accounting kaynak kodunu modern SWT (Eclipse 3.124) ile Linux/Windows/macOS (x86_64 + aarch64) için yeniden derleyip paketleyen Docker tabanlı reprodüksiyon yığını."
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
