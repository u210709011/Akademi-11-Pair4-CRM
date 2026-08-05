# Jira / Xray Test Case Import

FR-001…FR-005 kapsamındaki **125 test senaryosu**, Xray Test Case Importer ile Jira'ya aktarılmak üzere CSV formatında hazırlanmıştır.

| Dosya | Amaç |
|---|---|
| `CRM-Lite-FR001-FR005-xray.csv` | **Xray'e yüklenecek dosya.** UTF-8 (BOM'suz), virgül ayırıcı |
| `CRM-Lite-FR001-FR005-excel.csv` | Sadece gözle kontrol için. UTF-8 BOM'lu, noktalı virgül ayırıcı — Excel'de çift tıklayınca düzgün açılır |

> Bu dosyalar elle yazılmadı; `postman/CRM-Lite-FR001-FR005.postman_collection.json` koleksiyonundan programatik olarak üretildi. Koleksiyon değiştiğinde yeniden üretilmelidir ki iki kaynak birbirinden ayrışmasın.

## İçerik

- **125 satır = 125 test** (her senaryo tek adımlı, `TCID` benzersiz)
- Setup (`S01…S11`) ve teardown (`Z01, Z02`) istekleri **dahil değildir** — bunlar test senaryosu değil, ön koşul/temizlik adımlarıdır. İlgili ön koşul bilgisi her satırın `Description` alanına yazılmıştır.

| FR | Test sayısı |
|---|---|
| FR-001 Sistem Girişi | 18 |
| FR-002 Müşteri Arama ve Görüntüleme | 28 |
| FR-003 Müşteri Oluşturma | 41 |
| FR-004 Müşteri Bilgilerini Güncelleme | 18 |
| FR-005 Adres Yönetimi | 20 |

**Test tipi dağılımı:** 38 pozitif · 47 negatif · 15 negatif (doküman kuralı) · 14 sınır değer (BVA) · 11 güvenlik

## Import adımları (Xray Test Case Importer)

1. Jira → Xray → **Test Case Importer**
2. **Choose File** → `CRM-Lite-FR001-FR005-xray.csv`
3. File encoding: **UTF-8** · CSV Delimiter: **,** (virgül)
4. **Next** → Setup ekranında hedef proje: **EAT**
5. **Map fields** ekranında sütunları eşleştirin (aşağıdaki tabloya bakın)

### Alan eşleştirme

| CSV sütunu | Xray/Jira alanı | Not |
|---|---|---|
| `TCID` | Test Case Identifier | Satırları teste gruplayan anahtar |
| `Summary` | Summary | Postman'daki test adıyla birebir aynı — izlenebilirlik için |
| `Description` | Description | Gereksinim referansı, ön koşul, otomasyon konumu |
| `Action` | Action (adım) | Gönderilen HTTP isteği |
| `Data` | Data (adım) | İstek gövdesi / parametre |
| `Result` | Expected Result (adım) | Beklenen HTTP kodu ve mesaj |
| `Test Type` | Test Type | Hepsi `Manual` |
| `Priority` | Priority | High / Medium / Low |
| `Labels` | Labels | Boşlukla ayrılmış |
| `Component` | Component | Jira projesinde tanımlı değilse eşleştirmeyin |
| `Test Repository Path` | Test Repository Path | Testleri FR bazlı klasörler. Xray sürümünüz desteklemiyorsa atlayın |
| `Test Tipi` | *(eşleştirmeyin)* | Bilgi amaçlı — Labels ile aynı bilgiyi taşır |
| `Son Kosum` | *(eşleştirmeyin)* | 31.07.2026 koşumunun sonucu, referans için |
| `Iliskili Bulgu` | *(eşleştirmeyin)* | B-XX bulgu numarası, Description'da da geçiyor |

Son üç sütunu eşleştirmek zorunda değilsiniz; import sırasında "ignore" seçebilirsiniz.

## Etiketler (Labels)

| Etiket | Anlamı |
|---|---|
| `FR-001` … `FR-005` | Hangi fonksiyonel gereksinime ait |
| `otomasyon`, `postman`, `api-testi` | Otomatik koşan API testi |
| `pozitif` / `negatif` / `bva` / `guvenlik` | Test tipi |
| `bekleyen-boslik` | Dokümanda tanımlı ama API'de karşılığı yok |
| `bulgu-b01`, `bulgu-b03`, `bulgu-b06`, `bulgu-b07` | Gerçek bir kusuru ortaya çıkaran test |

## Bulguya bağlı testler

Aşağıdaki testler, izlenebilirlik matrisinde kayıtlı gerçek kusurları tespit etmektedir (`Priority: High`):

| Test | Bulgu | Kusur |
|---|---|---|
| `TC-001-04`, `TC-001-05`, `TC-001-10`, `TC-001-18` | **B-01** | Hatalı giriş 401 yerine 500 dönüyor |
| `TC-004-06` | **B-03** | T.C. kimlik no çakışmasında 409 yerine 500 |
| `TC-003-29` | **B-06** | Hesap numarası çakışması (aralıklı) |
| `TC-005-20` | **B-07** | Geçersiz `cityId` doğrulanmadan kabul ediliyor |

Ayrıca `TC-003-41`, gereksinim dokümanında kabul kriteri bulunmayan bir senaryoyu (pasif müşterinin T.C. kimlik numarasıyla yeni kayıt) belgelemekte ve açık analiz sorusu olarak izlenmektedir.

## Yeniden üretim

Koleksiyon güncellendiğinde bu CSV'lerin de yenilenmesi gerekir. Üretim, koleksiyon JSON'unu ve `newman/allure-results` klasöründeki son koşum sonuçlarını okuyarak yapılır — yani `Son Kosum` sütunu her zaman en son gerçek koşumu yansıtır.
