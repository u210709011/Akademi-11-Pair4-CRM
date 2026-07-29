# CRM Lite — Orta Katman (API) Test Takımı

CRM Lite gereksinim dokümanındaki (`FR-001 … FR-018`) fonksiyonel gereksinimlerin **orta katman** karşılıklarını doğrulayan, kod değiştikçe tekrar tekrar koşulabilen Postman/Newman regresyon takımı.

| | |
|---|---|
| **Hedef** | API Gateway — `http://localhost:8080` (front-end'in kullandığı yol) |
| **Kapsam** | 14 klasör, ~87 istek, ~230 assertion |
| **Kimlik doğrulama** | Keycloak `crm` realm, gerçek JWT, otomatik token yönetimi |
| **Test verisi** | Her koşum kendi verisini üretir ve sonunda temizler |
| **Raporlama** | Postman Runner / Newman (CLI + HTML + JUnit) |

---

## 1. Klasör yapısı

```
analiz-test/postman/
├── CRM-Lite.postman_collection.json     # Koleksiyon (tek dosya)
├── environments/
│   ├── CRM-Lite.local.postman_environment.json   # Lokal geliştirme
│   └── CRM-Lite.ci.postman_environment.json      # Newman / CI
├── newman/
│   ├── package.json                     # npm run test:local, test:strict, ...
│   └── reports/                         # (otomatik oluşur, git'e girmez)
├── docs/
│   └── traceability-matrix.md           # FR/ACC → test eşlemesi ve boşluk listesi
└── README.md
```

## 2. Ön koşullar

1. **Altyapı** ayakta olmalı:
   ```bash
   cd back-end/infra
   podman compose -f compose.yml up -d      # PostgreSQL, Kafka, Keycloak, Redis
   ```
2. **Servisler** şu sırayla başlatılmalı (her biri kendi klasöründe `./mvnw spring-boot:run`):
   `config-server` → `discovery-server` → `api-gateway` → `lookup-service` → `party-service` → `contact-info-service` → `customer-service` → `product-service` → `order-service`
3. **Kullanıcı**: Keycloak realm import'u `salesperson / password` kullanıcısını `CRM_AGENT` rolüyle oluşturur. Farklı bir kullanıcı kullanacaksanız environment'taki `username` / `password` değerlerini değiştirin.

## 3. Postman ile koşum

1. Postman → **Import** → `CRM-Lite.postman_collection.json` ve `environments/CRM-Lite.local.postman_environment.json`.
2. Sağ üstten **CRM Lite — Local (dev)** environment'ını seçin.
3. Önce sadece **`00 - Smoke`** klasörünü çalıştırın. Bu klasör kırmızıysa altyapı/servis sorunu vardır; diğer klasörleri koşmak anlamsızdır.
4. Ardından koleksiyonun tamamını **Run collection** ile çalıştırın. Klasör sırası bilinçlidir (veri üreten klasörler önce, temizlik en sonda) — **sırayı değiştirmeyin**.

## 4. Newman ile koşum (önerilen)

```bash
cd analiz-test/postman/newman
npm install            # bir kerelik

npm run test:smoke     # sadece ön koşul kontrolü (~5 sn)
npm run test:local     # tam koşum + HTML/JUnit rapor
```

Rapor: `newman/reports/crm-lite-report.html` (klasör ağacı FR bazlıdır, her assertion FR/ACC numarası taşır).

| Komut | Ne yapar |
|---|---|
| `npm run test:smoke` | Yalnızca `00 - Smoke` klasörü |
| `npm run test:local` | Tam koşum, HTML + JUnit rapor |
| `npm run test:strict` | `strictMode=true` — bilinen boşluklar da gerçek hata sayılır |
| `npm run test:slow` | Token süre dolumu testi dahil (~35 sn uzar) |
| `npm run test:keep-data` | Oluşan test kayıtlarını silmeden bırakır (manuel inceleme için) |
| `npm run test:ci` | CI ortamı; `BASE_URL` ve `CRM_TEST_PASSWORD` ortam değişkenlerini kullanır |

## 5. Takımın çalışma mantığı

### 5.1 Otomatik token yönetimi
Koleksiyon seviyesindeki pre-request script, token yoksa veya süresi dolmak üzereyse (`< 30 sn`) `/api/v1/auth/login` çağırıp token'ı saklar. Hiçbir isteğe elle token yapıştırmanız gerekmez. `/api/v1/auth/**` uçları ve `X-No-Auth: true` başlığı taşıyan istekler bu akışın dışındadır (401 testleri için).

### 5.2 Test verisi izolasyonu
- Her koşum bir `runId` üretir; müşteri adları, e-postalar ve GSM numaraları bu değerden türetilir.
- T.C. kimlik numaraları **geçerli checksum algoritmasıyla** rastgele üretilir → aynı koleksiyon sınırsız kez koşulabilir, "TC zaten kayıtlı" (409) çakışması olmaz.
- Oluşturulan tüm müşteri id'leri biriktirilir, `99 - Temizlik` klasöründe soft-delete edilir.
- Silinemeyen kayıtlar (aktif fatura hesabı olanlar — bkz. bölüm 7) koşum özetinde listelenir.

### 5.3 `strictMode` — bilinen boşlukların yönetimi
Dokümanda tanımlı olup kodda henüz karşılığı olmayan maddeler `gapTest` ile yazılmıştır:

| `strictMode` | Davranış | Ne zaman kullanılır |
|---|---|---|
| `false` (varsayılan) | Boşluk `[BEKLEYEN BOSLUK]` olarak raporlanır, koşum kırılmaz | Geliştirme devam ederken |
| `true` | Boşluk gerçek hata sayılır | İlgili gereksinimler tamamlandığında |

Bir boşluk kapatıldığında test kendiliğinden `[GAP KAPANDI]` etiketiyle yeşile döner — yani ekip bir gereksinimi tamamladığında bunu ayrıca haber vermenize gerek kalmaz, rapor söyler.

### 5.4 Ortak assertion katmanı
Her istekten sonra otomatik çalışır: 5xx dönmedi, yanıt süresi eşiğin altında, gövde geçerli JSON, hata gövdesi standart kontrata uygun (`status`/`error`/`message`/`path`). Tekil testler sadece kendi ACC maddesine odaklanır.

### 5.5 Validasyon matrisleri
Dokümandaki validasyon tablolarının her satırı ayrı bir assertion olarak koşar. Bunlar tek bir "matris" isteği içinde `pm.sendRequest` ile sıralı çalışır; raporda yine satır satır görünür.

> **Not:** Matris verisi bilinçli olarak ayrı CSV dosyalarında tutulmadı. Newman'ın `--iteration-data` mekanizması **koleksiyonun tamamını** her satır için tekrar koşar; bu, veri üreten/temizleyen bir takımda kabul edilemez. Matrisler istek içinde tanımlanarak tek kaynak korundu ve rapor okunabilirliği bozulmadı.

## 6. Kapsam dışı bıraktıklarım (ve nedeni)

Dokümandaki ACC maddelerinin önemli bir kısmı **saf UI davranışıdır** ve orta katmanda test edilmesi yanlış olur: buton aktif/pasif olması, göz ikonu, modal açılması, sıralama tıklaması, mesajların ekranda kırmızı gösterilmesi, dil değişiminde sayfanın yenilenmesi. Bunlar `docs/traceability-matrix.md` içinde **🖥 UI** olarak işaretlendi; kapsam yanılsaması oluşmasın diye API testine dönüştürülmedi.

## 7. Koşumdan çıkacak bilinen bulgular

Aşağıdakiler takım ilk koşumda **doğrudan raporlayacaktır** — hata değil, dokümanla kod arasındaki gerçek farklardır:

| Konu | Durum |
|---|---|
| FR-013 / FR-014 (katalog, kampanya, sepet) | API karşılığı yok; `product_relation` REQ/EXCL kuralları orta katmanda uygulanmamış |
| FR-003 ACC-006/007, FR-004 ACC-008/009 (KPS) | `FakeIdentityVerificationServiceImpl` her kimliği doğruluyor → negatif senaryo test edilemiyor |
| FR-011 ACC-004/005 | Hesabı pasifleştiren uç yok → "pasif hesap silinir" mutlu yolu doğrulanamıyor; ürün guard'ı kodda `TODO` |
| FR-007 ACC-004 | Pasif hesaba bağlı ürün kontrolü uygulanmamış |
| FR-002 ACC-007 | Doküman "ilk 10 kayıt" diyor, API varsayılanı 50 |
| FR-002 ACC-008 | `sort` parametresi uygulanmıyor (`PageRequest.of(page, size)`) |
| FR-002 (validasyon tablosu) | `Order Number` arama kriteri API'de yok |
| Ad/soyad/street uzunlukları | Doküman max 50 / 200 diyor, DTO'larda `@Size` yok |
| Home Phone | Doküman FR-003'te "2 ile başlar", FR-006'da "10–11 hane"; kod yalnızca `^[0-9]{10,11}$` |
| FR-007 ↔ FR-011 kilitlenmesi | Fatura hesabı pasifleştirilemediği için silinemiyor; hesabı olan müşteri de silinemiyor → bu müşteriler sistemde kalıcı hale geliyor |

Detaylı gerekçeler ve ACC bazlı eşleme: **`docs/traceability-matrix.md`**.

## 8. Sorun giderme

| Belirti | Olası neden / çözüm |
|---|---|
| Tüm testler 401 | Keycloak ayakta değil ya da `username`/`password` yanlış. `npm run test:smoke` ile doğrulayın. |
| Smoke'ta health 200 dönüyor ama arama 404 | Gateway route tanımları harici config repo'sundan gelir (`CONFIG_Etiya-11-Akademi-CRM`). Config Server'ın o repo'ya erişebildiğini kontrol edin. |
| `cityId` / `genderId` boş uyarısı | `lookup-service` ayakta değil. Testler `defaultCityId`/`defaultGenderId` ile devam eder, adres/gender doğrulaması yanıltıcı olabilir. |
| Onboarding 502 dönüyor | Saga adımlarından biri (party / contact-info) ayakta değil demektir. |
| Koşum sonunda "silinemeyen müşteri" uyarısı | Beklenen davranış — aktif fatura hesabı olan müşteri silinemez (bölüm 7'deki kilitlenme). |
| Newman `ECONNREFUSED` | `baseUrl` yanlış ya da gateway 8080'de değil. |
| Hesap kilitlendi, her şey 401 | `lockoutTestEnabled=true` ile koşulmuş olabilir; Keycloak 15 dakika sonra açar veya admin konsolundan kullanıcı unlock edilir. |

## 9. Takımı genişletirken

- Yeni test adı **her zaman** `FR-0XX / ACC-0YY — açıklama` formatında olsun; rapor–matris eşlemesi buna dayanır.
- Yeni müşteri oluşturan her istek, test script'inde `trackId('createdCustomerIds', b.custId)` çağırmalıdır; aksi halde kayıt temizlenmez.
- Henüz geliştirilmemiş bir gereksinim için test yazarken `pm.test` yerine `gapTest` kullanın.
- Yardımcı fonksiyonlar (`gapTest`, `runMatrix`, `newTckn`, `trackId`, `expectStatus`, `jsonBody`) koleksiyon pre-request script'inde tanımlıdır; her test script'i ilk satırda `eval(pm.collectionVariables.get('crmHelpers'));` ile bunları yükler.
