# Bulgu Raporu — sprint-6 · FR-006…FR-011 (Fatura Hesabı / İletişim / Silme)

| | |
|---|---|
| Tarih | 05.08.2026 |
| Branch | `test/integration` |
| Kapsam | **FR-006** İletişim Bilgileri · **FR-007** Müşteri Silme · **FR-008** Fatura Hesabı Oluşturma · **FR-009** Hesap ve Ürün Görüntüleme · **FR-010** Hesap Güncelleme · **FR-011** Hesap Silme |
| Yöntem | Postman/Newman API test takımı (FR-001…FR-011, 264 istek / **1157 assertion**) + servis loglarından kök neden analizi |
| Ortam | Tüm servisler temiz başlatıldı (config, discovery, gateway, lookup, party, contact-info, customer, order) |
| Sonuç | **1153 geçti / 4 kaldı** — 4 hatanın tamamı tek bir bulgudan (**B-10**) kaynaklanıyor |

> Sprint-5'te açılan B-01/B-03/B-06/B-08/B-09 bulgularının hepsi kapalı ve bu koşumda da yeşil kaldı — regresyon yok.

Her bulgu için **kök neden servis loglarından ya da kaynak koddan teyit edilmiştir**; tahmin yoktur.

---

# ✅ 2. DOĞRULAMA TURU — 06.08.2026 · commit `9158ad5` (validasyon metinleri)

Gereksinim dokümanının validasyon tabloları 06.08.2026'da güncellendi (uzunluk kuralları ayrı satıra alındı, FR-003 ↔ FR-006 iletişim alanı çelişkileri giderildi). Testler yeni metinlere göre güncellendi, ardından `9158ad5 fix: split shared validation messages per updated FR-003/005/006/008/010 tables` çekilip doğrulandı.

| Koşum | Assertion | Hata | Bekleyen boşluk |
|---|---|---|---|
| Doküman güncellemesi sonrası (düzeltme öncesi) | 1167 | 0 | **26** |
| **Düzeltme sonrası** | 1161 | **0** ✅ | **15** |
| **`strictMode=true` (kesin kanıt)** | 1161 | **15** — hepsi bu partide olmayan maddeler | — |

**Bu partideki 11 maddenin 11'i de kapandı:**

| Konu | Testler | Durum |
|---|---|---|
| `@Size` mesajı `FIELD_REQUIRED`'ı paylaşıyordu (**B-13a**) | `TC-003-29`, `TC-005-14`, `TC-008-11`, `TC-010-10a` | ✅ `FIELD_MAX_LENGTH` anahtarı eklenmiş: `Maximum {max} characters are allowed.` — tek anahtar hem 50 hem 200 için çalışıyor |
| İsim alanlarında uzunluk kısıtı hiç yoktu | `TC-003-22`, `TC-004-17` | ✅ `IndividualInfo` ve `UpdateIndividualInfo` isim alanlarına `@Size(max=50)` eklenmiş |
| Faks mesajı telefon mesajını paylaşıyordu (**B-13c**) | `TC-006-23`, `TC-003-39` | ✅ `FAX_INVALID` = `Invalid fax number.` eklenmiş |
| Ev telefonu kuralı dokümanla uyumsuzdu | `TC-003-38`, `TC-006-18`, `TC-006-28` | ✅ `^[0-9]{10,11}$` → `^2[0-9]{9}$` (10 hane, 2 ile başlar) |
| E-posta mesajında nokta eksikti | `TC-006-10` | ✅ `Invalid email format.` |

**Test tarafında bir eksiğimiz çıktı ve düzeltildi:** `TC-003-39` (onboarding faks) hâlâ telefon mesajını bekliyordu; faks kendi anahtarına geçince kırmızıya döndü. Bu, davranış testlerinin güvenlik ağı olarak çalıştığını gösteriyor — mesaj yanlış yönde değişseydi aynı şekilde yakalanırdı. Test dokümanın metnine göre güncellendi.

