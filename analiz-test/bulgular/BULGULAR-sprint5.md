# Bulgu Raporu — sprint-5 (test/integration)

| | |
|---|---|
| Tarih | 05.08.2026 |
| Branch / commit | `test/integration` · `cd8e2de` |
| Yöntem | Postman/Newman API test takımı (FR-001…FR-005, 125 test / 617 assertion) + servis loglarından kök neden analizi |
| Ortam | Tüm servisler temiz başlatıldı (config, discovery, gateway, lookup, party, contact-info, customer, order) |
| Sonuç | **603 geçti / 14 kaldı** — 14 hatanın tamamı aşağıdaki 5 bulgudan kaynaklanıyor |

Her bulgu için **kök neden servis loglarından teyit edilmiştir** (tahmin değildir); stack trace alıntıları eklenmiştir.

---

# 🟢 SON DURUM — 05.08.2026 (commit `05c6c3f`) · TÜM BULGULAR KAPANDI

`c2ec78d fix: role always null on customer search view + B-03 nested cause chain` ile son iki konu da kapandı. Temiz ortamda koşum:

| Koşum | Assertion | Hata | Allure |
|---|---|---|---|
| İlk tespit (`cd8e2de`) | 617 | 14 | 136/150 |
| 1. düzeltme turu (`ab6db1e`) | 617 | 2 | 149/150 |
| **2. düzeltme turu (`05c6c3f`)** | **618** | **0** | **150/150** ✅ |

