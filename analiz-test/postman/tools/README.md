# Üretici ve doğrulayıcı script'ler

Postman koleksiyonu **elle değil, bu script'lerle üretilir.** 264 isteği elle JSON'a yazmak hem hata kaynağıdır hem de bakımı imkânsız hale getirir; buradaki üretici, testleri okunabilir JavaScript tanımlarından derler.

> Gereksinim: Node.js (repo'da zaten Newman için kurulu).

## Script'ler

| Script | Ne yapar | Çıktı |
|---|---|---|
| `generate-collection.js` | Postman koleksiyonunu üretir | `../CRM-Lite-FR001-FR011.postman_collection.json` |
| `validate-collection.js` | Üretilen koleksiyondaki tüm script bloklarını sözdizimi açısından denetler | konsol raporu |
| `generate-jira-csv.js` | Xray Test Case Importer için CSV üretir (koleksiyondan + son Allure koşumundan) | `../../jira/*.csv` |

## Tipik akış

```bash
cd analiz-test/postman/tools

node generate-collection.js     # koleksiyonu yeniden üret
node validate-collection.js     # sözdizimi kontrolü (0 hata bekleriz)

cd ../newman && npm run demo    # gerçek servislere karşı koş + Allure raporu
```

Jira'ya test senaryosu aktaracaksanız, **önce koleksiyonu koşup** sonra CSV üretin — `Son Kosum` sütunu `newman/allure-results` klasöründeki en son koşumdan doldurulur:

```bash
cd analiz-test/postman/tools
node generate-jira-csv.js
```

## Önemli kural

**Koleksiyon JSON'u elle düzenlenmemelidir.** Bir sonraki `generate-collection.js` çalıştırmasında değişiklikler kaybolur. Test eklemek/değiştirmek için `generate-collection.js` içindeki tanımı düzenleyin.

Postman arayüzünde deneme yapmak serbesttir — ama kalıcı hale getirmek istediğiniz değişikliği üreticiye taşıyın.

## Üreticinin yapısı

`generate-collection.js` içinde:

- **Yardımcı fonksiyonlar** (`req`, `folder`, `url`) — Postman JSON şemasını üretir
- **Senaryo kısayolları** — tekrar eden test kalıplarını tek satıra indirger:

  | Kısayol | Ürettiği istek | FR |
  |---|---|---|
  | `loginCase` | `POST /auth/login` | FR-001 |
  | `onboardCase` | `POST /customers/onboarding` | FR-003 |
  | `verifyCase` | `POST /customers/onboarding/verify-identity` | FR-003 |
  | `individualCase` | `PUT /customers/{id}/individual` | FR-004 |
  | `addressCase` | `POST\|PUT /customers/{id}/addresses` | FR-005 |
  | `contactCase` | `PUT /customers/{id}/contact` | FR-006 |
  | `billingCase` | `POST\|PUT /customers/{id}/accounts` | FR-008, FR-010 |
  | `statusCase` | `PATCH /customers/{id}/accounts/{aid}/status` | FR-011 |
  | `fixture` | Setup müşterisi oluşturur (A…G) | 00 Setup |

- **`HELPERS`** — koleksiyonun içine gömülen, her test script'inin `eval` ile yüklediği ortak fonksiyonlar (`gapTest`, `expectCode`, `newTckn`, `newOnboardBody`, `newContactBody`, `newBillingBody`, `newBillingAddress` vb.)
- **Klasör tanımları** — `00 Setup`, `01…11 FR klasörleri`, `99 Temizlik`

Yeni bir test eklemek genelde tek satırdır:

```js
onboardCase('TC-003-42', 'Demografi - yeni kural -> 400',
  "b.individual.someField = 'gecersiz';", 400, 'Beklenen mesaj'),
```

Dokümanda tanımlı ama kodda karşılığı olmayan bir kural için son parametreye `{ gap: true }` ekleyin — test `[BEKLEYEN BOSLUK]` olarak raporlanır, koşumu kırmaz.