**Assertion 1167 → 1161:** `TC-003-22`, `TC-003-29`, `TC-003-38` artık doğru şekilde 400 dönüyor, dolayısıyla o isteklerde müşteri oluşmuyor; temizlik döngüsü de o kadar az kayıt siliyor.

## `strictMode` ile kalan 15 madde (bu partide değil)

| Alan | Testler |
|---|---|
| FR-002 arama kuralları (GSM normalizasyonu, kırpma, sıralama, filtre zorunluluğu, NAT ID uzunluğu) | `TC-002-05/06/14/21/22/23/25/26` |
| FR-001 kullanıcı adı uzunluğu | `TC-001-10` |
| Açık analiz sorusu — pasif müşterinin T.C. no'su | `TC-003-41` |
| Ev telefonu boş string sözleşmesi | `TC-006-21` |
| Bağlı ürün guard'ı (order-service entegrasyonu bekliyor) | `TC-007-06`, `TC-011-10` |
| Ürün detay modalı alanları | `TC-009-10` |
| **B-13b** — durum değiştirme "silinemez" mesajı | `TC-011-05` |

---

# ✅ 1. DOĞRULAMA TURU — 06.08.2026 · commit `e528169`

`e528169 fix: QA-reported bugs B-10/B-11/B-12/B-14` çekildi, `customer-service` ve `order-service` yeni kodla yeniden başlatıldı, test takımı temiz ortamda yeniden koşuldu.

| Koşum | Assertion | Hata | Bekleyen boşluk |
|---|---|---|---|
| Düzeltme öncesi | 1157 | **4** | 27 |
| **Düzeltme sonrası** | **1159** | **0** ✅ | **21** |

| Bulgu | Durum | Doğrulama |
|---|---|---|
| **B-10** Sayfalama 500 | ✅ **Çözüldü** | `GlobalExceptionHandler`'a `IllegalArgumentException` handler'ı eklenmiş. `?page=-1` ve `?size=0` artık `400 "Invalid request parameter."` dönüyor. `TC-009-12`, `TC-009-13` yeşil |
| **B-11** Hesap listesi 404 dönmüyordu | ✅ **Çözüldü** | `getAccounts` başına `customerFinder.getActiveCustomerOrThrow(custId)` eklenmiş. Tanımsız müşteri için `404 "Customer not found with id: 999999999"`. `TC-009-11`, `TC-007-10` → `[GAP KAPANDI]` |
| **B-12** `accountDesc` doğrulanmıyordu | ✅ **Çözüldü** | Her iki request record'una `@NotBlank` eklenmiş. `TC-008-12`, `TC-010-11` → `[GAP KAPANDI]` |
| **B-14** `cityId` doğrulanmıyordu | ✅ **Çözüldü** | Önerildiği gibi **`AddressBusinessRules.ensureCityExists`** seviyesinde çözülmüş — onboarding, adres ekleme/güncelleme ve fatura hesabı "yeni adres" akışı aynı metodu çağırıyor. **Sprint-5'te açık kalan B-07 de aynı düzeltmeyle kapandı** (`TC-005-20` ve `TC-008-20` → `[GAP KAPANDI]`) |

Düzeltmenin kalitesine dair not: B-14 için üç ayrı çağrı yolunu tek bir kurala bağlamışlar; ileride yeni bir adres ucu eklendiğinde kontrolün unutulma riski kalmıyor. B-10'un yorum satırında `CustomerController.search`'ün de aynı desende olduğu belirtilmiş — yani sorun tek uçta değil, ailede çözülmüş.

**Toplam assertion 1157 → 1159:** B-11 düzeltmesiyle `TC-009-11` ve `TC-007-10` bekleyen boşluktan gerçek teste dönüştü, her biri bir assertion ekledi.

## Kalan işler

Bu turda gönderilmeyen üç madde hâlâ açık — hepsi hata mesajı metni ilgili:

