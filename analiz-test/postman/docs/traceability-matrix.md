# İzlenebilirlik Matrisi — FR-001 … FR-011

| | |
|---|---|
| Kaynak doküman | `analiz-test/CRM Lite — Functional Requirements & Acceptance Criteria1 (2).docx` |
| Test takımı | `analiz-test/postman/CRM-Lite-FR001-FR011.postman_collection.json` |
| Kod referansı | `05c6c3f` (sprint-5 düzeltmeleri sonrası) |
| Son koşum | 05.08.2026 — **264 istek · 1157 assertion · 1153 geçti · 4 kaldı** (4 hatanın tamamı B-10'dan) |

## Gösterim

| Simge | Anlamı |
|---|---|
| ✅ | Orta katmanda test edilir ve **geçiyor** |
| ❌ | Test edilir ve **kalıyor** → gerçek kusur (bulgu numarası verilmiştir) |
| ⚠ | Dokümanda tanımlı, API'de karşılığı yok/eksik — `gapTest` ile izleniyor |
| 🖥 | Saf UI davranışı — orta katman kapsamı dışı, bilinçli olarak test edilmedi |
| ℹ | Kısmen doğrulanıyor (API tarafı test edilir, kalanı UI'dadır) |

---

# 1. Doğrulanan kusurlar

| # | Bulgu | Kanıt / Test | Şiddet |
|---|---|---|---|
| **B-01** | **Hatalı kimlik bilgisiyle giriş 401 yerine 500 dönüyor.** `AuthService`, Keycloak'ın `invalid_grant` yanıtını `InvalidCredentialsException`'a çevirmiyor. Aynı sorun `/auth/refresh` için de geçerli. | `TC-001-04`, `TC-001-05`, `TC-001-10`, `TC-001-18` | **Yüksek** — FR-001 ACC-005/007 karşılanmıyor; front-end "Wrong user name or password" mesajını üretemez, kullanıcıya sistem hatası görünür |
| **B-02** | **Gateway'in ürettiği hata gövdesinde `message` alanı yok** (`{timestamp, path, status, error, requestId}`). `shared-contracts/ErrorResponse` kontratı ihlal ediliyor. | B-01 ile aynı yanıtlar | Orta |
| **B-03** | **Nationality ID çakışmasında 409 yerine 500.** party-service'in 409'u customer-service'e ulaşırken `A dependent service call failed.` mesajıyla 500'e dönüşüyor. Aynı kural **onboarding'de doğru çalışıyor** (409) — hata yalnızca güncelleme yolunda. | `TC-004-06` | **Yüksek** — FR-004 ACC-006/007 karşılanmıyor |
| **B-06** | **Hesap numarası üretimi iki farklı ID uzayından besleniyor.** `CustomerServiceImpl`: varsayılan hesap `formatAccountNo(cust_id)`, fatura hesabı `formatAccountNo(cust_acct_id)` — ikisi de aynı `uq_cust_acct_no` kolonuna yazıyor. `cust_acct_id` her zaman daha hızlı büyüdüğü için, `cust_id` o aralığa girdiğinde onboarding kalıcı olarak kırılıyor. | Log: `duplicate key value violates unique constraint "uq_cust_acct_no" · Key (acct_no)=(000062) already exists` | **Kritik** — müşteri oluşturma belirli aralıklarda tamamen duruyor, kullanıcıya "tekrar deneyin" diye görünüyor, kendiliğinden geçtiği için tekrar üretilemiyor |

| **B-07** | **Geçersiz `cityId` doğrulanmadan kabul ediliyor.** Doküman "City: listeden seçim" diyor; API yalnızca `@NotNull` uyguluyor, değerin lookup-service'te tanımlı olup olmadığına bakmıyor. `cityId = 999999` ile adres **201 Created** dönüyor. | `TC-005-20` | Orta — veri bütünlüğü: hiçbir şehre karşılık gelmeyen adresler oluşabiliyor, UI bunları gösteremez |

> **B-06 düzeltme önerisi:** `acct_no` tek bir adanmış sequence'ten üretilmeli (`CREATE SEQUENCE acct_no_seq`) ya da tip önekiyle ayrıştırılmalı (`C000062` / `B000062`). İki ID uzayı aynı unique kolonu paylaştığı sürece çakışma kaçınılmazdır.
>
> **Geçici çözüm (sunum için):** `docker compose down -v` ile temiz veritabanı. FR-001…FR-005 hiç fatura hesabı açmadığı için bu koşumlarda çakışma tekrar oluşmaz.

**Not:** B-04 (sipariş gönderiminde HTTP 200 + gövdede `status:500`) ve B-05 (`/api/v1/product-procutOfferings` yazım hatası) FR-008+ kapsamındadır; ilgili testler `archive/CRM-Lite-TUM-FR.postman_collection.json` içindedir.

# 2. Sprint-4 ile kapanan boşluklar

Bu maddeler önceki koşumda ⚠ idi, artık ✅:

| Madde | Durum |
|---|---|
| FR-002 ACC-007 — varsayılan sayfa boyutu 10 | ✅ `TC-002-20` (önceden 50 idi) |
| FR-002 — küçük harfle arama eşleşir | ✅ `TC-002-12` |
| FR-002 — BÜYÜK harfle arama eşleşir | ✅ `TC-002-13` |
| FR-005 — adres `street` 200 karakter sınırı | ✅ `TC-005-14` (adres ucunda uygulanıyor) |
| FR-001 — kullanıcı adı büyük/küçük harfe duyarsız | ✅ `TC-001-11` |

# 3. Bekleyen boşluklar (15)

| Test | Beklenen (doküman) | Gerçek durum |
|---|---|---|
| `TC-001-10` | Kullanıcı adı max 50 karakter | `LoginRequest`'te `@Size` yok |
| `TC-002-05` | `+90` önekli GSM normalize edilip eşleşmeli | Tam eşleşme, normalizasyon yok |
| `TC-002-06` | `0` önekli GSM normalize edilmeli | Tam eşleşme, normalizasyon yok |
| `TC-002-14` | Baştaki/sondaki boşluk kırpılmalı | Kırpılmıyor |
| `TC-002-21` | `sort` parametresi uygulanmalı (ACC-008) | `PageRequest.of(page, size)` — sort yok |
| `TC-002-22` | NAT ID 10 hane reddedilmeli | Arama parametresinde format kuralı yok |
| `TC-002-23` | NAT ID 12 hane reddedilmeli | Aynı |
| `TC-002-25` | 5 ile başlamayan GSM reddedilmeli | Aynı |
| `TC-002-26` | En az bir filtre zorunlu (ACC-003) | API tüm aktif müşterileri döner |
| `TC-003-22` | First Name max 50 karakter | DTO'da `@Size` yok |
| `TC-003-29` | Adres `street` max 200 (onboarding yolu) | Bu koşumda B-06 nedeniyle sonuçsuz |
| `TC-003-38` | Home Phone 2 ile başlamalı | Kod `^[0-9]{10,11}$` uyguluyor |
| `TC-004-17` | First Name max 50 karakter (güncelleme) | DTO'da `@Size` yok |
| `TC-005-20` | City listeden seçilmeli (geçersiz id reddedilmeli) | **B-07** — doğrulanmıyor, 201 dönüyor |
| `TC-003-41` | Pasif müşterinin T.C. kimlik no kuralı | Dokümanda tanımlı değil — **açık analiz sorusu** (aşağıya bakınız) |

# 4. Doküman düzeltmesi gerektirenler

| Konu | Durum |
|---|---|
| **İsim alanları yalnızca harf ve boşluk içerebilir** | Kodda var (sprint-4: `^[a-zA-ZçÇğĞıİöÖşŞüÜ\s]*$` → `Name should contain letters only.`), **dokümanda yok**. `TC-003-19` ve `TC-004-16` ile test ediliyor. Doküman validasyon tablolarına eklenmeli. |
| **Home Phone kuralı çelişkili** | FR-003 tablosu "10 hane, 2 ile başlar", FR-006 tablosu "10–11 hane" diyor. Kod ikincisini uyguluyor. Doküman içi çelişki giderilmeli. |
| **Arama görünümü asenkron** | Güncelleme sonrası arama sonucuna yansıma Kafka ile gecikmeli. Dokümanda "anında" beklentisi var gibi okunuyor; kabul kriterine gecikme toleransı yazılmalı. |

## 4.1 Açık analiz sorusu — pasif müşterinin T.C. kimlik numarası

**Senaryo:** Bir müşteri soft-delete edildikten sonra, aynı T.C. kimlik numarasıyla yeni müşteri açılabilmeli mi?

- **Doküman:** AS-002 silmenin fiziksel olmadığını söylüyor ama bu senaryo için **kabul kriteri yok**.
- **Fiili davranış (`TC-003-41`):** sistem **409** dönüyor — pasif müşterinin numarası hâlâ tekillik kontrolüne takılıyor, yeni kayıt açılamıyor.
- **Neden önemli:** Gerçek hayatta yanlışlıkla silinen bir müşteri tekrar sisteme girilemez; kullanıcıya "bu kimlik zaten kayıtlı" denir ama arama sonucunda o müşteri görünmez. Kullanıcı çıkmaza girer.
- **Karar gerekiyor:** (a) mevcut davranış doğru kabul edilip dokümana yazılacak, (b) reaktivasyon akışı tanımlanacak, ya da (c) tekillik kontrolü yalnızca aktif kayıtlara uygulanacak.

Test, karar alınana kadar her koşumda açık soru olarak raporlanır.

---

# 5. FR-001 — Sistem Girişi

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Giriş ekranı gösterilir | 🖥 | — |
| 002 | İki alan doluysa Login aktif | ℹ | `TC-001-06`, `TC-001-07` (boş alan → 400) |
| 003 | Şifre göz ikonu | 🖥 | — |
| 004 | Bilgiler doğrulanır | ✅ | `TC-001-01`, `TC-001-03` |
| 005 | Hatalı bilgide mesaj | ❌ **B-01** | `TC-001-04`, `TC-001-05` |
| 006 | Yazınca mesaj kaybolur | 🖥 | — |
| 007 | Hatalı girişte oturum oluşmaz | ❌ **B-01** | `TC-001-04` |
| 008 | 5 hatalı deneme → 15 dk kilit | ⏸ | Keycloak realm'de yapılandırılmış (`bruteForceProtected`, `failureFactor=5`, `waitIncrementSeconds=900`). Test edilmiyor: koşum sırasında hesabı kilitleyip sonraki tüm testleri düşürür |
| 009 | 8 saatlik oturum token'ı | ✅ | `TC-001-02` (`expiresIn=28800` + JWT `exp-iat`) |
| 010 | Customer Search'e yönlendirme | 🖥 | — |
| 011 | Süre dolunca token geçersiz | ✅ | `TC-001-12` (refresh), `TC-001-14` (30 sn'lik token), `TC-001-15`, `TC-001-16` |
| 012 | Logout | ✅ | `TC-001-17` |
| 013 | Logout sonrası oturum biter | ✅ | `TC-001-18` (test geçiyor; ORTAK 5xx kontrolü B-01 nedeniyle kırmızı) |

Validasyon tablosu: zorunlu alanlar ✅ (`TC-001-06/07`), baş/son boşluk ✅ (`TC-001-08/09`), harfe duyarsızlık ✅ (`TC-001-11`), max 50 karakter ⚠ (`TC-001-10`).
Ek: bozuk JWT ve token'sız erişim ✅ (`TC-001-15`, `TC-001-16`), refresh token tekrar kullanımı belgelendi (`TC-001-13`).

# 6. FR-002 — Müşteri Arama ve Görüntüleme

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Filtre alanlarıyla arama | ✅ | `TC-002-01` … `TC-002-04` |
| 002 | First+Last AND, diğerleri OR | ✅ | `TC-002-07` (kesişim), `TC-002-08` (boş kesişim), `TC-002-09` (aynı soyad), `TC-002-10` (OR) |
| 003 | En az bir filtre olmadan Search pasif | ⚠ | `TC-002-26` — API 200 + tüm aktif müşteriler |
| 004 | Search kriterlere göre arar | ✅ | `TC-002-01/02/03/04/11` |
| 005 | Sonuçlar tablo halinde | ✅ | `TC-002-01` |
| 006 | Kolonlar: Customer ID, First/Second/Last Name, Role, NAT ID | ✅ | `TC-002-01` (şema kontrolü) |
| 007 | İlk 10 kayıt + sayfalama | ✅ | `TC-002-18`, `TC-002-19`, `TC-002-20` |
| 008 | Kolon başlığına tıklayınca sıralama | ⚠ | `TC-002-21` |
| 009 | Customer ID'ye tıklayınca Customer Info | ℹ | `TC-002-27` (veri ucu), ekran geçişi UI |
| 010 | Kayıt yoksa bilgilendirme | ℹ | `TC-002-16` (boş liste), mesaj UI |
| 011 | Create Customer butonu | 🖥 | — |
| 012 | Clear butonu | 🖥 | — |
| — | Pasif müşteri sonuçlarda yok | ✅ | `TC-002-17` |
| — | Tam eşleşme alanında kısmi değer | ✅ | `TC-002-15` |
| — | Olmayan müşteri detayı | ✅ | `TC-002-28` |

Validasyon tablosu: NAT ID/GSM format kuralları ⚠ (`TC-002-22/23/25`), Customer ID sayısal ✅ (`TC-002-24`), Order Number arama kriteri **API'de yok** (FR-002 kapsamı dışına alındı, arşivdeki koleksiyonda izleniyor).

# 7. FR-003 — Müşteri Oluşturma

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Create Customer → demografik ekran | 🖥 | — |
| 002 | Zorunlu alanlar dolmadan Next pasif | ℹ | `TC-003-14` … `TC-003-18` |
| 003 | Previous ile geri dönüş | 🖥 | — |
| 004 | Önce Nationality ID tekillik kontrolü | ✅ | `TC-003-01`, `TC-003-07` |
| 005 | Aynı Nationality ID varsa ilerlenemez | ✅ | `TC-003-07`, `TC-003-13` |
| 006 | KPS ile doğrulama | ⚠ | `FakeIdentityVerificationServiceImpl` — her kimliği doğruluyor |
| 007 | KPS başarısızsa ilerlenemez | ⚠ | Negatif senaryo üretilemiyor |
| 008 | Adres adımına geçiş | 🖥 | — |
| 009 | Bir veya birden fazla adres | ✅ | `TC-003-31` |
| 010 | En fazla 5 adres | ✅ | `TC-003-31` (5 → 201), `TC-003-32` (6 → 400) |
| 011 | En az bir adres zorunlu | ✅ | `TC-003-30` |
| 012 | Kontakt adımına geçiş | 🖥 | — |
| 013 | Email, Mobile, Home Phone, Fax girilir | ✅ | `TC-003-08`, `TC-003-40` |
| 014 | Geçerli format olmadan Create pasif | ℹ | `TC-003-33` … `TC-003-39` |
| 015 | Create → müşteri kaydı | ✅ | `TC-003-08` |
| 016 | Başarı sonrası Customer Info | ℹ | `TC-003-10` (veri ucu) |
| 017 | Varsayılan 223 tipi hesap | ✅ | `TC-003-08` |

**Saga kanıtları** (dokümanda yok, mikroservis mimarisi gereği eklendi): `TC-003-09` party-service'te kayıt oluştu, `TC-003-10` demografik bilgi tutarlı, `TC-003-11` adres contact-info-service'te, `TC-003-12` iletişim bilgisi kaydedildi.

**Sınır değerleri (BVA):** `TC-003-06` (01.01.1900 → 200), `TC-003-05` (31.12.1899 → 400), `TC-003-21` (50 karakter → 201), `TC-003-22` (51 karakter ⚠), `TC-003-28` (street 200 → 201), `TC-003-31/32` (5/6 adres), `TC-003-37` (GSM 9 hane → 400).

**Açık analiz sorusu:** `TC-003-41` — pasif müşterinin T.C. kimlik numarasıyla yeni kayıt (bkz. bölüm 4.1).

# 8. FR-004 — Müşteri Bilgilerini Güncelleme

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Kalem ikonu → update ekranı | 🖥 | — |
| 002 | Mevcut bilgiler dolu gelir | ✅ | `TC-004-01` |
| 003 | Alanlar güncellenebilir | ✅ | `TC-004-02` |
| 004 | Zorunlu alan boşsa Save pasif | ℹ | `TC-004-07` … `TC-004-12` |
| 005 | Cancel ile kayıtsız dönüş | 🖥 | — |
| 006 | Save'de tekillik kontrolü | ❌ **B-03** | `TC-004-06` |
| 007 | Çakışmada uyarı | ❌ **B-03** | `TC-004-06` |
| 008 | KPS doğrulaması | ⚠ | Fake |
| 009 | KPS başarısızsa kaydedilmez | ⚠ | Fake |
| 010 | Bilgiler kaydedilir | ✅ | `TC-004-02` |
| 011 | Customer Info güncel görünür | ✅ | `TC-004-03`, `TC-004-04` |

**Kritik ek test:** `TC-004-05` — müşterinin **kendi** Nationality ID'si ile güncelleme 409 vermemeli (tekillik kontrolü "kendisi hariç" çalışmalı). ✅ geçiyor.
`TC-004-04` arama görünümünün asenkron güncellenmesini yeniden deneme ile doğrular.

# 9. FR-005 — Adres Yönetimi

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Address tabında adresler listelenir | ✅ | `TC-005-01` |
| 002 | Add New Address | ✅ | `TC-005-02` |
| 003 | Zorunlu alan yoksa Save pasif | ℹ | `TC-005-09` … `TC-005-12` |
| — | City **listeden seçim** olmalı | ❌ **B-07** | `TC-005-20` — geçersiz id kabul ediliyor |
| 004 | Save → kaydedilir, listeye eklenir | ✅ | `TC-005-02` |
| 005 | En fazla 5 adres | ✅ | `TC-005-15` (5'e kadar), `TC-005-16` (6. → 409) |
| 006 | Birincil adres seçilebilir | ✅ | `TC-005-04`, `TC-005-05` (tek birincil kuralı) |
| 007 | Tek adres otomatik birincil | ✅ | `TC-005-01` |
| 008 | Delete ile silme | ✅ | `TC-005-07`, `TC-005-08` |
| 009 | Birincil adres silinemez | ✅ | `TC-005-06` |
| 010 | Onay + faturaya bağlılık kontrolü | ℹ | Fatura tarafı FR-008 kapsamında (arşiv) |
| 011 | Faturaya bağlı adres silinemez | ℹ | FR-008 kapsamında (arşiv) |
| 012 | Faturasız, birincil olmayan adres silinir | ✅ | `TC-005-07` |
| 013 | Edit ile güncelleme | ✅ | `TC-005-03` |
| 014 | Güncellemede zorunlu alanlar | ℹ | `TC-005-09` … `TC-005-12` |
| 015 | Save sonrası mesaj + dönüş | ℹ | `TC-005-03` (API 200) |

**Güvenlik (dokümanda yok, orta katman için zorunlu):** `TC-005-17`, `TC-005-18` IDOR — başka müşterinin adresi 404; `TC-005-19` olmayan adres 404.

---

# 10. Kapsam dışı ve sonraki faz

**Orta katmanda test edilmeyen ACC sayısı:** FR-001…FR-005 içinde ~30 madde saf UI davranışıdır (buton durumu, ikon, mesaj rengi, ekran geçişi). Bunlar bilinçli olarak API testine dönüştürülmedi.

**FR-006 … FR-011:** Sprint-6'da bu matrise eklendi — bkz. bölüm 11…16.

**FR-012 … FR-018:** Bu fazın kapsamı dışında. İlgili testler `archive/CRM-Lite-TUM-FR.postman_collection.json` içinde korunuyor (şu an bakımda değil) ve şu bulguları üretmişti:

- **B-04** — `POST /api/v1/orders/submit` HTTP 200 dönüp gövdede `status:500` yazıyor
- **B-05** — `/api/v1/product-procutOfferings` uç adresinde yazım hatası
- FR-013 — katalog/kampanya uçları sprint-4'te eklendi ama kriterli arama, sayfalama ve bundled offers hâlâ yok
- FR-018 ACC-006 — hata mesajları yalnızca İngilizce (`messages_tr.properties` yok)

---

# 11. FR-006 — İletişim Bilgileri Yönetimi

**Fixture:** Müşteri E · **Uçlar:** `GET|PUT /api/v1/customers/{custId}/contact`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Contact Medium tabında bilgiler görüntülenir | ✅ | `TC-006-01` |
| 002 | Kalem ikonu → update ekranı | ℹ | `TC-006-02` (veri ucu) |
| 003 | Mevcut bilgiler dolu gelir | ✅ | `TC-006-02` |
| 004 | Alanlar güncellenebilir | ✅ | `TC-006-03` |
| 005 | Zorunlu/format eksikse Save pasif | ℹ | `TC-006-06` … `TC-006-22` |
| 006 | Cancel uyarısı | 🖥 | — |
| 007 | Save → başarı mesajı + dönüş | ✅ | `TC-006-03`, `TC-006-04` (kalıcılık) |

**Validasyon tablosu karşılıkları**

| Alan | Kural | Test | Durum |
|---|---|---|---|
| E-mail | Geçerli e-posta formatı | `TC-006-07`, `TC-006-08`, `TC-006-09` | ✅ |
| E-mail | Doküman mesajı "Email must be a valid email address." | `TC-006-10` | ⚠ **B-13c** — API "Invalid email format" |
| Mobile Phone | 10 hane, 5 ile başlar | `TC-006-11` … `TC-006-16` | ✅ |
| Home Phone | 10–11 hane, opsiyonel | `TC-006-17` … `TC-006-20` | ✅ |
| Home Phone | Boş string opsiyonel sayılmalı | `TC-006-21` | ⚠ boş string 400 döner (null gönderilmeli) |
| Fax | Geçerli faks formatı | `TC-006-22` | ✅ |
| Fax | Doküman mesajı "Invalid fax number." | `TC-006-23` | ⚠ **B-13c** |

**BVA:** `TC-006-13` (GSM 9 hane → 400), `TC-006-14` (11 hane → 400), `TC-006-15` (10 hane → 200), `TC-006-17/18` (ev tel. 10/11 hane → 200), `TC-006-19/20` (9/12 hane → 400).
**Güvenlik / hata yolları:** `TC-006-24` olmayan müşteri 404 · `TC-006-25` pasif müşteri 404 · `TC-006-26` geçersiz tip 400 · `TC-006-27` tokensiz 401.

# 12. FR-007 — Müşteri Silme

**Fixture:** Müşteri G (uçtan uca senaryo) · **Uç:** `DELETE /api/v1/customers/{custId}`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Silme onayı istenir | 🖥 | — |
| 002 | Hesaplar ve bağlı ürünler kontrol edilir | ✅ | `TC-007-03` (hesap), `TC-007-06` (ürün) |
| 003 | Aktif fatura hesabı varsa silinemez + mesaj | ✅ | `TC-007-03`, `TC-007-04` (yan etki yok) |
| 004 | Pasif hesaba bağlı ürün varsa silinemez | ⚠ | `TC-007-06` — guard etkisiz (`NoOpBillingAccountProductGuard`) |
| 005 | Uygunsa soft delete | ✅ | `TC-007-07`, `TC-007-08` |
| 006 | Silme sonrası arama ekranına dönüş | ✅ | `TC-007-09` (silinen müşteri aramada yok) |

**Ek kontroller:** `TC-007-10` silinen müşterinin hesap listesi (⚠ **B-11**) · `TC-007-11` tekrar silme 404 · `TC-007-12` olmayan müşteri 404 · `TC-007-13` geçersiz tip 400 · `TC-007-14` tokensiz 401.

`TC-007-09` arama görünümünün asenkron güncellenmesini yeniden deneme (poll) ile doğrular — `TC-004-04` ile aynı desen.

# 13. FR-008 — Fatura Hesabı Oluşturma

**Fixture:** Müşteri F · **Uç:** `POST /api/v1/customers/{custId}/accounts`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Customer Account tabı açılır | ✅ | `TC-008-01` |
| 002 | Create New Account → ekran | 🖥 | — |
| 003 | Account Name / Description alanları | ✅ | `TC-008-02` |
| 004 | Yeni adres oluşturulabilir **veya** mevcut seçilebilir | ✅ | `TC-008-02` (mevcut), `TC-008-04` (yeni) |
| 005 | Yeni adres alanları (City/Street/No/Açıklama) | ✅ | `TC-008-15` … `TC-008-18` |
| 006 | Adres ekranında Cancel uyarısı | 🖥 | — |
| 007 | Adres kaydedilir ve döner | ✅ | `TC-008-04`, `TC-008-05` |
| 008 | Seçilen adres ekranda listelenir | ✅ | `TC-008-05` |
| 009 | Ad + açıklama + adres olmadan Create pasif | ℹ / ⚠ | `TC-008-07/08` (ad ✅), `TC-008-13` (adres ✅), `TC-008-12` (açıklama ⚠ **B-12**) |
| 010 | Cancel uyarısı | 🖥 | — |
| 011 | Create → hesap yaratılır | ✅ | `TC-008-02` |
| 012 | Hesap **224** tipinde açılır | ✅ | `TC-008-03` |
| 013 | Başarı mesajı + dönüş | ℹ | `TC-008-02` (API 201) |
| 014 | Yeni hesap tabloda listelenir | ✅ | `TC-008-06` |

**Validasyon:** `TC-008-09` ad 50 karakter → 201 (BVA) · `TC-008-10` 51 karakter → 400 (BVA) · `TC-008-11` uzunluk mesajı ⚠ **B-13a** · `TC-008-19` street 201 karakter → 400 · `TC-008-20` geçersiz `cityId` ⚠ **B-14**.
**XOR kuralı:** `TC-008-13` (hiçbiri → 400), `TC-008-14` (ikisi birden → 400).
**Güvenlik:** `TC-008-21` IDOR başka müşterinin adresi 404 · `TC-008-22` olmayan adres 404 · `TC-008-23` olmayan müşteri 404 · `TC-008-24` pasif müşteri 404 · `TC-008-25` tokensiz 401.

# 14. FR-009 — Fatura Hesabı ve Bağlı Ürün Görüntüleme

**Uçlar:** `GET /api/v1/customers/{custId}/accounts` · `GET /api/v1/orders?custAcctId={id}`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Hesap varsa tablo, yoksa "There are no billing accounts yet." | ℹ / ⚠ | `TC-009-01` — boş durum API'de oluşamaz (onboarding varsayılan hesap açar); olmayan müşteri de boş sayfa döner ⚠ **B-11** (`TC-009-11`) |
| 002 | Tablo kolonları (Status/Number/Name/Type/Action) | ✅ | `TC-009-02` |
| 003 | Satır genişletme oku | 🖥 | — |
| 004 | Bağlı ürünler tablosu | ✅ | `TC-009-08` |
| 005 | Ürün tablosu kolonları | ✅ | `TC-009-09` (liste boşsa açıkça raporlanır) |
| 006 | Göz ikonu → ürün detay modalı | ⚠ | `TC-009-10` — uç yok |
| 007 | Modal alanları (Offer Name/ID, Spec ID, Start Date, Prod Chars, Service Address) | ⚠ | `TC-009-10` — `CustOrdItemResponse` bu alanları taşımıyor |
| 008 | Modal kapatma | 🖥 | — |
| 009 | İlk 5 kayıt + sayfalama | ✅ | `TC-009-03` (boyut 5), `TC-009-04` (6 hesaba tamamlar), `TC-009-05`, `TC-009-06`, `TC-009-07` |

**Kusurlar:** `TC-009-12` negatif sayfa → ❌ **B-10** (500) · `TC-009-13` `size=0` → ❌ **B-10** (500) · `TC-009-11` olmayan müşteri ⚠ **B-11**.
**Güvenlik:** `TC-009-14` IDOR · `TC-009-15` geçersiz tip 400 · `TC-009-16` tokensiz 401.

# 15. FR-010 — Fatura Hesabı Güncelleme

**Uç:** `PUT /api/v1/customers/{custId}/accounts/{accountId}`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Edit → Update ekranı | 🖥 | — |
| 002 | Alanlar mevcut bilgilerle dolu | ✅ | `TC-010-01` |
| 003 | Yeni adres **veya** mevcut adres | ✅ | `TC-010-06`, `TC-010-07` |
| 004 | Yeni adres alanları | ✅ | `TC-008-15` … `TC-008-18` ile aynı kurallar |
| 005 | Adres ekranında Cancel uyarısı | 🖥 | — |
| 006 | Adres kaydedilir ve döner | ✅ | `TC-010-07` |
| 007 | Adres ekranda listelenir | ✅ | `TC-010-06` |
| 008 | Ad + açıklama + adres olmadan Save pasif | ℹ / ⚠ | `TC-010-08` (ad ✅), `TC-010-12` (adres ✅), `TC-010-11` (açıklama ⚠ **B-12**) |
| 009 | Cancel uyarısı | 🖥 | — |
| 010 | Save → güncellenir | ✅ | `TC-010-02` |
| 011 | Başarı mesajı + dönüş | ℹ | `TC-010-02` (API 200) |
| 012 | Tabloda güncel görünür | ✅ | `TC-010-03` |

**Değişmezlik regresyonu (dokümanda yok, sözleşme gereği):** `TC-010-04` `accountNo` değişmez · `TC-010-05` `accountTpId` değişmez.
**BVA / validasyon:** `TC-010-09` (50 → 200), `TC-010-10` (51 → 400), `TC-010-13` (XOR).
**Güvenlik:** `TC-010-14` olmayan hesap 404 · `TC-010-15` IDOR 404 · `TC-010-16` olmayan müşteri 404 · `TC-010-17` geçersiz tip 400 · `TC-010-18` tokensiz 401.

# 16. FR-011 — Fatura Hesabı Silme

**Uçlar:** `DELETE .../accounts/{accountId}` · `PATCH .../accounts/{accountId}/status`

| ACC | Özet | Durum | Test |
|---|---|---|---|
| 001 | Silme onay diyaloğu | 🖥 | — |
| 002 | Aktiflik ve bağlı ürün kontrolü | ✅ | `TC-011-01`, `TC-011-10` |
| 003 | Aktif hesap silinemez + mesaj | ✅ | `TC-011-01`, `TC-011-02` (yan etki yok) |
| 004 | Pasif olsa da bağlı ürün varsa silinemez | ⚠ | `TC-011-10` — guard etkisiz (`NoOpBillingAccountProductGuard`) |
| 005 | Pasif + ürünsüz hesap soft delete + mesaj | ✅ | `TC-011-11`, `TC-011-12` |

**Durum yönetimi (`PATCH .../status` — sprint-5'te eklendi, FR-011 ACC-005'i test edilebilir kıldı):**
`TC-011-06` PASSIVE → 200 · `TC-011-07` listede pasif görünür · `TC-011-08` yeniden ACTIVE → 200 · `TC-011-09` yeniden PASSIVE · `TC-011-15` geçersiz durum 400 · `TC-011-16` boş durum 400.

**Varsayılan (CUST_ACCT) hesap koruması:** `TC-011-03` silinemez 409 · `TC-011-04` durumu değiştirilemez 409 · `TC-011-05` hata mesajı yanıltıcı ⚠ **B-13b**.

**Çapraz kural:** `TC-011-19` — fatura hesabına bağlı adres silinemez (FR-005 ACC-011). Sprint-4'te arşiv koleksiyonunda kapsanamamıştı, artık doğrulanıyor. ✅

**Güvenlik / hata yolları:** `TC-011-13` tekrar silme 404 · `TC-011-14` silinen hesap güncellenemez 404 · `TC-011-17` IDOR 404 · `TC-011-18` olmayan hesap 404 · `TC-011-20` tokensiz 401.

---

# 17. Sprint-6 bulguları (FR-006…FR-011)

Ayrıntılı kök neden analizi, log alıntıları ve düzeltme önerileri: **`analiz-test/bulgular/BULGULAR-sprint6.md`**

| # | Başlık | Şiddet | Test |
|---|---|---|---|
| **B-10** | Geçersiz sayfalama parametresi (`page=-1`, `size=0`) 500 döndürüyor. `PageRequest.of` `IllegalArgumentException` fırlatıyor, `GlobalExceptionHandler`'da karşılığı yok. B-09 ile aynı aile. | **Yüksek** | `TC-009-12`, `TC-009-13` |
| **B-11** | `GET /accounts` var olmayan/silinmiş müşteri için 404 yerine 200 + boş sayfa dönüyor. `getAccounts`, diğer metotların aksine `getActiveCustomerOrThrow` çağırmıyor. | Orta | `TC-009-11`, `TC-007-10` |
| **B-12** | `accountDesc` dokümanda zorunlu, request record'larında hiçbir kısıt yok. ACC-009/ACC-008 yalnızca front-end'de karşılanıyor. | Orta | `TC-008-12`, `TC-010-11` |
| **B-13** | Yanıltıcı mesajlar: (a) uzunluk ihlali "This field is required." diyor, (b) durum değiştirme "cannot be deleted" diyor, (c) faks/e-posta mesajları dokümandan farklı. | Düşük | `TC-008-11`, `TC-011-05`, `TC-006-10`, `TC-006-23` |
| **B-14** | Fatura adresinde de `cityId` lookup doğrulaması yok — B-07'nin ikinci giriş yolu. | Düşük | `TC-008-20` |
