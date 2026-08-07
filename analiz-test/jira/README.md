# Jira / Xray Test Case Import

FR-001…FR-011 kapsamındaki **245 test senaryosu**, Xray Test Case Importer ile Jira'ya aktarılmak üzere CSV formatında hazırlanmıştır.

| Dosya | Amaç |
|---|---|
| `CRM-Lite-FR001-FR011-xray.csv` | **Xray'e yüklenecek dosya.** UTF-8 (BOM'suz), virgül ayırıcı |
| `CRM-Lite-FR001-FR011-excel.csv` | Sadece gözle kontrol için. UTF-8 BOM'lu, noktalı virgül ayırıcı — Excel'de çift tıklayınca düzgün açılır |

> Bu dosyalar elle yazılmadı; `postman/CRM-Lite-FR001-FR011.postman_collection.json` koleksiyonundan programatik olarak üretildi. Koleksiyon değiştiğinde yeniden üretilmelidir ki iki kaynak birbirinden ayrışmasın.

## İçerik

- **245 satır = 245 test** (her senaryo tek adımlı, `TCID` benzersiz)
- Setup (`S01…S17`) ve teardown (`Z01, Z02`) istekleri **dahil değildir** — bunlar test senaryosu değil, ön koşul/temizlik adımlarıdır. İlgili ön koşul bilgisi her satırın `Description` alanına yazılmıştır.

| FR | Test sayısı |
|---|---|
| FR-001 Sistem Girişi | 18 |
| FR-002 Müşteri Arama ve Görüntüleme | 28 |
| FR-003 Müşteri Oluşturma | 41 |
| FR-004 Müşteri Bilgilerini Güncelleme | 18 |
| FR-005 Adres Yönetimi | 20 |
| FR-006 İletişim Bilgileri Yönetimi | 27 |
| FR-007 Müşteri Silme | 14 |
| FR-008 Fatura Hesabı Oluşturma | 25 |
| FR-009 Hesap ve Bağlı Ürün Görüntüleme | 16 |
| FR-010 Fatura Hesabı Güncelleme | 18 |
| FR-011 Fatura Hesabı Silme | 20 |

**Test tipi dağılımı:** 78 pozitif · 92 negatif · 28 negatif (doküman kuralı) · 26 sınır değer (BVA) · 21 güvenlik

## Import adımları (Xray Test Case Importer)

1. Jira → Xray → **Test Case Importer**
2. **Choose File** → `CRM-Lite-FR001-FR011-xray.csv`
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
| `Test Repository Path` | Test Repository Path | Testleri FR bazlı klasörler. **Klasörlerin Jira'da önceden var olması gerekir** — yoksa "Test Repository folder not found" hatası alırsınız; o durumda bu sütunu eşleştirmeden geçin |
| `Test Tipi` | *(eşleştirmeyin)* | Bilgi amaçlı — Labels ile aynı bilgiyi taşır |
| `Son Kosum` | *(eşleştirmeyin)* | Son koşumun sonucu, referans için |
| `Iliskili Bulgu` | *(eşleştirmeyin)* | B-XX bulgu numarası, Description'da da geçiyor |

Son üç sütunu eşleştirmek zorunda değilsiniz; import sırasında "ignore" seçebilirsiniz.

## Etiketler (Labels)

| Etiket | Anlamı |
|---|---|
| `FR-001` … `FR-011` | Hangi fonksiyonel gereksinime ait |
| `otomasyon`, `postman`, `api-testi` | Otomatik koşan API testi |
| `pozitif` / `negatif` / `bva` / `guvenlik` | Test tipi |
| `bekleyen-boslik` | Dokümanda tanımlı ama API'de karşılığı yok |
| `bulgu-b01` … `bulgu-b14` | Gerçek bir kusuru ortaya çıkaran test |

## Bulguya bağlı testler

Aşağıdaki testler, izlenebilirlik matrisinde kayıtlı gerçek kusurları tespit etmektedir (`Priority: High`):

| Test | Bulgu | Kusur | Durum |
|---|---|---|---|
| `TC-001-04`, `TC-001-05`, `TC-001-10`, `TC-001-18` | **B-01** | Hatalı giriş 401 yerine 500 dönüyor | ✅ Kapandı (sprint-5) |
| `TC-004-06` | **B-03** | T.C. kimlik no çakışmasında 409 yerine 500 | ✅ Kapandı (sprint-5) |
| `TC-003-29` | **B-06** | Hesap numarası çakışması | ✅ Kapandı (sprint-5) |
| `TC-005-20` | **B-07** | Geçersiz `cityId` doğrulanmadan kabul ediliyor | 🔶 Açık |
| `TC-009-12`, `TC-009-13` | **B-10** | Geçersiz sayfalama parametresi 500 döndürüyor | 🔴 **Açık** |
| `TC-009-11`, `TC-007-10` | **B-11** | Hesap listeleme ucu olmayan müşteri için 404 yerine 200 dönüyor | 🔶 Açık |
| `TC-008-12`, `TC-010-11` | **B-12** | `Account Description` dokümanda zorunlu, API'de doğrulanmıyor | 🔶 Açık |
| `TC-008-11`, `TC-011-05`, `TC-006-10`, `TC-006-23` | **B-13** | Yanıltıcı hata mesajları | 🔶 Açık |
| `TC-008-20` | **B-14** | Fatura adresinde de `cityId` doğrulanmıyor (B-07'nin ikinci yolu) | 🔶 Açık |

Ayrıntılı kök neden analizi: `analiz-test/bulgular/BULGULAR-sprint5.md` ve `BULGULAR-sprint6.md`.

Ayrıca `TC-003-41`, gereksinim dokümanında kabul kriteri bulunmayan bir senaryoyu (pasif müşterinin T.C. kimlik numarasıyla yeni kayıt) belgelemekte ve açık analiz sorusu olarak izlenmektedir.

## Yeniden üretim

Koleksiyon güncellendiğinde bu CSV'lerin de yenilenmesi gerekir:

```bash
cd analiz-test/postman/newman
npm run test:allure            # önce koşun (Son Kosum sütunu buradan gelir)

cd ../tools
node generate-jira-csv.js
```

`Son Kosum` sütunu `newman/allure-results` klasöründeki en son koşumdan doldurulur — yani her zaman gerçek bir koşumu yansıtır.