| # | Konu | Durum |
|---|---|---|
| **B-13a** | Uzunluk ihlali "This field is required." diyor | Açık — gereksinim dokümanı güncellendi, hedef metin netleşti |
| **B-13b** | Durum değiştirme "cannot be deleted" hatası dönüyor | Açık |
| **B-13c** | Faks hatası telefon mesajı dönüyor | Açık — doküman "Invalid fax number." diyor |

> Gereksinim dokümanının validasyon tabloları 06.08.2026'da güncellendi: uzunluk kuralları ayrı satıra alındı ve `"Maximum 50 characters are allowed."` / `"Maximum 200 characters are allowed."` mesajları tanımlandı; FR-003 ile FR-006 arasındaki iletişim alanı çelişkileri giderildi. Testler bu yeni metinlere göre güncellenecek ve ikinci tur bulgu listesi buna göre çıkarılacaktır.

---

## Özet tablo

### Düzeltilmesi gerekenler

| # | Sorun | Çözüm | Dosya | Öncelik |
|---|---|---|---|---|
| **B-10** | `?page=-1` ve `?size=0` → **500**. `PageRequest.of` `IllegalArgumentException` fırlatıyor, handler yok | `IllegalArgumentException` handler'ı ekle **veya** `@Min(0)`/`@Min(1)` ile parametreleri doğrula | `CustomerAccountController:53` + `GlobalExceptionHandler` | 🔴 **Yüksek** |
| **B-11** | Var olmayan/silinmiş müşteri için hesap listesi 404 yerine **200 + boş sayfa** dönüyor | Metodun başına `customerFinder.getActiveCustomerOrThrow(custId);` — diğer metotlarla aynı hale gelir | `BillingAccountServiceImpl.getAccounts` | 🟠 Orta |
| **B-12** | `accountDesc` boş ya da hiç gönderilmemişken **201** dönüyor; dokümanda zorunlu | Her iki record'un `accountDesc` alanına `@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")` | `CreateBillingAccountRequest`, `UpdateBillingAccountRequest` | 🟠 Orta |
| **B-13a** | 51 karakterlik ad → *"This field is required."* Alan dolu olduğu halde "zorunlu" mesajı çıkıyor | Yeni mesaj anahtarı (ör. `validation.field.max-length`) ekle, `@Size` onu kullansın | `CreateBillingAccountRequest`, `UpdateBillingAccountRequest`, `AddressInfo.streetName` | 🟡 Düşük |
| **B-13b** | `PATCH .../status` isteği *"...cannot be **deleted**."* hatası dönüyor; kullanıcı silme yapmadı | Durum değişimi için ayrı istisna/mesaj anahtarı (`...default-cannot-be-changed`) | `BillingAccountServiceImpl.updateBillingAccountStatus` | 🟡 Düşük |
| **B-13c** | Faks hatası telefon mesajını paylaşıyor → kullanıcı hangi alanın hatalı olduğunu ayırt edemiyor | `MessageKeys.FAX_INVALID` ekle, değeri **"Invalid fax number."**, `ContactInfo.fax` bu anahtarı kullansın | `MessageKeys`, `messages*.properties`, `ContactInfo` | 🟡 Düşük |
| **B-14** | Fatura adresinde de `cityId` lookup'ta var mı diye bakılmıyor — **B-07'nin ikinci giriş yolu** | B-07 düzeltmesini `AddressBusinessRules` seviyesinde yap; her iki yol (FR-005 adres + FR-008/010 fatura adresi) tek seferde kapanır | `AddressBusinessRules` | 🟡 Düşük |

> **Test tarafında hiçbir şey yapmanıza gerek yok.** B-11…B-14 `gapTest` ile yazıldı; düzeltildiklerinde otomatik olarak `[GAP KAPANDI]` etiketiyle yeşile döner. B-10 da düzelince doğrudan yeşile döner.

### Bulgu değil — henüz yapılmamış iş

