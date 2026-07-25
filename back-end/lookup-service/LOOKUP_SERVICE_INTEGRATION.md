# lookup-service Entegrasyon Rehberi

Bu dokuman, `lookup-service`'i tuketen diger servisler (party-service, customer-service,
contact-info-service, ileride product-service/order-service) icin yazilmistir. Eski duz
`lookup` tablosu (`group_code`/`value_id`/`code`/`look_up_value`) kaldirildi; yerine 7 tablodan
olusan kurumsal bir sema geldi. Bu dokuman o semayi nasil kullanacaginizi anlatir.

## Temel ilke: hicbir id'yi hardcode etmeyin

Eski moddel `DataTypeIds.CUSTOMER = 102L` gibi sabit sayilari servis kodunuza gomuyordu. Yeni
modelde bunu YAPMAYIN - lookup-service'in kendi verisi degisebilir (id'ler farkli olabilir).
Bunun yerine asagidaki "kod ile coz" uclarini kullanarak id'yi HER ZAMAN dinamik olarak alin
(gerekirse kendi tarafinizda cache'leyin - bkz. customer-service'teki mevcut
`LookupCacheServiceImpl` Caffeine cache deseni, ayni yaklasim burada da gecerli).

## Semanin genel yapisi

| Tablo | Ne ise yarar |
|---|---|
| **GNL_TP** | Duz "tip" degerleri tablosu - her satir tek bir deger (orn. "Mobil Hat"/GSM). `ent_code_name` o degerin ait oldugu grubu belirtir (orn. CNTC_MEDIUM). |
| **GNL_ST** | GNL_TP ile ayni mantik, ama "durum" (lifecycle/status) degerleri icin (orn. Aktif/Pasif/Silinmis). |
| **TYPE_VALUE** | GNL_TP/GNL_ST'den **bagimsizdir**. Herhangi bir is tablosuna (PROD, PARTY, CUST, CUST_ACCT...) atanmis polimorfik "tip etiketi" numarasini tutar - eski DATA_TYPE grubunun genellestirilmis hali. `row_id + bu numara` ile "bu kayit hangi tabloya ait" sorusu cevaplanir (ayni desen contact-info-service'in `ADDR.row_id/data_type_id`'si). |
| **GNL_CHAR / GNL_CHAR_VAL** | Karakteristik tanimi + o karakteristigin alabilecegi degerler. Aralarinda gercek FK yok - iliski bunlari kullanan servisin kendi junction tablosunda (orn. product-service'teki `PROD_CHAR_VAL`) kurulur. |
| **RSRC_SPEC / SRVC_SPEC** | Kaynak/servis spesifikasyonu tanimlari. `st_id` alani GNL_ST'ye mantiksal referanstir (gercek FK degil). |

**Onemli:** lookup-service icindeki hicbir tablo digerine gercek FK ile baglanmaz. Butun capraz
referanslar (TYPE_VALUE.field_name, RSRC_SPEC/SRVC_SPEC.st_id) mantiksaldir - caller dogru
degeri kod uzerinden coze coze bulur, sabit sayi olarak saklamaz.

## GNL_TP / GNL_ST - kod ile deger coz

Her iki tablo da ayni pattern'i sunar (yollar tabloya gore degisir):

| Islem | GNL_TP | GNL_ST |
|---|---|---|
| Kod ile coz (id'yi al) | `GET /api/v1/general-types/resolve/{entCodeName}/{shrtCode}` | `GET /api/v1/general-statuses/resolve/{entCodeName}/{shrtCode}` |
| Id ile getir (kod/gosterim metnini al) | `GET /api/v1/general-types/{id}` | `GET /api/v1/general-statuses/{id}` |
| Bir gruptaki tum degerleri listele | `GET /api/v1/general-types?entCodeName=...` | `GET /api/v1/general-statuses?entCodeName=...` |
| Tumunu listele | `GET /api/v1/general-types` | `GET /api/v1/general-statuses` |
| Ekle/guncelle/sil | `POST` / `PUT /{id}` / `DELETE /{id}` (ayni path) | ayni |

**Ornek:** contact-info-service bir contact medium'un tipini (GSM/EML/...) coze bilmek icin:
```
GET /api/v1/general-types/resolve/CNTC_MEDIUM/GSM
```
Response (`GnlTpResponse`):
```json
{ "gnlTpId": 172, "name": "Mobil Hat", "descr": "Mobil Hat", "shrtCode": "GSM",
  "entCodeName": "CNTC_MEDIUM", "entName": "CNTC_MEDIUM", "active": true, ... }
```
`gnlTpId` (172) sakladiginiz/gonderdiginiz id'dir.

**Guncelleme kurali:** `shrtCode`/`entCodeName` bircok yerde join anahtari olarak kullanildigi
icin PUT ile serbestce degistirilebilir olsa da (soft-delete disinda bir immutability kisiti
yok), pratikte bunlari degistirmek diger servislerin cache'lerini/hardcoded referanslarini
kirar - degistirmeden once konusun.

## TYPE_VALUE - bir tablonun polimorfik tip etiketini coz

```
GET /api/v1/type-values/by-table/{tableName}
```

**Ornek:** party-service kendi PARTY tablosunun tip etiketini almak icin:
```
GET /api/v1/type-values/by-table/PARTY
```
Response (`TypeValueResponse`):
```json
{ "typeValueId": 1, "tableName": "PARTY", "fieldName": 9, "description": "Party_id", ... }
```
`fieldName` (9) kendi tablonuzda `row_id`'nin yaninda saklayacaginiz polimorfik etikettir.

Diger uclar: `GET /api/v1/type-values` (tumu), `GET /api/v1/type-values/{id}` (id ile),
`POST`/`PUT /{id}`/`DELETE /{id}` (CRUD - `tableName`/`fieldName` update'te degistirilemez,
immutable kimlik alanlaridir).

## GNL_CHAR / GNL_CHAR_VAL / RSRC_SPEC / SRVC_SPEC

Bu 4 tablo su an sadece tam CRUD sunuyor (henuz "kod ile coz" gibi ozel bir entegrasyon
deseni tanimlanmadi - ihtiyac ortaya ciktikca eklenecek):

| Tablo | Path |
|---|---|
| GNL_CHAR | `/api/v1/characteristics` |
| GNL_CHAR_VAL | `/api/v1/characteristic-values` |
| RSRC_SPEC | `/api/v1/resource-specs` |
| SRVC_SPEC | `/api/v1/service-specs` |

`RSRC_SPEC`/`SRVC_SPEC` olusturulurken `stId` alani var olan bir `GNL_ST` id'si olmali (`GNL_ST`
tarafinda `RSRC_SPEC`/`SRVC_SPEC` gruplarinin kendi durum degerleri zaten seed'li - asagidaki
referans tabloya bakin).

## shared-contracts - kullanilacak siniflar

Tum DTO'lar ve sabitler `com.etiya.crm.shared.contracts` altinda, tablo basina bir paket:

| Paket | Icerik |
|---|---|
| `gnltp` | `CreateGnlTpRequest`, `UpdateGnlTpRequest`, `GnlTpResponse`, **`GnlTpGroups`** (ent_code_name sabitleri), **`GnlTpCodes`** (shrt_code sabitleri) |
| `gnlst` | `CreateGnlStRequest`, `UpdateGnlStRequest`, `GnlStResponse`, **`GnlStGroups`**, **`GnlStCodes`** |
| `typevalue` | `CreateTypeValueRequest`, `UpdateTypeValueRequest`, `TypeValueResponse`, **`TypeValueTables`** (table_name sabitleri) |
| `gnlchar` | `CreateGnlCharRequest`, `UpdateGnlCharRequest`, `GnlCharResponse` |
| `gnlcharval` | `CreateGnlCharValRequest`, `UpdateGnlCharValRequest`, `GnlCharValResponse` |
| `rsrcspec` | `CreateRsrcSpecRequest`, `UpdateRsrcSpecRequest`, `RsrcSpecResponse` |
| `srvcspec` | `CreateSrvcSpecRequest`, `UpdateSrvcSpecRequest`, `SrvcSpecResponse` |

Sabitleri kullanin, string literal yazmayin:
```java
lookupClient.resolveType(GnlTpGroups.CONTACT_MEDIUM, GnlTpCodes.MOBILE); // "CNTC_MEDIUM", "GSM" degil
```

## Su anki seed verisi - referans tablo

**GNL_TP gruplari** (✅ = gercek DBeaver verisinden, ⚠️ = henuz dogrulanmadi/tahmini):

| ent_code_name | Sabit | Degerler | Durum |
|---|---|---|---|
| CNTC_MEDIUM | `GnlTpGroups.CONTACT_MEDIUM` | PSTN(170)/FAX(171)/GSM(172)/EML(173) | ✅ |
| ACCOUNT_TYPE | `GnlTpGroups.ACCOUNT_TYPE` | CUST_ACCT(223)/BILL_ACCT(224) | ✅ |
| CAM_PARTY_TYPE | `GnlTpGroups.PARTY_TYPE` | ORG(163)/INDV(164) | ✅ |
| PROD_SPEC_RSRC_SPEC | `GnlTpGroups.PROD_SPEC_RSRC_SPEC` | REALIZED(48) | ✅ |
| PROD_SPEC_SRVC_SPEC | `GnlTpGroups.PROD_SPEC_SRVC_SPEC` | REALIZED(47) | ✅ |
| PROD_REL | `GnlTpGroups.PROD_REL` | PRNTPROD(591) | ✅ |
| PARTY | `GnlTpGroups.PARTY` | GNL(21) | ✅ |
| PARTY_ROLE_TYPE | `GnlTpGroups.PARTY_ROLE_TYPE` | CUSTOMER/PARTNER | ⚠️ |
| GENDER | `GnlTpGroups.GENDER` | MALE/FEMALE | ⚠️ |
| CITY | `GnlTpGroups.CITY` | ANKARA | ⚠️ |
| CUSTOMER_TYPE | `GnlTpGroups.CUSTOMER_TYPE` | YOUNG/RETIRED | ⚠️ |

**GNL_ST gruplari** (hepsi ✅ gercek veriden, hepsinde ACTV/PASS/DEL benzeri ortak kodlar var):

CUST_ORD, CUST_ACCT_PROD_INVL*, PROD, PROD_CHAR_VAL, RSRC_SPEC, IND, CUST_ACCT, PROD_SPEC,
PROD_SPEC_SRVC_SPEC, PARTY, PARTY_ROLE, PROD_SPEC_RSRC_SPEC, SRVC_SPEC, PROD_CATAL, PROD_OFR,
CUST, PROD_CATAL_PROD_OFR, CMPG, CNTC_MEDIUM (`GnlStGroups` sinifinda tam liste).
*CUST_ACCT_PROD_INVL: yeni tasarimda tablo kaldirildi, sadece eski referans veride var.

**TYPE_VALUE:**

| table_name | field_name | Sabit |
|---|---|---|
| PARTY | 9 | `TypeValueTables.PARTY` |
| CUST | 12 | `TypeValueTables.CUSTOMER` |
| CUST_ACCT | 13 | `TypeValueTables.CUSTOMER_ACCOUNT` |
| PROD | 20 | `TypeValueTables.PRODUCT` |
| ORDER | ❌ henuz yok | `TypeValueTables.ORDER` (sabit var, seed verisi yok) |

## Eski modelden gecis (deprecated siniflar)

`shared-contracts.lookup` paketindeki `LookupGroups`, `LookupCodes`, `LookupValueResponse`,
`DataTypeIds` `@Deprecated(forRemoval = true)` isaretlendi. Hala derleniyorlar (party-service/
customer-service/contact-info-service hala kullaniyor) ama lookup-service artik bu API'yi
sunmuyor - `GET /api/v1/lookups/...` endpoint'i kaldirildi.

| Eskiden | Simdi |
|---|---|
| `LookupClient.getByCode(groupCode, code)` → `LookupValueResponse` | `GET /api/v1/general-types/resolve/{entCodeName}/{shrtCode}` veya `/general-statuses/...` |
| `LookupClient.getById(groupCode, valueId)` → `LookupValueResponse` | `GET /api/v1/general-types/{id}` veya `/general-statuses/{id}` |
| `DataTypeIds.PARTY` / `DataTypeIds.CUSTOMER` (hardcoded 101/102) | `GET /api/v1/type-values/by-table/{tableName}` (gercek degerler artik farkli: PARTY=9, CUST=12) |

**Onemli:** `DataTypeIds`'teki 101/102 degerleri artik lookup-service'in gercek verisiyle
UYUSMUYOR (TYPE_VALUE'de PARTY=9, CUST=12). Bu siniflari kullanmaya devam eden servisler
kendi tarafinda dinamik cozmeye gecene kadar bu tutarsizligin farkinda olmali.

## Bilinen eksikler

- **CUST_TP hiyerarsik verisi** (Ozel/Resmi Daireler/Belediyeler/... - CAM_PARTY_TYPE'a bagli alt
  siniflandirma) henuz eklenmedi - GNL_TP'nin bir parent/hiyerarsi kolonuna ihtiyaci var, onaylanmadi.
- **TYPE_VALUE ORDER girdisi** henuz eklenmedi - gercek field_name numarasi bekleniyor.
