# Turquaz — CVS'den Git'e Aktarım

Bu branch, orijinal Turquaz Financial Accounting projesinin (2007–2010, yazar
**huseyiner** — Hüseyin Erkin) CVS deposundan alınmış RCS (`,v`) dosyalarının
`git cvsimport` ile Git geçmişine dönüştürülmüş hâlidir.

Beş CVS modülü:

- `TurquazBusinessLogic/`
- `TurquazClient/`
- `TurquazCommon/`
- `TurquazServer/`
- `TurquazStandAlone/`

her biri kendi alt dizininde, orijinal commit zaman damgaları ve yazarları
korunarak burada birleştirilmiştir.

Her modülün izole geçmişi ayrı branch'lerde de durur:

- `legacy/cvs-TurquazBusinessLogic`
- `legacy/cvs-TurquazClient`
- `legacy/cvs-TurquazCommon`
- `legacy/cvs-TurquazServer`
- `legacy/cvs-TurquazStandAlone`

Modernleştirilmiş kod tabanı `main` branch'indedir.