| Konu | Durum |
|---|---|
| Bağlı ürün kontrolü (FR-007 ACC-004, FR-011 ACC-004) | Kural kodda yazılı ama `NoOpBillingAccountProductGuard` sabit `false` dönüyor → kural hiç tetiklenmiyor |
| Ürün detay modalı alanları (FR-009 ACC-006/007) | `CustOrdItemResponse` altı alanın hiçbirini taşımıyor, ayrı detay ucu da yok |

Ayrıntı: en alttaki "Henüz test edilemeyen kabul kriterleri" bölümü.

### Kapsanan testler

| # | Test |
|---|---|
| B-10 | `TC-009-12`, `TC-009-13` |
| B-11 | `TC-009-11`, `TC-007-10` |
| B-12 | `TC-008-12`, `TC-010-11` |
| B-13a / B-13b / B-13c | `TC-008-11` / `TC-011-05` / `TC-006-23` (ayrıca `TC-006-10` e-posta metni) |
| B-14 | `TC-008-20` |

---

# 🔴 B-10 — Geçersiz sayfalama parametresi 500 döndürüyor

**Testler:** `TC-009-12 · Negatif sayfa numarasi -> 400`, `TC-009-13 · Sifir sayfa boyutu -> 400`
**Uç:** `GET /api/v1/customers/{custId}/accounts?page=-1` · `GET .../accounts?size=0`

### Gözlem

```
GET /api/v1/customers/144/accounts?page=-1
-> 500 {"status":500,"error":"Internal Server Error","message":"Unexpected error occurred"}

GET /api/v1/customers/144/accounts?size=0
-> 500 {"status":500,"error":"Internal Server Error","message":"Unexpected error occurred"}
```

### Kök neden (log ile teyit edildi)

```
ERROR c.e.c.c.b.e.GlobalExceptionHandler : Unexpected error

java.lang.IllegalArgumentException: Page index must not be less than zero
	at org.springframework.data.domain.AbstractPageRequest.<init>(AbstractPageRequest.java:46)
	at org.springframework.data.domain.PageRequest.of(PageRequest.java:61)
	at com.etiya.crm.customerservice.api.controllers.CustomerAccountController.getAccounts(CustomerAccountController.java:53)
```

`CustomerAccountController.getAccounts` istemciden gelen `page`/`size` değerlerini doğrulamadan doğrudan `PageRequest.of(page, size)` çağrısına veriyor:

```java
@GetMapping("/{custId}/accounts")
public ResponseEntity<Page<CustomerAccountResponse>> getAccounts(@PathVariable Long custId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
    return ResponseEntity.ok(billingAccountService.getAccounts(custId, PageRequest.of(page, size)));
}
```

`PageRequest.of` negatif index ya da 1'den küçük boyut için `IllegalArgumentException` fırlatıyor. `GlobalExceptionHandler`'da bu istisna tipi için bir `@ExceptionHandler` **yok**, dolayısıyla en sondaki `@ExceptionHandler(Exception.class)` yakalıyor ve 500 dönüyor.

### Neden önemli

- **İstemci hatası, sunucu hatası olarak raporlanıyor.** Yanlış parametre gönderen bir front-end/entegrasyon, "sistem çöktü" sinyali alıyor; izleme/alarm sistemlerinde gerçek arızalarla karışıyor.
- Bu, **sprint-5'te kapatılan B-09 ile aynı ailedendir** (`MethodArgumentTypeMismatchException` → 500). O düzeltmede yalnızca tip uyuşmazlığı ele alınmıştı; sayfalama parametreleri açıkta kaldı.
- Aynı desen **başka sayfalı uçlarda da olabilir** — `PageRequest.of(...)` çağıran her controller kontrol edilmelidir.

### Önerilen düzeltme

İki yoldan biri (ikincisi tercih edilir):

