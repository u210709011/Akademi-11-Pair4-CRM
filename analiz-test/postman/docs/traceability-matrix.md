# İzlenebilirlik Matrisi — Gereksinim ↔ Orta Katman Testi

Kaynak doküman: `analiz-test/CRM Lite — Functional Requirements & Acceptance Criteria1 (2).docx`
Test takımı: `analiz-test/postman/CRM-Lite.postman_collection.json`
Hazırlanma tarihi: 29.07.2026 — kod referansı: `ee7a02e` (sprint-3 merge)

## Gösterim

| Simge | Anlamı |
|---|---|
| ✅ | Orta katmanda otomatik test edilir (koleksiyonda karşılığı var) |
| 🖥 | Saf UI davranışı — orta katman kapsamı dışı, bilinçli olarak test edilmedi |
| ⚠ | Dokümanda tanımlı, API'de karşılığı yok/eksik — `gapTest` ile izleniyor |
| ℹ | Kısmen doğrulanıyor (API tarafı test edilir, kalanı UI'dadır) |

## Özet

| Kategori | Yaklaşık ACC sayısı |
|---|---|
| ✅ API ile doğrulanan | ~86 |
| 🖥 UI katmanı (kapsam dışı) | ~65 |
| ⚠ Boşluk (API karşılığı yok) | ~34 |
| **Toplam** | **~185** |

> Orta katmanda test edilebilir maddelerin (86 + 34 = 120) **%72'si** bugün karşılanıyor. Kalan %28, ağırlıklı olarak FR-013 (katalog/kampanya) ve FR-014 (sepet) gereksinimlerinden geliyor.

---

## FR-001 — Sistem Girişi

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Giriş ekranı gösterilir | 🖥 | — |
| 002 | İki alan doluysa Login aktif | 🖥 | API karşılığı: boş alan → 400 (validasyon matrisi) |
| 003 | Şifre göz ikonu | 🖥 | — |
| 004 | Bilgiler doğrulanır | ✅ | `FR-001 / ACC-004, ACC-009 — Geçerli kimlik bilgisi ile giriş` |
| 005 | Hatalı bilgide mesaj | ℹ | API: 401 + `message`. Mesajın rengi/konumu UI'dadır |
| 006 | Yazınca mesaj kaybolur | 🖥 | — |
| 007 | Hatalı girişte oturum oluşmaz | ✅ | `ACC-005, ACC-007 — Hatalı şifre ile giriş reddedilir` |
| 008 | 5 hatalı deneme → 15 dk kilit | ✅ | `ZZ` klasörü (opsiyonel). Keycloak realm: `bruteForceProtected=true, failureFactor=5, waitIncrementSeconds=900` |
| 009 | 8 saatlik oturum token'ı | ✅ | `expiresIn=28800` + JWT `exp-iat` kontrolü |
| 010 | Customer Search'e yönlendirme | 🖥 | — |
| 011 | Süre dolunca token geçersiz | ✅ | Kısa ömürlü (30 sn) client ile; `runSlowTests=true` iken |
| 012 | Logout | ✅ | `ACC-012, ACC-013 — Logout sonrası oturum sonlandırılır` |
| 013 | Logout sonrası oturum biter | ✅ | Aynı test: logout sonrası refresh 4xx |

**Giriş validasyon tablosu**

| Kural | Durum | Not |
|---|---|---|
| Kullanıcı adı zorunlu | ✅ | 400 |
| Kullanıcı adı max 50 karakter | ⚠ | `LoginRequest`'te `@Size` yok |
| Kullanıcı adı büyük/küçük harfe duyarsız | ⚠ | Keycloak davranışına bağlı, sözleşmede garanti edilmiyor |
| Kullanıcı adı baş/son boşluk yasak | ✅ | `@Pattern("\\S(.*\\S)?")` |
| Şifre zorunlu | ✅ | 400 |
| Şifre max 50 karakter | ⚠ | Kural yok |
| Şifre baş/son boşluk yasak | ⚠ | `LoginRequest.password` üzerinde pattern yok |

---

## FR-002 — Müşteri Arama ve Görüntüleme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Filtre alanlarıyla arama | ✅ | 4 ayrı kriter testi (NAT ID, Customer ID, Account No, GSM) |
| 002 | First+Last AND, diğerleri OR | ✅ | `ACC-002 — AND mantığı` ve `ACC-002 — OR mantığı` |
| 003 | En az bir filtre olmadan Search pasif | 🖥 | API kriter olmadan 200 + tüm aktif müşteriler döner (test bu davranışı belgeler) |
| 004 | Search kriterlere göre arar | ✅ | Aynı 4 test |
| 005 | Sonuçlar tablo halinde | ✅ | `content` dizisi kontrolü |
| 006 | Kolonlar: Customer ID, First/Second/Last Name, Role, NAT ID | ✅ | `ACC-006 — Sonuç kolonları` |
| 007 | İlk 10 kayıt + sayfalama | ℹ ⚠ | Sayfalama ✅; **varsayılan sayfa boyutu 50, doküman 10 diyor** → `gapTest` |
| 008 | Kolon başlığına tıklayınca sıralama | ⚠ | `PageRequest.of(page, size)` — `sort` desteklenmiyor |
| 009 | Customer ID'ye tıklayınca Customer Info | ℹ | Ekran geçişi UI; veri ucu `GET /customers/{id}` ✅ |
| 010 | Kayıt yoksa bilgilendirme | ℹ | API boş sayfa döner ✅; mesaj UI'dadır |
| 011 | Create Customer butonu | 🖥 | — |
| 012 | Clear butonu | 🖥 | — |

**Arama validasyon tablosu**

| Alan | Kural | Durum | Not |
|---|---|---|---|
| NAT ID | 11 hane, yalnızca rakam | ⚠ | Arama parametresinde format doğrulaması yok |
| Customer ID | Yalnızca rakam | ℹ | `Long` tip dönüşümü ile dolaylı |
| Account Number | Yalnızca rakam | ⚠ | `ACC-{custId}` formatında string olarak aranıyor |
| GSM Number | 10 hane, 5 ile başlar | ⚠ | Arama parametresinde kural yok |
| First/Last Name | Metin, max 50 | ⚠ | Kural yok |
| Order Number | Yalnızca rakam | ⚠ | **Arama parametresi hiç yok** |

---

## FR-003 — Müşteri Oluşturma

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Create Customer → demografik ekran | 🖥 | — |
| 002 | Zorunlu alanlar dolmadan Next pasif | ℹ | API: her zorunlu alan için 400 (validasyon matrisi) |
| 003 | Previous ile geri dönüş | 🖥 | — |
| 004 | Önce Nationality ID tekillik kontrolü | ✅ | `ACC-004, ACC-006 — verify-identity` |
| 005 | Aynı Nationality ID varsa ilerlenemez | ✅ | 409 + `A customer with this national ID already exists.` |
| 006 | KPS ile doğrulama | ⚠ | `FakeIdentityVerificationServiceImpl` — her kimliği doğruluyor |
| 007 | KPS başarısızsa ilerlenemez | ⚠ | Negatif senaryo üretilemiyor (11 numaralı klasörde izleniyor) |
| 008 | Adres adımına geçiş | 🖥 | — |
| 009 | Bir veya birden fazla adres | ✅ | Onboarding 1–5 adres kabul ediyor |
| 010 | En fazla 5 adres | ✅ | 6 adres → 400 `You can add up to 5 addresses.` |
| 011 | En az bir adres zorunlu | ✅ | 0 adres → 400 `At least one address is required.` |
| 012 | Kontakt adımına geçiş | 🖥 | — |
| 013 | Email, Mobile, Home Phone, Fax girilir | ✅ | Onboarding gövdesinde dört alan da kabul ediliyor |
| 014 | Geçerli format olmadan Create pasif | ℹ | API: her format kuralı için 400 |
| 015 | Create → müşteri kaydı | ✅ | 201 + `custId` |
| 016 | Başarı sonrası Customer Info | ℹ | Ekran UI; veri ucu ✅ |
| 017 | Varsayılan 223 tipi hesap | ✅ | `accounts[0].accountTpId === 223` |

**FR-003 validasyon tablosu**

| Alan | Kural | Durum |
|---|---|---|
| First Name | Zorunlu | ✅ |
| First Name / Middle / Last / Father / Mother | Max 50 karakter | ⚠ (DTO'da `@Size` yok) |
| Last Name | Zorunlu | ✅ |
| Birth Date | DD/MM/YYYY, gelecek ve <01/01/1900 yasak | ✅ (sınır değer 01/01/1900 dahil test edilir) |
| Gender | Zorunlu | ✅ |
| Nationality ID | 11 hane rakam | ✅ |
| City / Street / House No / Address Desc | Zorunlu | ✅ |
| Street | Max 200 karakter | ⚠ |
| E-mail | Geçerli format | ✅ |
| Mobile Phone | 10 hane, 5 ile başlar | ✅ |
| Home Phone | 10 hane, 2 ile başlar | ⚠ (kod: `^[0-9]{10,11}$`, "2 ile başlar" kuralı yok) |
| Fax | Geçerli faks formatı | ℹ (kod: `^[0-9]{10,11}$`) |

---

## FR-004 — Müşteri Bilgilerini Güncelleme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Kalem ikonu → update ekranı | 🖥 | — |
| 002 | Mevcut bilgiler dolu gelir | ✅ | `ACC-002 — Mevcut müşteri bilgileri dolu gelir` (8 alan + tarih formatı) |
| 003 | Alanlar güncellenebilir | ✅ | `ACC-003, ACC-010 — Demografik bilgiler güncellenir` |
| 004 | Zorunlu alan boşsa Save pasif | ℹ | API: 400 (validasyon matrisi) |
| 005 | Cancel ile kayıtsız dönüş | 🖥 | — |
| 006 | Save'de tekillik kontrolü | ✅ | Çakışma testi |
| 007 | Çakışmada uyarı | ✅ | 409 + mesaj |
| 008 | KPS doğrulaması | ⚠ | Fake |
| 009 | KPS başarısızsa kaydedilmez | ⚠ | Fake |
| 010 | Bilgiler kaydedilir | ✅ | 200 + yanıtta güncel değerler |
| 011 | Customer Info güncel görünür | ✅ | Tekrar GET ile kalıcılık kontrolü |

**Validation table (İngilizce):** zorunlu alan / tarih formatı / gelecek tarih / 1900 öncesi / gender / 11 hane NAT ID kurallarının tamamı ✅. `max 50 characters` kuralları ⚠ (kodda `@Size` yok).

---

## FR-005 — Adres Yönetimi

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Address tabında adresler listelenir | ✅ | `ACC-001, ACC-007` |
| 002 | Add New Address | ✅ | 201 + alan doğrulaması |
| 003 | Zorunlu alan yoksa Save pasif | ℹ | API: 400 (adres validasyon matrisi) |
| 004 | Save → kaydedilir, listeye eklenir | ✅ | 201 |
| 005 | En fazla 5 adres | ✅ | Limit doldurulur, 6. adres → 409 |
| 006 | Birincil adres seçilebilir | ✅ | `primary=true` sonrası tek birincil kuralı doğrulanır |
| 007 | Tek adres otomatik birincil, değiştirilemez | ℹ ⚠ | Otomatik birincil ✅; "değiştirilemez" kısıtı API'de zorlanmıyor |
| 008 | Delete ile silme | ✅ | 204 + listede yok |
| 009 | Birincil adres silinemez | ✅ | 409 `Primary address cannot be deleted.` |
| 010 | Onay + faturaya bağlılık kontrolü | ℹ | Onay UI; fatura kontrolü ✅ |
| 011 | Faturaya bağlı adres silinemez | ✅ | 409 `Please change the related billing address in customer account.` |
| 012 | Faturasız, birincil olmayan adres silinir | ✅ | 204 |
| 013 | Edit ile güncelleme | ✅ | 200 |
| 014 | Güncellemede zorunlu alanlar | ℹ | API: 400 |
| 015 | Save sonrası mesaj + dönüş | ℹ | API 200 ✅; mesaj UI |

Ek olarak: **IDOR koruması** — başka müşterinin adresi 404 döner ✅ (dokümanda yok, orta katman için zorunlu).

---

## FR-006 — İletişim Bilgileri Yönetimi

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Contact Medium tabında bilgiler görünür | ✅ | 200 + email/mobilePhone dolu |
| 002 | Kalem ikonu → update ekranı | 🖥 | — |
| 003 | Bilgiler dolu gelir | ✅ | Kontrat kontrolü (email, mobilePhone, homePhone, fax) |
| 004 | Alanlar güncellenebilir | ✅ | 200 + yanıtta güncel değerler |
| 005 | Format geçerli değilse Save pasif | ℹ | API: 9 senaryoluk validasyon matrisi |
| 006 | Cancel uyarısı | 🖥 | — |
| 007 | Save → başarı + dönüş | ✅ | Kalıcılık testi |

**Validasyon tablosu:** e-posta formatı ✅, Mobile Phone (10 hane / 5 ile başlar) ✅, Home Phone (10–11 hane) ✅, Fax ✅.
⚠ **Mesaj metni farkı:** doküman `Email must be a valid email address.` diyor, kod `Invalid email format` döndürüyor.
⚠ FR-003 tablosundaki "Home Phone 2 ile başlar" kuralı burada 10–11 hane olarak gevşetilmiş — iki tablo çelişiyor, kod ikincisini uyguluyor.

---

## FR-007 — Müşteri Silme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Silme onayı istenir | 🖥 | — |
| 002 | Fatura hesapları ve ürünler kontrol edilir | ℹ | Hesap kontrolü ✅, ürün kontrolü ⚠ |
| 003 | Aktif fatura hesabı varsa silinemez | ✅ | 409 `This customer has an active billing account...` |
| 004 | Pasif hesaba bağlı ürün varsa silinemez | ⚠ | `CustomerBusinessRules` içinde ürün guard'ı yok |
| 005 | Uygunsa soft delete | ✅ | 204 + aramada görünmez + detayda 404 |
| 006 | Silme sonrası arama ekranına dönüş | 🖥 | — |

---

## FR-008 — Yeni Fatura Hesabı Oluşturma

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Customer Account tabı açılır | 🖥 | Veri ucu FR-009'da ✅ |
| 002 | Create New Account → ekran | 🖥 | — |
| 003 | Account Name / Description alanları | ✅ | 201 + alan doğrulaması |
| 004 | Yeni adres veya mevcut adres seçimi | ✅ | Her iki yol da test edilir |
| 005 | Yeni adres alanları | ✅ | `newAddress` ile oluşturma |
| 006 | Adres ekranında Cancel uyarısı | 🖥 | — |
| 007 | Yeni adres Save → kaydedilir | ✅ | 201 + `addressId` döner |
| 008 | Seçilen adres listelenir | ✅ | Yanıttaki `addressId` kontrolü |
| 009 | Ad/Açıklama/adres olmadan Create pasif | ✅ | 400 (+ ⚠ Account Name max 50 kuralı create DTO'sunda yok) |
| 010 | Cancel uyarısı | 🖥 | — |
| 011 | Create → hesap yaratılır | ✅ | 201 |
| 012 | Hesap tipi 224 | ✅ | `accountTpId === 224` |
| 013 | Doğrulama mesajı + dönüş | ℹ | API 201 ✅ |
| 014 | Yeni hesap tabloda listelenir | ✅ | `GET /accounts` içinde bulunur |

⚠ Doküman "addressId **veya** newAddress" diyor; kod yalnızca "ikisi de boş" durumunu engelliyor, ikisi birlikte gönderilirse hata vermiyor → `gapTest`.

---

## FR-009 — Fatura Hesabı ve Bağlı Ürün Detayları

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Hesaplar listelenir / hesap yoksa mesaj | ℹ | Liste ✅; boş mesajı UI |
| 002 | Kolonlar: Status, Number, Name, Type, Action | ✅ | Kontrat kontrolü |
| 003 | Satır genişletme oku | 🖥 | — |
| 004 | Hesaba bağlı ürünler tabloda | ✅ | `GET /api/v1/orders?custAcctId=` |
| 005 | Ürün kolonları: Product ID/Name, Campaign ID/Name | ✅ | `CustOrdItemResponse` kontratı |
| 006 | Göz ikonu → ürün detay modalı | 🖥 | — |
| 007 | Modal alanları: Product Offer Name/ID, Product Spec ID, Service Start Date, Prod Chars, Service Address | ⚠ | Bu alanları döndüren bir ürün detay ucu yok |
| 008 | Modal kapatma | 🖥 | — |
| 009 | İlk 5 kayıt + sayfalama | ✅ | Varsayılan `size=5` doğrulanır |

---

## FR-010 — Billing Account Güncelleme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Edit → Update ekranı | 🖥 | — |
| 002 | Alanlar dolu gelir | ✅ | `GET /accounts` içeriği |
| 003 | Yeni/mevcut adres seçimi | ✅ | `addressId` ile güncelleme |
| 004 | Yeni adres alanları | ✅ | `newAddress` desteklenir |
| 005 | Adres ekranı Cancel | 🖥 | — |
| 006 | Yeni adres Save | ✅ | — |
| 007 | Adres listelenir | ✅ | — |
| 008 | Zorunlu alanlar olmadan Save pasif | ✅ | 400 (+ Account Name max 50 ✅ burada var) |
| 009 | Cancel uyarısı | 🖥 | — |
| 010 | Save → güncellenir | ✅ | 200 |
| 011 | Doğrulama mesajı + dönüş | ℹ | API 200 ✅ |
| 012 | Güncel bilgiler listede | ✅ | `accountNo` ve `accountTpId` değişmediği de doğrulanır |

Ek: **IDOR** — başka müşterinin hesabı 404 ✅.

---

## FR-011 — Billing Account Silme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Onay diyaloğu | 🖥 | — |
| 002 | Aktiflik ve bağlı ürün kontrolü | ℹ | Aktiflik ✅, ürün ⚠ |
| 003 | Aktif hesap silinemez | ✅ | 409 `This billing account is active and cannot be deleted.` |
| 004 | Pasif + bağlı ürün varsa silinemez | ⚠ | Kodda `TODO` (order-service bekleniyor) |
| 005 | Pasif + ürünsüz hesap silinir | ⚠ | **Hesabı pasifleştiren bir API ucu yok** → mutlu yol doğrulanamıyor |

> ⚠ **Kritik bulgu:** Yeni açılan her fatura hesabı aktiftir ve pasifleştirilemez. Dolayısıyla FR-011 ACC-005 hiç çalıştırılamaz; ayrıca FR-007 ACC-003 gereği fatura hesabı olan müşteri de silinemez. Sonuç: fatura hesabı açılmış bir müşteri sistemden hiçbir şekilde temizlenemiyor. Test takımının temizlik adımı bu kayıtları "kalıntı" olarak raporlar.

---

## FR-012 — Yeni Satış Sürecini Başlatma

| ACC | Özet | Durum |
|---|---|---|
| 001–005 | Offer Selection ekranı, Catalog/Campaign sekmeleri, boş sepet mesajı, Next butonu | 🖥 |

Bu gereksinim tamamen ekran davranışıdır; orta katman karşılığı gerektirmez.

---

## FR-013 — Katalog ve Kampanya Listeleme

| ACC | Özet | Durum | Not |
|---|---|---|---|
| 001, 002 | Varsayılan sekme, sekme durumunun korunması | 🖥 | — |
| 003–014 | Katalog/kampanya arama, kolonlar, bundled offers, "No records found", sayfalama | ⚠ | **product-service'te yalnızca `ProductSpecController` var; katalog ve kampanya arama uçları yok.** `Campaign`, `CampaignOffering`, `ProductOffering`, `ProductRelation` entity ve repository'leri mevcut ancak API'ye açılmamış |

Koleksiyonda `11` numaralı klasörde iki `gapTest` ile izlenir.

---

## FR-014 — Sepet Yönetimi

| ACC | Özet | Durum | Not |
|---|---|---|---|
| 001, 003–006, 010–014 | Sepete ekleme, REQ ürünlerin otomatik eklenmesi, silme kilidi, çakışma, "In Basket"/"Already Active" | ⚠ | Sepet uçları yok; kurallar orta katmanda uygulanmıyor |
| 002, 007–009, 015–018 | Sepet gösterimi, ürün sayısı, Total Amount, boş sepet, Cancel/Next | 🖥 | UI durumu (arkasında sepet API'si olmadığı için doğrulanamaz) |
| İK-01…İK-05 | `product_relation` REQ/EXCL çözümlemesi, aynı ürün tekrarı, kategori çakışması, aktif ürün kontrolü | ⚠ | `order-service/BasketValidationRules` yalnızca iki kural içeriyor: adres verilmiş mi, hesap müşteriye ait mi |

---

## FR-015 — Ürün Konfigürasyonu

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Next → Product Configuration | 🖥 | — |
| 002 | Her ürün için karakteristik alanları | ⚠ | Ürünün karakteristik tanımlarını dönen bir uç yok (`charVals` gönderilebiliyor ama şema sunulmuyor) |
| 003 | Tek servis adresi (yeni veya mevcut) | ✅ | `submit` içinde `addressId` / `newAddress` |
| 004 | Yeni adres alanları | ✅ | `AddressInfoRequest` (city/street/building/desc + uzunluk kuralları) |
| 005–008 | Cancel/Save/Previous ekran akışları | 🖥 | — |
| 009 | Eksik bilgiyle Next pasif | ℹ | API: 400 (sipariş validasyon matrisi) |
| 010 | Review & Submit ekranı | 🖥 | — |

---

## FR-016 — Sipariş Özeti Görüntüleme

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Review & Submit ekranı açılır | 🖥 | — |
| 002 | Order ID gösterilir | ✅* | `OrderSummaryResponse.custOrdId` |
| 003 | Order Items: Prod Offer ID + Name | ✅* | `items[].prodOfrId`, `items[].ofrName` |
| 004 | Service Address | ✅* | `serviceAddress` |
| 005 | Total Amount | ✅* | `totalAmount` |

\* Bu testler **katalog seed verisi gerektirir**. `product-service` migration'larında ürün/kampanya seed'i bulunmadığı için environment'taki `prodOfrId` boştur ve test `[ATLANDI]` olarak raporlanır. Seed eklendiğinde `prodOfrId` doldurulduğu anda mutlu yol otomatik devreye girer.

---

## FR-017 — Siparişi Tamamlama

| ACC | Özet | Durum | Test / Not |
|---|---|---|---|
| 001 | Previous ile bilgiler korunur | 🖥 | — |
| 002 | Submit onay mesajı | 🖥 | — |
| 003 | Sipariş orta katmana iletilir | ✅* | `POST /api/v1/orders/submit` |
| 004 | Başarı mesajı | ℹ | API 2xx ✅ |
| 005 | Customer Information'a yönlendirme | 🖥 | — |

Ayrıca zorunlu alan/yetki senaryoları ✅: `custId`, `custAcctId`, boş sepet, adres eksikliği, başka müşterinin hesabı.

---

## FR-018 — Dil Desteği

| ACC | Özet | Durum | Not |
|---|---|---|---|
| 001–005, 007 | Navbar dil seçeneği, varsayılan Türkçe, sayfa yenileme, kalıcılık, müşteri verisinin etkilenmemesi | 🖥 | Front-end sorumluluğu |
| 006 | **Uyarı/hata mesajları da dil değişiminden etkilenir** | ⚠ | `customer-service` yalnızca `messages.properties` (İngilizce) içeriyor; `Accept-Language: tr` gönderilse de mesajlar İngilizce dönüyor. `messages_tr.properties` eklenmeli |

---

## Dokümanda olmayan ama test edilen konular

Orta katman kalitesi için eklenen, gereksinim dokümanında karşılığı bulunmayan kontroller:

| Konu | Test |
|---|---|
| Token'sız / geçersiz token ile erişim | 401 (2 test) |
| CORS preflight ve izin verilen origin | `Access-Control-Allow-Origin` kontrolü |
| IDOR (yatay yetki aşımı) | Başka müşterinin adresi/hesabı → 404 (3 test) |
| Standart hata gövdesi kontratı | Her 4xx yanıtta `status`/`error`/`message`/`path` |
| 5xx sızıntısı | Her istekte ortak assertion |
| Yanıt süresi eşiği | Her istekte ortak assertion (`maxResponseTimeMs`) |
| Soft-delete'in aramaya yansıması | FR-007 sonrası arama kontrolü |

---

## Boşluk listesi (öncelik önerisiyle)

| # | Boşluk | Etkilenen gereksinim | Öneri |
|---|---|---|---|
| 1 | Katalog ve kampanya arama uçları yok | FR-013 (12 ACC) | Yüksek — satış akışının tamamı buna bağlı |
| 2 | Sepet uçları ve REQ/EXCL kuralları yok | FR-014 (10 ACC + 5 iş kuralı) | Yüksek |
| 3 | Fatura hesabı pasifleştirilemiyor → silinemiyor, müşteri de silinemiyor | FR-007, FR-011 | Yüksek — veri kalıcı olarak kilitleniyor |
| 4 | KPS entegrasyonu fake | FR-003, FR-004 | Orta — negatif senaryolar hiç test edilemiyor |
| 5 | Ürün detay ucu (Prod Chars, Service Start Date…) yok | FR-009 ACC-007 | Orta |
| 6 | Ürün karakteristik şeması sunulmuyor | FR-015 ACC-002 | Orta |
| 7 | Arama: `sort` ve `Order Number` desteği yok, varsayılan sayfa boyutu 50 | FR-002 | Orta |
| 8 | Metin alanlarında uzunluk kuralları (`@Size`) uygulanmıyor | FR-003, FR-004, FR-005, FR-008 | Düşük — veri kalitesi riski |
| 9 | Hata mesajları yalnızca İngilizce | FR-018 ACC-006 | Düşük |
| 10 | Home Phone kuralı iki tabloda çelişiyor | FR-003 ↔ FR-006 | Düşük — **doküman düzeltmesi** gerekiyor |
| 11 | E-posta hata mesajı metni dokümanla uyuşmuyor | FR-006 | Düşük — doküman ya da kod hizalanmalı |

> 10 ve 11 numaralı maddeler kod hatası değil **doküman hatasıdır**; analiz tarafında düzeltilmesi önerilir.
