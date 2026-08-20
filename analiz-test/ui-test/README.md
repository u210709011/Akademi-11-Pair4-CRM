# CRM Lite — UI Test Otomasyonu

Selenium tabanli UI test otomasyonu. Kapsam: **FR-001 … FR-005**
(Sistem Girisi, Musteri Arama, Musteri Olusturma, Musteri Guncelleme, Adres Yonetimi).

Gereksinim kaynagi: `../CRM Lite — Functional Requirements & Acceptance Criteria1 (2).docx`
Orta katman (API) testleri icin bkz. `../postman/`.

---

## Durum

| Faz | Icerik | Durum |
|---|---|---|
| 0 | Mimari + test stratejisi + senaryo listesi | ✅ Tamam |
| 1 | Cerceve iskeleti (driver / config / wait / listener / rapor) | ✅ Tamam |
| 2 | Page Object & Component Object'ler | ✅ Tamam |
| **3** | **Test verisi katmani (model / builder / API arranger)** | **✅ Tamam** |
| 4 | FR-001…FR-005 test siniflari | ✅ Tamam |
| **5** | **CI/CD (GitHub Actions) + Allure yayini** | **✅ Tamam** |

Faz 1–3'te **hicbir gereksinim test edilmez**; yalnizca cercevenin ayakta oldugu,
locator'larin gecerli oldugu ve veri katmaninin dogru calistigi kanitlanir.
Gercek senaryolar Faz 4'te gelir.

### Faz 4 — suite durumu

| Suite | Sonuc | Not |
|---|---|---|
| FR-001 Sistem Girisi | Tamamlanmis kabul | Talep uzerine uzerinde calisilmiyor. Logout kapsam disi, hesap kilidi karantinada. |
| FR-002 Musteri Arama | ✅ 21/21 | |
| FR-003 Musteri Olusturma | ✅ 31/31 | |
| FR-004 Musteri Guncelleme | ✅ 14/14 | |
| FR-005 Adres Yonetimi | ✅ 14/14 | |

**Toplam: 59 otomatik senaryo yesil** (FR-002…FR-005), FR-001 tamamlanmis kabul.

> ℹ️ **Cozulen kararsizlik.** Iki ayri kosuda (FR-003 ve FR-005) Chrome/chromedriver
> cokup surefire JVM'i **testler arasinda** asili birakti (11 dk ve 20 dk hicbir cikti yok).
> `RetryAnalyzer` bunu yakalayamaz — asilma test icinde degil, driver kapatma/acma adiminda.
> Cozum: WebDriver HTTP istemcisine komut zaman asimi verildi (`timeout.command`, varsayilan
> 120 sn) — olu oturum artik sonsuza kadar beklemek yerine acik bir hata firlatir ve suite ilerler.
> Uzun kosularda tekrar gozlenirse deger dusurulebilir.

> Suite'ler bugune kadar **tek tek ve `parallel="false"`** ile calistirildi.
> `regression.xml` paralel calisacak sekilde tanimlidir ancak bu **henuz dogrulanmadi**
> (bkz. dosya icindeki not).

## Test verisi katmani (Faz 3)

```
data/
├── model/      IndividualInfo, AddressInfo, AddressResponse, ContactInfo,
│               CustomerData, CreatedCustomer, SearchCriteria, Gender, ValidationCase
├── builder/    CustomerBuilder, AddressBuilder, TestRunId
├── api/        ApiClient, AuthApi, CustomerApi, AddressApi,
│               TestDataFactory, TestDataCleaner
├── provider/   ValidationDataProvider
└── ExpectedMessages
```