**1) `GlobalExceptionHandler`'a ekleme** — en dar kapsamlı, hemen uygulanabilir:

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    // 400 + messages.properties'ten çözülen mesaj
}
```

**2) Parametreleri controller'da doğrulamak** — hata mesajı kullanıcıya anlamlı gelir:

```java
@GetMapping("/{custId}/accounts")
public ResponseEntity<Page<CustomerAccountResponse>> getAccounts(@PathVariable Long custId,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
```

`@Min`/`@Max` kullanılırsa sınıfa `@Validated` eklenmeli; ihlal `ConstraintViolationException` fırlatır ve bunun da handler'ı bulunmalıdır. Mesaj metni `messages/messages.properties` üzerinden çözülmelidir (proje kuralı: kodda literal metin yok).

> **Not:** `size` için bir üst sınır konması ayrıca önerilir — `size=1000000` şu an kabul ediliyor ve tek istekte tüm tabloyu çekmeye çalışır.

---

# 🟠 B-11 — Hesap listeleme ucu var olmayan müşteri için 404 yerine 200 dönüyor

**Testler:** `TC-009-11`, `TC-007-10` (ikisi de `[BEKLEYEN BOŞLUK]` olarak raporlanıyor, koşumu kırmıyor)
**Uç:** `GET /api/v1/customers/{custId}/accounts`

### Gözlem

| İstek | Beklenen | Gelen |
|---|---|---|
| `GET /customers/999999999/accounts` (hiç var olmayan) | 404 | **200** + `{"content":[],"totalElements":0}` |
| `GET /customers/{silinmiş}/accounts` | 404 | **200** + `{"content":[],"totalElements":0}` |
| `GET /customers/999999999/contact` | 404 | 404 ✅ |
| `GET /customers/999999999` | 404 | 404 ✅ |
| `POST /customers/999999999/accounts` | 404 | 404 ✅ |
| `PUT /customers/999999999/accounts/{id}` | 404 | 404 ✅ |

### Kök neden (kaynak koddan)

`BillingAccountServiceImpl` içindeki diğer tüm metotlar müşteri varlığını kontrol ediyor:

```java
public CustomerAccountResponse createBillingAccount(...) {
    Customer customer = customerFinder.getActiveCustomerOrThrow(custId);   // ✅
public CustomerAccountResponse updateBillingAccount(...) {
    customerFinder.getActiveCustomerOrThrow(custId);                       // ✅
public void deleteBillingAccount(...) {
    customerFinder.getActiveCustomerOrThrow(custId);                       // ✅
```

`getAccounts` ise kontrol etmiyor:

```java
public Page<CustomerAccountResponse> getAccounts(Long custId, Pageable pageable) {
    Long activeStatusId = lookupResolver.resolveActiveAccountStatusId();   // ❌ müşteri kontrolü yok
    return customerAccountRepository.findByCustomer_CustIdAndAcctStIdNotDeleted(...)
```

### Neden önemli

**FR-009 ACC-001** şunu söylüyor: *"müşterinin en az bir fatura hesabı varsa fatura hesapları tablo halinde listelenmeli; hesap yoksa tablo yerine 'There are no billing accounts yet.' mesajı gösterilmelidir."*

Şu anki davranışla front-end, **silinmiş ya da hiç var olmayan bir müşteri için de "There are no billing accounts yet." gösterir.** Yani "böyle bir müşteri yok" ile "bu müşterinin hesabı yok" ayırt edilemiyor — kullanıcı geçersiz bir müşteri sayfasında geçerli bir ekran görüyor.

### Önerilen düzeltme

```java
@Transactional(readOnly = true)
public Page<CustomerAccountResponse> getAccounts(Long custId, Pageable pageable) {
    customerFinder.getActiveCustomerOrThrow(custId);   // diğer metotlarla aynı hale gelir
    ...
}
```

Tek satır. Testler zaten hazır: düzeltme sonrası `TC-009-11` ve `TC-007-10` otomatik olarak `[GAP KAPANDI]` etiketine döner.

---

# 🟠 B-12 — `Account Description` dokümanda zorunlu, API'de hiç doğrulanmıyor

**Testler:** `TC-008-12`, `TC-010-11` (`[BEKLEYEN BOŞLUK]`)
**Uçlar:** `POST /api/v1/customers/{custId}/accounts` · `PUT .../accounts/{accountId}`

### Gözlem

```
POST /accounts  {"accountName":"Desc bos","accountDesc":"","addressId":175}   -> 201 ✅ oluşturuldu
POST /accounts  {"accountName":"Desc yok","addressId":175}                     -> 201 ✅ oluşturuldu (accountDesc: null)
PUT  /accounts/{id}  {"accountName":"X","accountDesc":"","addressId":175}      -> 200 ✅ güncellendi
```

### Kök neden

Gereksinim dokümanının FR-008 ve FR-010 validasyon tabloları:

| Adım | Alan | Zorunlu | Kural | Hata Mesajı |
|---|---|---|---|---|
| Billing Account | Account Name | **Evet** | Metin, maks 50 | "This field is required." |
| Billing Account | Account Description | **Evet** | Metin | "This field is required." |

Kodda `accountName` doğrulanıyor, `accountDesc` doğrulanmıyor:

```java
public record CreateBillingAccountRequest(
        @NotBlank(...) @Size(max = 50, ...) String accountName,
        String accountDesc,                                // ❌ hiçbir kısıt yok
        Long addressId,
        @Valid AddressInfo newAddress) {}
```

`UpdateBillingAccountRequest` de aynı durumda.

### Neden önemli

**FR-008 ACC-009** ve **FR-010 ACC-008**: *"Account Name, Account Description ve en az bir adres girilmeden Create/Save butonu aktif olmamalıdır."*

Bu kural şu anda **yalnızca front-end'de** karşılanıyor. API'ye doğrudan istek atan (Postman, entegrasyon, mobil istemci) her aktör açıklamasız hesap açabilir. Sunucu tarafı doğrulaması olmayan bir zorunluluk, zorunluluk değildir.

### Düzeltme

> **Karar verildi (analiz):** `Account Description` **zorunludur** — doküman doğru, kod eksik.

`CreateBillingAccountRequest` ve `UpdateBillingAccountRequest` içinde:

```java
@Schema(description = "Hesap aciklamasi", example = "Aylik elektrik/su faturasi icin")
@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
String accountDesc,
```

Alanın Swagger açıklamasındaki "(opsiyonel)" ifadesi de kaldırılmalıdır.

**Düzeltme sonrası:** `TC-008-12` ve `TC-010-11` otomatik olarak `[GAP KAPANDI]` etiketiyle yeşile döner; test değişikliği gerekmez.

---

# 🟡 B-13 — Yanıltıcı hata mesajları (3 ayrı yer)

Üçü de işlevsel olarak doğru davranıyor (doğru HTTP kodu dönüyor); sorun **kullanıcıya gösterilen metin**.

### B-13a · Uzunluk ihlali "alan zorunlu" diyor

**Test:** `TC-008-11`

```
POST /accounts  {"accountName": "<51 karakter>", ...}
-> 400 "This field is required."
```

Alan **dolu**, ama kullanıcı "bu alan zorunludur" mesajı görüyor. Neden: `@Size` kısıtı `FIELD_REQUIRED` mesaj anahtarını paylaşıyor.

```java
@NotBlank(message = "{" + MessageKeys.FIELD_REQUIRED + "}")
@Size(max = 50, message = "{" + MessageKeys.FIELD_REQUIRED + "}")   // ❌ aynı mesaj
String accountName,
```

**Düzeltme:** `messages*.properties`'e yeni bir anahtar (ör. `validation.field.max-length=This field can be at most {0} characters.`) ekleyip `@Size` bu anahtarı kullanmalı. Aynı desen `AddressInfo.streetName` (`@Size(max = 200)`) için de geçerlidir.

### B-13b · Durum değiştirme isteği "silinemez" hatası dönüyor

**Test:** `TC-011-05`

```
PATCH /accounts/{varsayılan hesap}/status  {"status":"PASSIVE"}
-> 409 "This account is not a billing account and cannot be deleted."
```

Kullanıcı **hiçbir silme işlemi yapmadı**, hesabın durumunu değiştirmek istedi. `DefaultAccountCannotBeDeletedException` iki farklı akışta (DELETE ve PATCH status) paylaşılıyor:

```java
// BillingAccountServiceImpl.updateBillingAccountStatus
rules.ensureAccountIsBillingType(account, lookupResolver.resolveBillingAccountTypeId());
// -> DefaultAccountCannotBeDeletedException ("...cannot be deleted.")
```

**Düzeltme:** Durum değişimi için ayrı bir mesaj anahtarı, ör. `error.customer-account.default-cannot-be-changed=This account is not a billing account and its status cannot be changed.`

### B-13c · Faks hatası telefon mesajı dönüyor

**Test:** `TC-006-23`

Doküman FR-006 validasyon tablosu faks için ayrı bir mesaj tanımlıyor:

| Alan | Kural | Hata Mesajı (doküman) | API'nin döndürdüğü |
|---|---|---|---|
| Fax | Yalnızca rakam, geçerli faks formatı | **"Invalid fax number."** | "Invalid phone number format" |
| E-mail | Geçerli e-posta formatı | **"Email must be a valid email address."** | "Invalid email format" |
| Mobile / Home Phone | — | **"Invalid phone number."** | "Invalid phone number format" |

`ContactInfo.fax` alanı `MessageKeys.PHONE_INVALID` anahtarını kullanıyor. Kullanıcı hangi alanın hatalı olduğunu mesajdan ayırt edemiyor.

### Düzeltme

> **Karar verildi (analiz):** Faks için ayrı mesaj kullanılacak — doküman doğru.

1. `MessageKeys`'e yeni sabit:
   ```java
   public static final String FAX_INVALID = "validation.fax.invalid";
   ```
2. `messages.properties`, `messages_en.properties` ve `messages_tr.properties` dosyalarına:
   ```properties
   validation.fax.invalid=Invalid fax number.
   ```
3. `ContactInfo.fax` alanı bu anahtarı kullansın:
   ```java
   @Pattern(regexp = "^[0-9]{10,11}$", message = "{" + MessageKeys.FAX_INVALID + "}")
   String fax
   ```

**Düzeltme sonrası:** `TC-006-23` otomatik olarak `[GAP KAPANDI]` etiketiyle yeşile döner.

**E-posta metni ayrı bir karar bekliyor.** Doküman *"Email must be a valid email address."* diyor, API *"Invalid email format"* dönüyor. Faks için verilen karar e-postayı kapsamıyor; `TC-006-10` bu ayrışmayı bekleyen boşluk olarak izlemeye devam ediyor. Aynı yönde karar verilirse `validation.email.invalid` değeri güncellenmesi yeterlidir.

---

# 🟡 B-14 — Fatura hesabı adresinde de `cityId` doğrulanmıyor

**Test:** `TC-008-20` (`[BEKLEYEN BOŞLUK]`)

```
POST /accounts  {"accountName":"A","accountDesc":"d",
                 "newAddress":{"cityId":999999,"streetName":"S","buildingName":"B","addressDesc":"D"}}
-> 201 ✅ hesap ve adres oluşturuldu (cityId=999999 ile)
```

Bu, sprint-5'te açılan **B-07'nin ikinci giriş yoludur**. Doküman City alanı için "listeden seçim" diyor; `AddressInfo.cityId` üzerinde yalnızca `@NotNull` var, değerin lookup-service'te tanımlı olup olmadığı kontrol edilmiyor.

Sonuç: veritabanında hiçbir şehre karşılık gelmeyen bir adres ve ona bağlı bir fatura hesabı oluşabiliyor. Fatura adresinin geçersiz olması, ekranda boş/bozuk şehir bilgisi olarak görünür.

**Düzeltme:** B-07 için yapılacak lookup doğrulaması `AddressBusinessRules` seviyesinde yapılırsa her iki yol (FR-005 adres ekleme ve FR-008/FR-010 fatura adresi) tek seferde kapanır.

---

# Henüz test edilemeyen kabul kriterleri

Bunlar **bulgu değil**, kapsam/bağımlılık notudur — sunumda ve izlenebilirlik matrisinde açıkça belirtilmektedir.

### 1. Bağlı ürün kontrolü etkisiz (FR-007 ACC-004, FR-011 ACC-004)

İki kabul kriteri de "fatura hesabı pasif olsa dahi bağlı ürünü varsa silinemez" diyor. Kural kodda **yazılı**:

```java
rules.ensureNoLinkedProducts(productGuard.hasLinkedProducts(account.getCustAcctId()));
```

Ancak guard'ın gerçeklemesi sabit `false` dönüyor:

```java
@Service
public class NoOpBillingAccountProductGuard implements BillingAccountProductGuard {
    @Override
    public boolean hasLinkedProducts(Long custAcctId) {
        return false;   // order-service entegrasyonu gelene kadar geçici stub
    }
}
```

Yani kural şu an **hiçbir zaman tetiklenmiyor.** Ürünlü bir hesabın silinmeye çalışıldığı senaryo doğrulanamıyor. `TC-007-06` ve `TC-011-10` bu boşluğu izliyor; guard order-service'e bağlandığında testler otomatik yeşile döner.

`order-service` ayakta ve `GET /api/v1/orders?custAcctId={id}` ucu çalışıyor (200 + dizi dönüyor) — entegrasyon için gereken uç mevcut.

### 2. Ürün detay modalı alanları API'de yok (FR-009 ACC-006, ACC-007)

ACC-007 modalda şu alanları istiyor: **Product Offer Name, Product Offer ID, Product Spec ID, Service Start Date, Prod Chars, Service Address.**

`order-service`'in `CustOrdItemResponse` kaydı yalnızca şunları taşıyor:

```java
public record CustOrdItemResponse(
    Long custOrdItemId, Long prodId, String prodName,
    Long cmpgId, String cmpgName, Long custAcctId) {}
```

Ürün detayı için ayrı bir uç da tanımlı değil. ACC-005 kolonları (Product ID, Product Name, Campaign Name, Campaign ID) karşılanıyor; ACC-006/ACC-007 **karşılanmıyor**. `TC-009-10` bunu izliyor.

---

# Ekler

## Koşum özeti

```
İstek        : 264 (setup 17 · FR klasörleri 245 · teardown 2)
Assertion    : 1157   ->  1153 geçti / 4 kaldı
Süre         : ~47 sn
Bekleyen boşluk (gapTest) : 27
```

4 hatanın tamamı **B-10**'dan geliyor (2 test × 2 assertion: biri testin kendi beklentisi, biri koleksiyon seviyesindeki "5xx dönmedi" ortak kontrolü).

Diğer bulgular (**B-11…B-14**) `gapTest` mekanizmasıyla `[BEKLEYEN BOŞLUK]` olarak raporlanıyor — koşumu kırmıyorlar ama Allure raporunda ve konsolda görünüyorlar. Düzeltildiklerinde otomatik olarak `[GAP KAPANDI]` etiketine dönerler; ayrıca test değişikliği gerekmez.

## Testleri kendiniz koşmak için

```bash
cd analiz-test/postman/newman
npm install          # ilk seferde
npm run demo         # koşum + Allure raporu tarayıcıda açılır
```

Yalnızca yeni FR'ları koşmak için:

```bash
npx newman run ../CRM-Lite-FR001-FR011.postman_collection.json \
  -e ../environments/CRM-Lite.local.postman_environment.json \
  --folder "09 · FR-009 Fatura Hesabi ve Bagli Urun Goruntuleme" -r cli
```

> Klasör bazlı koşumda `00 · Ortam Hazirligi (Setup)` klasörünün de çalışması gerekir (fixture müşterileri orada oluşur).

## Düzeltme sonrası

Düzeltmeleri gönderdiğinizde haber verin; temiz ortamda yeniden koşup her bulgu için doğrulama sonucunu bu dosyaya ekleyeceğim — sprint-5'te olduğu gibi.