| Bulgu | Son durum |
|---|---|
| B-01 Giriş 500 | ✅ Çözüldü |
| B-03 Downstream iş hataları | ✅ **Çözüldü** — `findKnownCause` zinciri derinlik sınırıyla dolaşıyor. Doğrulandı: `PUT /customers/{id}/individual` (başka müşterinin T.C. no'su) → `409 "A customer with this national ID already exists."` |
| B-06 Hesap no çakışması | ✅ Çözüldü |
| B-08 Adres silme | ✅ Çözüldü |
| B-09 Tip hatası 500 | ✅ Çözüldü |

## Ekibin kendi bulduğu ek hata: `role` alanı null kalıyordu

Bu bulguyu ekip kendi tespit etti (`c2ec78d`): arama görünümündeki `role` alanı yalnızca async Kafka olayı (`IndividualPartyCreated`) ile dolduruluyordu. Olay kaybolur ya da lookup çağrısı 4 denemeden sonra DLQ'ya düşerse **hiçbir hata görünmeden** alan kalıcı olarak null kalıyordu. Artık onboarding sırasında senkron yazılıyor.

**Test tarafındaki eksiğimiz:** `TC-002-01` bu hatayı yakalayamamıştı — alanın *varlığını* kontrol ediyordu ama *dolu olduğunu* değil. Test güçlendirildi:

```
TC-002-01 · Zorunlu sonuc alanlari bos/null degil
```

Artık `custId`, `firstName`, `lastName`, `role`, `tcNo` alanlarının hiçbiri `null`/boş olamaz (`middleName` opsiyonel olduğu için hariç). Aynı hata tekrarlanırsa test kırmızıya döner. Toplam assertion 617 → 618.

---

# ✅ 1. DOĞRULAMA TURU — 05.08.2026 (commit `ab6db1e`)

Ekip `2ed3fd9 fix: QA-reported bugs B-03/B-06/B-08/B-09` commit'i ile düzeltmeleri gönderdi. Temiz ortamda (Keycloak realm yeniden import edildi, tüm servisler yeniden derlenip başlatıldı) test takımı tekrar koşuldu.

| Koşum | Assertion | Hata | Allure test case |
|---|---|---|---|
| Düzeltme öncesi | 617 | **14** | 150 / 136 geçti |
| **Düzeltme sonrası** | 617 | **2** | **150 / 149 geçti** |

| Bulgu | Durum | Doğrulama |
|---|---|---|
| **B-08** Adres silme | ✅ **ÇÖZÜLDÜ** | M2M servis hesaplarına `CRM_AGENT` rolü verilmiş. Token doğrulandı, adres silme 204 dönüyor, `TC-005-07`/`TC-005-08` yeşil |
| **B-01** Giriş 500 | ✅ **ÇÖZÜLDÜ** | Config'e `spring.messages.basename: messages/messages` eklenmiş. Hatalı giriş artık `401 "Invalid credentials or token."` dönüyor; 4 test yeşil |
| **B-06** Hesap no çakışması | ✅ **ÇÖZÜLDÜ** | `AccountNumberGenerator` ile her iki yol da `custAcctId` kullanıyor — yapısal olarak çakışamaz. Senaryo ayrıca elle doğrulandı: 3 fatura hesabı açıldıktan sonra 6 ardışık müşteri oluşturma, hepsi başarılı (eskiden burada 502 alınıyordu) |
| **B-09** Tip hatası 500 | ✅ **ÇÖZÜLDÜ** | `?custId=abc` artık 400 dönüyor |
| **B-03** Downstream iş hataları | ⚠️ **KISMEN** | 500 → 502 oldu ama **409 hâlâ istemciye ulaşmıyor** — aşağıya bakınız |

## ⚠️ B-03 için kalan tek sorun: cause zinciri tek katman açılıyor

Yazılan `handleNoFallbackAvailable` mantığı **doğru**, ancak sarmalama beklenenden bir katman daha derin:

```
NoFallbackAvailableException
  └── java.util.concurrent.ExecutionException      ← ex.getCause() burayı görüyor
        └── feign.FeignException$Conflict [409]    ← asıl hata bir katman daha altta
```

Handler `ex.getCause() instanceof FeignException` kontrolünü yapıyor, cause `ExecutionException` olduğu için eşleşmiyor ve genel 502 dalına düşüyor. Servisin kendi log satırı da bunu doğruluyor:

```
AbstractDownstreamExceptionHandler : NoFallbackAvailableException beklenmeyen bir cause ile geldi:
java.util.concurrent.ExecutionException: feign.FeignException$Conflict: [409] during [PUT] to
[http://party-service/api/v1/individuals/by-party-role/30]
[{"status":409,"message":"A customer with this national ID already exists."}]
```

**Sonuç:** `PUT /customers/{id}/individual` (başka müşterinin T.C. no'su ile) → `502 "A dependent service call failed."` (beklenen: `409 "A customer with this national ID already exists."`). FR-004 ACC-006/ACC-007 hâlâ karşılanmıyor.

**Önerilen düzeltme:** cause zincirini tek katman değil, `FeignException` bulunana kadar dolaşmak:

```java
private static Throwable unwrap(Throwable t) {
    Throwable current = t;
    while (current != null
            && !(current instanceof FeignException)
            && !(current instanceof CallNotPermittedException)) {
        current = current.getCause();
    }
    return current;
}
```

`handleNoFallbackAvailable` içinde `ex.getCause()` yerine `unwrap(ex)` kullanılması yeterli. (`ExecutionException` dışında `CompletionException` gibi sarmalayıcılar da olabileceği için döngü, tekil `instanceof ExecutionException` kontrolünden daha güvenli.)

Bu düzeltilmeden FR-006…FR-011'de downstream'in döndüğü iş hataları (adres limiti 409, adres bulunamadı 404, iletişim formatı 400) istemciye ayırt edilemez şekilde 502 olarak ulaşmaya devam eder.

## Ek gözlem — dağıtım notu

`crm-realm.json` değişikliği (M2M rolleri) çalışan Keycloak'a **kendiliğinden yansımaz**; Keycloak realm'i yalnızca ilk açılışta import eder. Doğrulama sırasında container yeniden oluşturuldu:

```bash
docker compose up -d --force-recreate keycloak
```

Keycloak'ın kalıcı volume'ü olmadığı için bu işlem Postgres verisine dokunmaz. Ekip arkadaşları da düzeltmeyi test ederken bu adımı atlamamalı — aksi halde B-08 hâlâ açıkmış gibi görünür.

---

## B-08 · Adres silme tamamen çalışmıyor 🔴 KRİTİK · YENİ

**Belirti**
```
DELETE /api/v1/customers/{custId}/addresses/{addressId}  →  500
{"status":500,"error":"Internal Server Error","message":"Unexpected error occurred"}
```
Birincil olmayan, hiçbir fatura hesabına bağlı olmayan bir adres bile silinemiyor. **FR-005 ACC-008/ACC-012 çalışmıyor.** Deterministik, her seferinde tekrarlanıyor.

**Kök neden**

`contact-info-service`, adresi silmeden önce "bu adres bir fatura hesabına bağlı mı" diye `customer-service`'e soruyor:

```java
// AddressServiceImpl.java:94
addressBusinessRules.ensureNotLinkedToAccount(customerAccountClient.existsByAddressId(id));
```

Bu çağrı **yetki hatası** alıyor:

```
customer-service log:
org.springframework.security.authorization.AuthorizationDeniedException: Access Denied
    at CustomerAccountController$$SpringCGLIB$$0.existsAccountByAddress(<generated>)
```

Sebebi: `CustomerAccountController` sınıf seviyesinde `@PreAuthorize("hasRole('CRM_AGENT')")` taşıyor ve bu, servisler arası çağrı için açılmış `exists-by-address` ucunu da kapsıyor. Ancak `contact-info-service`'in makine (M2M) token'ında bu rol yok:

```
m2m client   : contact-info-service-m2m
realm rolleri: ["offline_access", "default-roles-crm", "uma_authorization"]
CRM_AGENT    : YOK
```

**Neden 403 değil de 500 görüyoruz:** `AuthorizationDeniedException` customer-service'in `GlobalExceptionHandler`'ında ele alınmıyor → 500. Ardından zincir katlanarak büyüyor:

```
1. customer-service   exists-by-address  →  500 (Access Denied)
2. contact-info       Feign 500 alır → CircuitBreaker fallback yok → NoFallbackAvailableException → 500
3. customer-service   Feign 500 alır → CircuitBreaker fallback yok → NoFallbackAvailableException → 500
4. istemci            500 "Unexpected error occurred"
```

**Önerilen çözüm** — biri yeterli:
- `exists-by-address` ve `by-address` uçlarını sınıf seviyesindeki `@PreAuthorize`'dan muaf tutmak (metot seviyesinde `@PreAuthorize("permitAll()")` ya da ayrı bir controller'a taşımak), **veya**
- `contact-info-service-m2m` servis hesabına gerekli rolü vermek (realm import dosyasında service account role mapping)

İlk seçenek daha doğru: bu uç kullanıcı değil, servis içindir.

---

## B-01 · Hatalı giriş 401 yerine 500 dönüyor 🔴 YÜKSEK · KÖK NEDEN BULUNDU

**Belirti**
```
POST /api/v1/auth/login   (hatalı parola)  →  500   (beklenen: 401)
POST /api/v1/auth/refresh (geçersiz token) →  500   (beklenen: 401)
```
**FR-001 ACC-005/ACC-007 karşılanmıyor** — front-end "Wrong user name or password" mesajını üretemiyor, kullanıcıya sistem hatası görünüyor.

**Kök neden — iyi haber: mantık zaten doğru, eksik olan tek bir konfigürasyon satırı**

Sprint-5'te eklenen `AuthExceptionHandler` doğru çalışıyor; `InvalidCredentialsException` → 401, `AccountLockedException` → 423 eşlemeleri yerinde. Ancak mesajı çözerken patlıyor:

```
api-gateway log:
Failure in @ExceptionHandler AuthExceptionHandler#handleInvalidCredentials
org.springframework.context.NoSuchMessageException:
    No message found under code 'error.auth.invalid-credentials' for locale 'en_US'
    at AuthExceptionHandler.resolve(AuthExceptionHandler.java:51)
```

Anahtar **dosyada mevcut** (`api-gateway/src/main/resources/messages/messages.properties`), ama Spring onu bulamıyor: dosya `messages/` alt klasöründe, Spring'in varsayılan basename'i ise `messages`. Diğer servislerde bu ayar yapılmış, gateway'de unutulmuş:

| Servis | `spring.messages.basename` |
|---|---|
| customer-service | `messages/messages` ✔ |
| **api-gateway** | **tanımlı değil** ✘ |

Exception handler'ın kendisi patlayınca istek varsayılan 500 hatasına düşüyor.

**Önerilen çözüm** — config repo'sunda `configs/api-gateway/api-gateway.yml` dosyasına:

```yaml
spring:
  messages:
    basename: messages/messages
```

Bu tek satır 5 testi birden yeşile çevirir (`TC-001-04`, `TC-001-05`, `TC-001-10`, `TC-001-18` ve ortak 5xx kontrolü).

---

## B-03 · Downstream iş hataları 500'e dönüşüyor 🔴 KRİTİK · MİMARİ · YAYILAN ETKİ

**Belirti**
```
PUT /api/v1/customers/{id}/individual   (başka müşterinin T.C. no'su ile)  →  500
{"message":"Unexpected error occurred"}     (beklenen: 409 + açıklayıcı mesaj)
```
**FR-004 ACC-006/ACC-007 karşılanmıyor.**

**Kök neden**

`party-service` **doğru davranıyor** — 409 ve doğru mesajı dönüyor:

```
Caused by: feign.FeignException$Conflict: [409] during [PUT] to
  [http://party-service/api/v1/individuals/by-party-role/16]
  [{"status":409,"error":"Conflict","message":"A customer with this national ID already exists."}]
```

Ama `customer-service` bu 409'u istemciye taşıyamıyor:

```
org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException: No fallback available.
    at CustomerIndividualServiceImpl.updateIndividual(CustomerIndividualServiceImpl.java:42)
```

Feign istemcileri circuit breaker ile sarılmış (`spring.cloud.openfeign.circuitbreaker.enabled=true`) ama:
- Hiçbir Feign istemcisinde **fallback** tanımlı değil
- Projede **hiçbir yerde `ErrorDecoder` yok** (tüm modüllerde arandı)
- `NoFallbackAvailableException` hiçbir `GlobalExceptionHandler`'da ele alınmıyor

Sonuç: downstream servisin bilinçli olarak döndüğü **her iş hatası** (409, 404, 422) istemciye **500** olarak ulaşıyor.

**⚠ Sonraki FR'lar için en önemli uyarı bu**

Bu bir tek uca özgü hata değil, **genel bir desen**. FR-006…FR-011 kapsamındaki şu akışların hepsi aynı şekilde kırılacak:

| Akış | Downstream'in döndüğü | İstemcinin göreceği |
|---|---|---|
| FR-006 iletişim bilgisi güncelleme (geçersiz e-posta/telefon) | 400 | **500** |
| FR-008 fatura hesabı için yeni adres (5 adres limiti aşımı) | 409 | **500** |
| FR-010 fatura hesabı güncelleme (adres bulunamadı) | 404 | **500** |
| FR-011 hesap silme (bağlı adres/ürün kontrolleri) | 409 | **500** |

Yani bu düzeltilmeden FR-006…FR-011 testleri yazılırsa, **iş kurallarının doğru çalışıp çalışmadığı ölçülemez** — hepsi 500 döneceği için hata mesajları ayırt edilemez.

**Önerilen çözüm** — sırayla en temizden:
1. Ortak bir **`ErrorDecoder`** yazıp downstream 4xx'i domain istisnalarına çevirmek (409 → `DuplicateNationalIdException` gibi)
2. Ya da `GlobalExceptionHandler`'a `NoFallbackAvailableException` yakalayıcısı ekleyip `getCause()` zincirindeki `FeignException`'ın statüsünü ve mesajını istemciye taşımak
3. Ya da her Feign istemcisine anlamlı fallback tanımlamak

`order-service`'te downstream hatalarını statüsüyle forward eden bir `FeignException` yakalayıcısı zaten var — aynı desen customer-service ve contact-info-service'e de taşınabilir.

---

## B-06 · Hesap numarası çakışması 🔴 KRİTİK · HÂLÂ AÇIK

Bu bulgu önceki raporda iletilmişti, **düzeltilmemiş**. `existsByAccountNo` metodu repository'ye eklenmiş ancak **hiçbir yerde kullanılmıyor**.

**Kök neden (değişmedi)** — aynı `acct_no` unique kolonu iki farklı ID uzayından besleniyor:

```java
// CustomerOnboardingServiceImpl.java:124  — varsayılan hesap
account.setAccountNo(AccountDefaults.formatAccountNo(customer.getCustId()));     // cust_id

// BillingAccountServiceImpl.java:77       — fatura hesabı
account.setAccountNo(AccountDefaults.formatAccountNo(account.getCustAcctId()));  // cust_acct_id
```

`cust_acct_id` her zaman daha hızlı büyüdüğü için, `cust_id` o aralığa girdiğinde onboarding kırılıyor:

```
duplicate key value violates unique constraint "uq_cust_acct_no"
Detail: Key (acct_no)=(000062) already exists.
→ POST /api/v1/customers/onboarding  →  502 "Customer onboarding failed."
```

Kendiliğinden geçip tekrar başladığı için destek tarafında tekrar üretilemez.

**Test tarafına etkisi:** FR-008 (fatura hesabı oluşturma) testleri her koşumda fatura hesabı açacağı için bu çakışma sürekli tetiklenecek ve **FR-002…FR-005 testlerini de düşürecek**. FR-006…FR-011 genişletmesine başlamadan önce düzeltilmesi gerekiyor.

**Önerilen çözüm:** `acct_no` tek bir adanmış sequence'ten üretilmeli (`CREATE SEQUENCE acct_no_seq`) ya da tip önekiyle ayrıştırılmalı (`C000062` / `B000062`). Mevcut veri için migration gerekir.

---

## B-09 · Geçersiz tipte parametre 500 dönüyor 🟡 ORTA · YENİ

**Belirti**
```
GET /api/v1/customers/search?custId=abc  →  500   (beklenen: 400)
```
Önceki sürümde 400 dönüyordu, sprint-5 ile 500'e döndü.

**Kök neden**
```
customer-service log:
org.springframework.web.method.annotation.MethodArgumentTypeMismatchException:
    Method parameter 'custId': Failed to convert value of type 'java.lang.String'
    to required type 'java.lang.Long'; For input string: "abc"
```

`GlobalExceptionHandler`'da bu istisna için yakalayıcı yok, genel 500'e düşüyor.

**Önerilen çözüm:** `GlobalExceptionHandler`'a `MethodArgumentTypeMismatchException` → 400 eşlemesi eklemek.

---

# Özet ve öncelik önerisi

| # | Bulgu | Etki | Düzeltme maliyeti | Öncelik |
|---|---|---|---|---|
| **B-08** | Adres silme hiç çalışmıyor | FR-005 tamamen kırık | Düşük (yetki ayarı) | 🔴 1 |
| **B-03** | Downstream iş hataları 500'e dönüşüyor | FR-004 kırık; **FR-006…011'i ölçülemez hale getirir** | Orta (ErrorDecoder / handler) | 🔴 2 |
| **B-06** | Hesap no çakışması | Müşteri oluşturma aralıklarla duruyor; FR-008 testlerini engelliyor | Orta (sequence + migration) | 🔴 3 |
| **B-01** | Giriş hatası 500 | FR-001 kırık | **Çok düşük (tek config satırı)** | 🟠 4 |
| **B-09** | Tip hatası 500 | Kullanıcıya sistem hatası | Düşük (tek handler) | 🟡 5 |

**En hızlı kazanım:** B-01 tek satırlık config değişikliği ile kapanır ve 5 testi birden yeşile çevirir.

**FR-006…FR-011 çalışmasına başlamadan önce mutlaka:** B-03 ve B-06. Bu ikisi düzeltilmeden yazılacak testler, iş kurallarını değil altyapı hatalarını ölçer.

---

## Ek not — düzeltilmiş görünen konular

- Sprint-5 ile `PATCH /api/v1/customers/{custId}/accounts/{accountId}/status` ucu eklenmiş (`ACTIVE` ↔ `PASSIVE`). Bu, daha önce "test edilemez" olarak işaretlediğimiz **FR-011 mutlu yolunu (pasif hesabın silinebilmesi) artık test edilebilir hale getiriyor.**
- `contact-info-service`'e `messages_tr.properties` / `messages_en.properties` eklenmiş — FR-018 (dil desteği) için olumlu adım.
- Fatura hesabına bağlı ürün kontrolü hâlâ stub (`NoOpBillingAccountProductGuard` her zaman `false` dönüyor) → FR-007 ACC-004 ve FR-011 ACC-004 test edilemiyor.