**Kullanim (Faz 4'te):**

```java
CreatedCustomer customer = TestDataFactory.simpleCustomer();        // FR-002 / FR-004
CreatedCustomer full     = TestDataFactory.customerAtAddressLimit();// FR-005 ACC-005
var dataSet              = TestDataFactory.searchDataSet(12);       // FR-002 ACC-007/008

CustomerData invalid = CustomerBuilder.aValidCustomer().withoutLastName().build();
CustomerData future  = CustomerBuilder.aValidCustomer().withFutureBirthDate().build();
```

**Ilkeler:**

| Ilke | Uygulanis |
|---|---|
| Testler bagimsiz | Her test kendi verisini kurar; hicbiri baskasinin biraktigina guvenmez |
| Benzersizlik | `NationalIdGenerator` + `TestRunId` eki; paralel ve tekrarli kosumlar catismaz |
| API yalnizca kurulum icin | `data/api` hicbir assertion icermez; dogrulama her zaman UI'da |
| Sinir degerleri veriden gelir | Yeni bir vaka icin **kod degil**, `testdata/*.json` guncellenir |
| Beklenen metin tek kaynakta | `expected/messages_en.properties` — celiskiler tek satirla cozulur |
| Artik sessizce yutulmaz | Silinemeyen kayitlar loglanir **ve** Allure'a eklenir |

### Temizlik ve "artik" raporu

`TestDataCleaner`, suite sonunda olusturulan musterileri soft-delete eder.
Fatura hesabi acilmis musteriler silinemez (409) ve hesabi pasiflestiren bir API ucu
yoktur — bu **bilinen bir urun boslugudur** (izlenebilirlik matrisi, bosluk #3).
Bu kayitlar "artik" olarak raporlanir; `runId` sayesinde hangi kosumdan kaldiklari
veritabaninda da izlenebilir.

Temizligi kapatmak icin: `./mvnw test -Dcleanup=false`
API istek/yanit dokumu icin: `./mvnw test -Dapi.log=true`

## Page Object haritasi (Faz 2)

| Sinif | Kapsam |
|---|---|
| `auth/LoginPage` | FR-001 — giris, sifre goz ikonu, hata mesaji |
| `customer/SearchCustomerPage` | FR-002 — 7 filtre alani, Search/Clear, bos durum |
| `customer/create/CreateCustomerPage` | FR-003 — sihirbaz kabugu, sekme kilidi, Next/Create |
| `customer/create/DemographicStep` | FR-003 1. adim |
| `customer/create/AddressStep` | FR-003 2. adim (5 adres limiti) |
| `customer/create/ContactStep` | FR-003 3. adim |
| `customer/CustomerDetailPage` | FR-004 girisi + FR-005'in tamami |
| `customer/UpdateCustomerPage` | FR-004 — guncelleme formu |
| `components/NavbarComponent` | Dil secici, profil menusu, Logout |
| `components/SidebarComponent` | B2C/B2B/Approvals, Logout |
| `components/ResultsTableComponent` | FR-002 sonuc tablosu, kolonlar, siralama |
| `components/PaginationComponent` | FR-002 ACC-007 sayfalama |
| `components/AddressModalComponent` | FR-003 + FR-005 adres modal'i (iki id setini destekler) |
| `components/AddressCardComponent` | FR-005 adres karti ve uc-nokta menusu |
| `components/ConfirmDialogComponent` | Silme onay diyaloglari |

17 sinif, **116 statik locator**. Hepsi `LocatorSanityTest` ile dogrulanir.

---

## Gereksinimler

- **JDK 21+** (gelistirme ortaminda JDK 25 ile dogrulandi)
- **Chrome / Firefox / Edge** — driver binary'leri Selenium Manager tarafindan otomatik indirilir
- Maven kurulumu **gerekmez**; `mvnw` sarmalayicisi kullanilir

---

## Calistirma

```bash
# Varsayilan: smoke suite, local ortam, Chrome (headed)
./mvnw test

# Headless
./mvnw test -Dheadless=true

# Suite secimi: smoke | regression | quarantine
./mvnw test -Dsuite=regression

# Ortam ve tarayici
./mvnw test -Denv=test -Dbrowser=firefox -Dheadless=true

# Tek sinif
./mvnw test -Dtest=SmokeSelfCheckTest
```

### Allure raporu

```bash
./mvnw test
./mvnw allure:report      # target/site/allure-maven-plugin
./mvnw allure:serve       # tarayicida acar
```

---

## Konfigurasyon

Oncelik zinciri (ustteki alttakini ezer):

1. `-Dkey=value` komut satiri parametresi
2. `CRM_UI_USERNAME` / `CRM_UI_PASSWORD` ortam degiskenleri
3. `src/test/resources/config/{env}.properties`
4. `src/test/resources/config/config.properties`
5. `FrameworkConfig` icindeki `@DefaultValue`

Onemli ayarlar:

| Anahtar | Varsayilan | Aciklama |
|---|---|---|
| `env` | `local` | Ortam adi; `{env}.properties` dosyasini secer |
| `base.url` | `http://localhost:4200` | UI adresi (sonunda `/` olmamali) |
| `api.base.url` | `http://localhost:8080` | API gateway — yalnizca veri hazirlama icin |
| `browser` | `chrome` | `chrome` \| `firefox` \| `edge` |
| `headless` | `false` | CI'da `true` |
| `remote.url` | *(bos)* | Doluysa Selenium Grid / Docker kullanilir |
| `timeout.explicit` | `15` | Saniye |
| `timeout.multiplier` | `1.0` | Yavas CI makinelerinde `1.5`–`2.0` |
| `ui.language` | `en` | UI dili (localStorage `crm-lite-lang`) |
| `retry.count` | `1` | Yalnizca altyapi hatalarinda |
| `screenshot.mode` | `failure` | `all` \| `failure` \| `off` |

> **Kimlik bilgileri repoya yazilmaz.** `local.properties` yalnizca dokumandaki demo
> kullaniciyi (`salesperson`) icerir. Paylasimli ortamlarda `CRM_UI_USERNAME` /
> `CRM_UI_PASSWORD` ortam degiskenleri kullanilmalidir.

---

## Mimari — kisa ozet

```
Test katmani     →  TestNG sinifları, yalnizca is dili + assertion
Page katmani     →  Page Object + Component Object (locator/eylem, assertion YOK)
Core katmani     →  driver / config / wait / listener / report / log / exception
Data katmani     →  model + builder + DataProvider + API arranger (Faz 3)
```

Bagimlilik yonu tek yonludur: **Test → Page → Core**.

### Bilincli tasarim kararlari

| Karar | Gerekce |
|---|---|
| **Implicit wait = 0** | Implicit + explicit karisimi ongorulemez bekleme sureleri ve yaniltici hata mesajlari uretir. Tum beklemeler `WaitFactory` uzerinden explicit. |
| **PageFactory (`@FindBy`) kullanilmaz** | Angular DOM'u yeniden olusturdugunda proxy'ler `StaleElementReferenceException` uretiyor. `By` sabitleri ile cagri aninda cozumleme yapilir. |
| **`AppConditions` ile ozel bekleme kosullari** | Uygulama Angular **Signal Forms** kullaniyor; buton `[disabled]` durumu DOM'a senkron yansimiyor. "Buton aktif olmalidir/olmamalidir" ACC'leri standart `ExpectedConditions` ile guvenilir test edilemez. |
| **Negatif kontrollerde `remainsDisabled`** | "Buton aktif olmamalidir" anlik kontrol edilirse, Angular henuz render etmemisken **yanlis-gecer** uretir. Kisa sure gozlemlenir. |
| **AspectJ weaver yok** | Allure `@Step`/`@Attachment` yerine programatik API (`Allure.step`, `Allure.addAttachment`) kullanilir. JDK surumu ile AspectJ arasindaki uyum riski tamamen ortadan kalkar. |
| **`AssertionError` yeniden denenmez** | Gercek urun hatasini tekrar deneyerek yesile boyamak en tehlikeli anti-pattern'dir. Yalnizca `TimeoutException`/`StaleElement` gibi altyapi hatalari 1 kez denenir ve raporda "retried" gorunur. |
| **UI dili sabitlenir** | Front-end varsayilani `en`, FR-018 ise Turkce diyor. Dil acikca pinlenmezse metin assertion'lari makineye gore degisir. |
| **CDP uyarilari susturulur** | Chrome, Selenium'dan hizli guncelleniyor. Cerceve DevTools kullanmadigi icin surum pesinde kosmak yerine ilgili JUL logger susturulur. |
| **Her etkilesim kendi DOM olayini uretir** | Angular Signal Forms modelini yalnizca gercek DOM olaylarindan gunceller. Ayrintili aciklama asagida. |

### Signal Forms ve DOM olaylari — Faz 4'un en pahali dersi

Bu uygulamada **Selenium'un standart etkilesimleri tek basina yetmiyor.** Signal Forms
modeli sessizce senkronize olmadigi icin, DOM dogru gorunurken form gecersiz kalabiliyor.
Belirti her seferinde ayni ve yaniltici: **hicbir hata mesaji yok, sadece Save/Next pasif kaliyor.**

Dort ayri varyant cikti; dordu de `BasePage` icinde tek noktada cozuldu:

| # | Etkilesim | Selenium'un yaptigi | Eksik olan | Cozum |
|---|---|---|---|---|
| 1 | `<select>` secimi | `value`'yu ayarlar | `input` + `change` | `selectByValue` / `selectByVisibleText` icinde olay gonderimi |
| 2 | Alani bosaltma | `clear()` | `input` (yalniz `change` uretir) | `type()` bos deger yolunda olay gonderimi |
| 3 | Hata mesajini gorunur kilma | — | `blur` (mesajlar `touched()` kosuluna bagli) | Sayfa nesnelerinde `blurField()` |
| 4 | **Dolu alanin uzerine yazma** | `clear()` + `sendKeys` | `clear()` bazi bilesenlerde **hic ise yaramaz** | `clearField()` — once `clear()`, ise yaramazsa `Ctrl+A`+`DELETE` |

**4. madde en sinsi olani.** `MatDatepickerInput` modelini `input` olayindan gunceller;
`clear()` bu olayi uretmedigi icin model eski tarihte kalir ve Angular bir sonraki
degisiklik algilama turunda bicimlenmis eski degeri DOM'a **geri yazar**. Ardindan
`sendKeys` yeni metni eskisinin sonuna ekler:

```
"15 / 06 / 1990" + "01/01/1900"  ->  "15 / 06 / 199001/01/1900"
```

`clearField()` once yine `clear()` dener; bu bilincli bir tercihtir — halihazirda
calisan alanlarin davranisi degismesin diye. Yalnizca deger kaldiysa klavyeye duser.
Klavye ile silme telefon alanlarindaki `blockNonDigitInput` engelleyicisini tetiklemez,
cunku silme olaylarinda `event.data === null`.

---

## Hata aninda toplanan deliller

`TestListener`, bir test basarisiz oldugunda **driver kapatilmadan once** su uc delili toplar:

1. **Ekran goruntusu** — Allure eki + `target/screenshots/`
2. **Sayfa kaynagi (HTML)** — Allure eki
3. **Tarayici console log'u** — Angular'daki sessiz JS hatalarini ortaya cikarir

Loglar: konsol + `target/logs/automation.log` (MDC'de `testName` ile korelasyonlu).

---

## Suite'ler

| Suite | Amac | CI |
|---|---|---|
| `smoke` | PR dogrulamasi, P1 senaryolar | Her PR |
| `regression` | FR-001…FR-005 tamami | `main` merge + nightly |
| `quarantine` | **FR-001 ACC-008 hesap kilitleme** | ❌ **Asla** |

> ⚠ **Karantina uyarisi:** FR-001 ACC-008, Keycloak brute-force korumasini tetikler
> (`failureFactor=5`, `waitIncrementSeconds=900`) ve kullaniciyi **15 dakika kilitler**.
> Paylasilan `salesperson` hesabiyla calistirilirsa sonraki tum testler bloke olur.
> Ayri, adanmis bir kullanici ile ve yalnizca manuel tetikle calistirilmalidir:
> `./mvnw test -Dsuite=quarantine -Dui.username=<adanmis> -Dui.password=<...>`

---

## CI/CD (Faz 5)

Iki is akisi: `.github/workflows/ui-test-pr.yml` ve `ui-test-nightly.yml`.

| | PR Check | Nightly Regression |
|---|---|---|
| Tetik | `analiz-test/ui-test/**` veya `front-end/**` degisen PR | Her gun 01:00 UTC + elle |
| Yapar | Derleme + suite XML dogrulamasi | Tam stack + 59 senaryo (headless) |
| Sure | ~2 dk | ~35 dk (stack ~10 + testler ~25) |
| Tarayici | Yok | Chrome (headless) |

**PR'da neden test kosmuyor?** Uctan uca kosu 9 JVM + Docker altyapisi gerektiriyor.
Her PR'da bunu beklemek gelistirmeyi yavaslatir; dahasi altyapi kaynakli fla-kilik,
gercek kod hatalarinin uzerini orter. PR sinyali hizli ve deterministik tutuldu.

**Suite XML dogrulamasi** PR job'inin ikinci adimi. Sebep gercek bir risk: suite
dosyalarindaki sinif adlari elle yazilir, dolayisiyla bir sinif yeniden
adlandirildiginda **derleme gecer ama suite sessizce bosalir**. Adim her
`<class name="..."/>` girdisinin karsiligi olan `.class` dosyasini arar.

**Nightly'de dikkat edilenler:**
- `shared-*` modulleri once install edilir (yoksa 6 servis "Could not find artifact" verir)
- `config-server` hazir olmadan domain servisleri baslatilmaz (acilista olurlar)
- Kafka healthcheck yanlis negatifine karsi `debezium`/`kafka-ui` ayrica `--no-deps` ile kaldirilir
- Testlerden **once** login -> gateway -> customer-service zinciri dogrulanir; 25 dk kosup
  ortamin bozuk oldugunu ogrenmek yerine dakikalar icinde hata verilir
- Testler kirmizi olsa bile Allure raporu ve deliller yayinlanir; basarisizlik
  **rapor yayinlandiktan sonra** yansitilir

**Rapor:** Allure HTML raporu GitHub Pages'e yayinlanir (trend grafikleri icin gecmis tasinir).
Ekran goruntuleri, log'lar ve surefire raporlari ayrica artifact olarak yuklenir.

> ⚙️ **Depo ayari gerekir:** Settings → Pages → Source = "GitHub Actions".
> Ayrica `salesperson` kimlik bilgileri paylasilan ortamda `CRM_UI_USERNAME` /
> `CRM_UI_PASSWORD` secret'lari ile verilmelidir.

---

## Bilinen kapsam disi maddeler

| ACC | Neden otomatize edilmiyor |
|---|---|
| **FR-001 ACC-012/013 (Logout)** | ⛔ **UI'da uygulanmamis.** Hem `navbar .profile-option.logout` hem `sidebar .nav-item.logout` butonlarinda `(click)` baglayicisi yok; `AuthService` icinde `logout()` metodu bulunmuyor (yalnizca hicbir yerden cagrilmayan bir `clearSession()` var). Locator'lar `NavbarComponent` icinde hazir; ozellik gelistirildiginde testler calisir hale gelir. |
| FR-003 ACC-006/007, FR-004 ACC-008/009 | `FakeIdentityVerificationServiceImpl` her kimligi dogruluyor; **KPS basarisizligi uretilemiyor**. Back-end'e bir test hook'u eklenirse 4 ACC otomasyona girer. |
| FR-001 ACC-008 | 15 dakikalik hesap kilidi (yukariya bkz.) — karantina. |
| FR-001 ACC-011 | 8 saatlik oturum suresi beklenemez; yalnizca yonlendirme davranisi kismi olarak dogrulanabilir. |
| FR-001 ACC-005 (mesaj konumu), FR-002 ACC-011 (buton kose konumu) | Gorsel yerlesim kriterleri — manuel. |

### Urun bulgulari (otomasyon hatasi degil)

Testler kosarken ortaya cikan, **urun ekibine iletilmesi gereken** maddeler:

| # | Bulgu | Etki | Testlerdeki durum |
|---|---|---|---|
| 1 | **Datepicker `parse()` ezilmemis.** `AppDateAdapter` (`app.config.ts`) yalnizca `format()` metodunu eziyor. `NativeDateAdapter.parse()` yazilan metni hala `Date.parse()` ile, yani **MM/DD/YYYY** olarak cozumluyor. | ⚠️ **Kullanici etkisi var:** placeholder "DD/MM/YYYY" dedigi halde, kullanici bu formatta yazdiginda tarih gecersiz sayiliyor ve Next/Save aktiflesmiyor. | `DateUtil.toDatepickerInput()` gun/ay yerini degistirerek hatayi tek noktada soğuruyor. **`parse()` duzeltilince bu metot silinmelidir.** |
| 2 | **Tarih gosteriminde ayirac bosluklari.** `format()` `` `${day} / ${month} / ${year}` `` uretiyor -> `15 / 06 / 1990`. FR-004 ACC-002 formati `DD/MM/YYYY` olarak tanimliyor. | Kozmetik. Gun-once sira ve sifir dolgusu dogru. | Assertion bosluk toleransli (`\d{2}\s*/\s*\d{2}\s*/\s*\d{4}`); sira veya dolgu bozulursa test yine yakalar. |
| 3 | **"Set as Primary" sonrasi gecici olarak IKI adres birden "Primary" gorunuyor.** Eski adresin bayragi aninda kalkmiyor; bu araliktaki bir sayfa yuklemesinde `GET /addresses` iki birincil adres donuyor ve arayuz iki karti da Primary rozetiyle ciziyor. Hata anindaki ekran goruntusuyle dogrulandi. | ⚠️ **Kullanici etkisi var:** kisa sure yanlis bilgi gosteriliyor. Son durum dogru — API dogrudan cagrildiginda (senkron) sorun hic olusmuyor, yaris yalnizca UI akisinda. | Test, `AddressApi.awaitSinglePrimary()` ile sunucu yerlesene kadar senkronize olur; **assertion gevsetilmedi**. Kalici bir cift-birincil durumu olusursa 15 sn sonra acikca patlar. |

### Gereksinimde karsiligi olmayan senaryo (duzeltildi)

FR-004 icin yazdigim `genderIsRequired` testi, gender alaninin bosaltilabildigini
varsayiyordu. Guncelleme ekraninda gender **her zaman dolu gelir** ve placeholder
secenegi `<option value="" disabled>` oldugu icin **kullanici gender'i hicbir sekilde
bosaltamaz** — yani "gender bos -> Save pasif" durumu UI uzerinden hic olusturulamaz.
Test `genderCannotBeCleared` olarak yeniden yazildi ve zorunlulugun fiilen nasil
uygulandigini dogruluyor. Bu bir urun hatasi degil, **senaryo varsayiminin hatasiydi**.

### Dokumanda tespit edilen celiskiler

Bu maddelerde **metin yerine davranis** assert edilir; beklenen metinler tek noktada
(`src/test/resources/expected/`) tutulur ve karar netlestiginde tek satirla guncellenir.

1. FR-002 ACC-010 metni ile UC-002 Adim 6.2 metni farkli
2. FR-004 ACC-005 "Cancel" diyor; UC-004 Alt-4 ve kod "Previous"
3. Home Phone kurali FR-003 ("10 hane, 2 ile baslar") ile FR-006 ("10–11 hane") celisiyor
4. FR-018 varsayilan dili Turkce diyor; kod `en` ile basliyor

---

## Faz 1 kabul kriteri

```bash
./mvnw test -Dsuite=smoke -Dheadless=true
```

`SmokeSelfCheckTest` iki testten olusur:

- `driverBootstrapWorks` — **uygulama gerektirmez.** Konfigurasyonun dogrulanarak
  yuklendigini, WebDriver'in ayakta oldugunu ve bekleme altyapisinin calistigini kanitlar.
- `applicationIsReachable` — uygulama kapaliysa **hata vermez, ATLANIR** (`SkipException`).
  Bu bir urun hatasi degil, ortam durumudur ve gercek hata sayisini kirletmemelidir.

Uygulamanin da dogrulanmasi icin:

```bash
# 1) Altyapi
cd ../../back-end/infra && docker compose up -d
# 2) Back-end servisleri (en az: discovery, config, gateway, customer-service)
# 3) Front-end
cd ../../front-end && npm install && npm start      # http://localhost:4200
# 4) Testler
cd ../analiz-test/ui-test && ./mvnw test
```
