# CRM Lite — Orta Katman (API) Test Takımı · FR-001…FR-011

CRM Lite gereksinim dokümanındaki **FR-001 … FR-011** maddelerinin orta katman karşılıklarını doğrulayan, kod değiştikçe tekrar tekrar koşulabilen Postman/Newman regresyon takımı.

| | |
|---|---|
| **Hedef** | API Gateway — `http://localhost:8080` (front-end'in kullandığı yol) |
| **Kapsam** | 13 klasör, **264 istek**, **1157 assertion** |
| **Raporlama** | Allure (sunum), HTML (htmlextra), JUnit XML (CI) |
| **Kimlik doğrulama** | Keycloak `crm` realm, gerçek JWT, otomatik token yönetimi |
| **Test verisi** | Her koşum kendi verisini üretir ve sonunda temizler |
| **Son koşum** | 05.08.2026 — 1153 geçti · 4 kaldı (tamamı B-10 kusurundan) |

---

## 1. Klasör yapısı

```
analiz-test/postman/
├── CRM-Lite-FR001-FR011.postman_collection.json   # Ana koleksiyon (elle düzenlenmez, tools/ ile üretilir)
├── environments/
│   └── CRM-Lite.local.postman_environment.json
├── newman/
│   ├── package.json          # npm run demo / test / test:allure ...
│   ├── allure-results/       # (otomatik oluşur)
│   ├── allure-report/        # (otomatik oluşur)
│   └── reports/              # HTML + JUnit (otomatik oluşur)
├── tools/
│   ├── generate-collection.js   # koleksiyonu ÜRETİR (kaynak burasıdır)
│   ├── validate-collection.js   # gömülü script bloklarını söz dizimi denetler
│   ├── generate-jira-csv.js     # Xray CSV üretir
│   └── README.md
├── docs/
│   └── traceability-matrix.md   # ACC ↔ TC eşlemesi, bulgular, boşluk listesi
├── archive/
│   └── CRM-Lite-TUM-FR.postman_collection.json   # FR-012…FR-018 (şu an bakımda değil)
└── README.md
```

## 2. Ön koşullar

1. **Altyapı**: `cd back-end/infra && docker compose up -d`
2. **Servisler** (IntelliJ'de `back-end/` klasörünü açın), şu sırayla:
   `config-server` → `discovery-server` → `api-gateway` → `lookup-service` → `party-service` → `contact-info-service` → `customer-service`

   Config Server'ın **"Started" olmasını bekleyin** — tüm servislerde `spring.config.import: configserver:...` zorunludur, fallback yoktur. Konfigürasyonlar harici repo'dan geldiği için internet erişimi de gerekir. Doğrulama: `curl http://localhost:8888/api-gateway/dev`

   > `order-service` FR-009 (hesaba bağlı ürünler) için **gereklidir**. `product-service` FR-001…FR-011 için gerekmez.
3. **Kullanıcı**: Keycloak realm import'u `salesperson / password` kullanıcısını `CRM_AGENT` rolüyle oluşturur.

## 3. Koşum

Komutlar **her zaman `newman` klasöründen** çalıştırılır:

```cmd
cd c:\...\Akademi-11-Pair4-CRM\analiz-test\postman\newman
npm install          :: sadece ilk seferde
```

> `npm run`, script'leri bulunduğunuz klasördeki `package.json`'dan okur. Yanlış klasörde çalıştırırsanız "Missing script" hatası alırsınız.

| Komut | Ne yapar |
|---|---|
| `npm run demo` | **Sunum için:** temizle → koş → Allure raporunu tarayıcıda aç |
| `npm test` | Tam koşum + HTML/JUnit rapor (`reports/`) |
| `npm run test:allure` | Tam koşum + Allure sonuçları — **hata varsa exit code 1** (CI için) |
| `npm run test:allure:demo` | Aynısı, ama exit code bastırılır — `demo` zinciri bu yüzden kesilmez |
| `npm run allure:serve` | Mevcut sonuçlardan Allure raporunu açar |
| `npm run allure:report` | Statik Allure raporu üretir (`allure-report/`) |
| `npm run test:setup` | Sadece `00 · Ortam Hazırlığı` — ortam sağlam mı (5 sn) |
| `npm run test:strict` | `strictMode=true` — bilinen boşluklar da gerçek hata sayılır |
| `npm run test:keep-data` | Oluşan test kayıtlarını silmeden bırakır |

**Sunum akışı:** tek komut yeter → `npm run demo`

Postman arayüzünden koşmak isterseniz: koleksiyonu ve environment'ı import edin, environment'ı seçin, **Run collection**. Klasör sırası bilinçlidir (00 Setup → 01…11 → 99 Temizlik), **değiştirmeyin**.

## 4. Takımın çalışma mantığı

### 4.1 Deterministik veri seti (`00 · Ortam Hazırlığı`)
Testler rastgele veriye değil, kurulan yedi fixture'a dayanır. Her fixture tek bir sorumluluk alanına ayrılmıştır; böylece bir FR'nin testleri diğerinin verisini bozmaz:

| Fixture | Rolü |
|---|---|
| **Müşteri A** | Ana test müşterisi — arama, güncelleme senaryoları |
| **Müşteri B** | A ile **aynı soyad**, farklı ad → AND/OR mantığı ve sıralama testleri |
| **Müşteri C** | Oluşturulup soft-delete edilir → "pasif müşteri aramada görünmez" |
| **Müşteri D** | FR-005 adres testleri → adres sayısı deterministik kalır |
| **Müşteri E** | FR-006 iletişim bilgisi testleri (güncellemeler kalıcı olduğu için ayrı) |
| **Müşteri F** | FR-008…FR-011 fatura hesabı testleri (aynı müşteri üzerinde uçtan uca akış) |
| **Müşteri G** | FR-007 müşteri silme testleri — koşum içinde silinir |

`cityId` ve `genderId` sabit yazılmaz, lookup servisinden **kod ile çözülür** (`CITY/ANKARA`, `GENDER/MALE`). Veritabanı sıfırlandığında bu id'ler değişir; sabit yazılsaydı her test kırılırdı.

### 4.2 Otomatik token yönetimi
Koleksiyon seviyesindeki pre-request, token yoksa veya süresi dolmak üzereyse otomatik login olur. Hiçbir isteğe elle token yapıştırmazsınız. `/api/v1/auth/**` uçları ve `X-No-Auth: true` başlıklı istekler bu akışın dışındadır (401 testleri için).

### 4.3 Test verisi izolasyonu
- Her koşum bir `runId` üretir; isimler, e-postalar ve GSM'ler bundan türetilir.
- T.C. kimlik numaraları **geçerli checksum algoritmasıyla** üretilir → koleksiyon sınırsız kez koşulabilir, 409 çakışması olmaz.
- İsimler yalnızca harf içerir (sprint-4'te eklenen `Name should contain letters only.` kuralı).
- Oluşturulan tüm müşteriler `99 · Temizlik` klasöründe soft-delete edilir.

### 4.4 `strictMode` — bilinen boşlukların yönetimi
Dokümanda tanımlı olup kodda karşılığı olmayan maddeler `gapTest` ile yazılmıştır:

| `strictMode` | Davranış |
|---|---|
| `false` (varsayılan) | `[BEKLEYEN BOSLUK]` olarak raporlanır, koşumu kırmaz |
| `true` | Gerçek hata sayılır |

Bir boşluk kapandığında test kendiliğinden `[GAP KAPANDI]` etiketiyle yeşile döner. Son koşumda 27 bekleyen boşluk, 5 kapanmış boşluk vardı. Bekleyen boşlukların bir kısmı gerçek kusurları izler (bkz. `bulgular/BULGULAR-sprint6.md` — B-11, B-12, B-13, B-14), bir kısmı ise henüz geliştirilmemiş kabul kriterlerini (FR-007/FR-011 ACC-004 ürün guard'ı, FR-009 ACC-006/007 ürün detay modalı).

Aynı mekanizma **açık analiz soruları** için de kullanılır: `TC-003-41` (pasif müşterinin T.C. kimlik numarasıyla yeni kayıt) dokümanda kabul kriteri olmayan bir senaryodur; test fiili davranışı belgeler ve karar alınana kadar her koşumda görünür kalır.

### 4.5 Aynı anda birden fazla kısıt ihlali
Bir alan hem `@NotBlank` hem `@Pattern` ihlal ettiğinde (örn. `mobilePhone = ""`), Bean Validation hangisini önce raporlayacağını **garanti etmez**. Bu tür testlerde tek bir mesaj beklemek kırılganlık yaratır; `expectMessageAny('...', '...')` kullanın.

### 4.6 Ortak assertion katmanı
Her istekten sonra otomatik: 5xx dönmedi, yanıt süresi eşiğin altında, gövde geçerli JSON. Tekil testler yalnızca kendi ACC maddesine odaklanır.

### 4.7 Nihai tutarlılık (eventual consistency)
Arama görünümü (`CUSTOMER_SEARCH_VIEW`) Kafka olayı ile **asenkron** güncellenir. `TC-004-04` (güncelleme sonrası) ve `TC-007-09` (silme sonrası) bu yüzden tek atışlık okumaz; güncel değer görünene kadar kısa aralıklarla yeniden okur. Benzer testler yazarken bunu unutmayın.

## 5. Test adlandırma standardı

```
TC-002-05 · GSM - +90'li format (URL-encoded) ile arama          [ACC-004]
└─┬─┘ └┬┘   └──────────────┬──────────────┘                      └───┬───┘
  │    │                   │                                         │
  │    │                   senaryo                        izlenebilirlik (ACC)
  │    sıra no
  FR numarası
```

Kural: **isimlerde `/`, `—`, `→` kullanmayın.** Allure bu karakterleri bozuyor veya ismi kesiyor (`/` yol ayracı sayılıyor). Onun yerine `-` ve `->` kullanın.

## 6. Kapsam dışı bıraktıklarımız

Dokümandaki ACC maddelerinin bir kısmı **saf UI davranışıdır** ve orta katmanda test edilemez: buton aktif/pasif, göz ikonu, mesaj rengi, ekran geçişi, sıralama tıklaması. Bunlar `docs/traceability-matrix.md` içinde **🖥 UI** olarak işaretlendi — kapsam yanılsaması oluşmasın diye API testine dönüştürülmedi.

## 7. Sorun giderme

| Belirti | Çözüm |
|---|---|
| `npm error Missing script` | Yanlış klasördesiniz. `newman` klasörüne `cd` yapın. |
| Tüm testler 401 | Keycloak ayakta değil ya da kullanıcı adı/şifre yanlış. `npm run test:setup` ile doğrulayın. |
| Hesap kilitlendi (her şey 401/500) | Keycloak quick-login koruması. Kilidi açmak için: `curl -X DELETE http://localhost:8180/admin/realms/crm/attack-detection/brute-force/users -H "Authorization: Bearer <admin-token>"` (admin token: `master` realm, `admin-cli`, admin/admin) |
| Onboarding 502 veriyor | **B-06** — hesap numarası çakışması. `docker compose down -v` ile temiz başlangıç yapın (bkz. izlenebilirlik matrisi). |
| Setup'ta 404 | Gateway route'ları harici config repo'sundan gelir; Config Server'ın oraya erişebildiğini kontrol edin. |
| `cityId` / `genderId` uyarısı | `lookup-service` ayakta değil; testler yedek değerle devam eder ama sonuçlar yanıltıcı olabilir. |
| Allure raporu açılmıyor | `allure-commandline` Java gerektirir. `java -version` ile kontrol edin. |

## 8. Takımı genişletirken

⚠️ **Koleksiyon JSON'u elle düzenlenmez.** Kaynak `tools/generate-collection.js` dosyasıdır; JSON her çalıştırmada yeniden üretilir ve elle yapılan değişiklikler kaybolur. Ayrıntı: `tools/README.md`.

```bash
cd analiz-test/postman/tools
node generate-collection.js     # koleksiyonu yeniden üret
node validate-collection.js     # söz dizimi denetimi (0 hata bekleriz)
```

- Yeni test adı **her zaman** `TC-0XX-YY · açıklama -> beklenen [ACC-0ZZ]` formatında olsun.
- Müşteri oluşturan her istek test script'inde `trackId('createdCustomerIds', jsonBody().custId)` çağırmalı; aksi halde kayıt temizlenmez. Fatura hesapları için `trackId('createdAccountIds', ...)` kullanılır — `99 · Temizlik` aktif hesabı olan müşteriyi silemeyeceği için önce hesapları pasifleştirip siler, sonra müşteriyi siler.
- Henüz geliştirilmemiş bir gereksinim için `pm.test` yerine `gapTest` kullanın.
- Yardımcı fonksiyonlar (`gapTest`, `expectCode`, `expectMessage`, `newTckn`, `newGsm`, `newOnboardBody`, `newAddressBody`, `newContactBody`, `newBillingBody`, `newBillingAddress`, `repeat`, `dateOffset`, `trackId`) koleksiyon pre-request'inde tanımlıdır; her test script'i ilk satırda `eval(pm.collectionVariables.get('crmHelpers'));` ile yükler.
- Asenkron yansıyan bir veriyi doğruluyorsanız tek atışlık okumayın — `TC-004-04`'teki yeniden deneme desenini örnek alın.
