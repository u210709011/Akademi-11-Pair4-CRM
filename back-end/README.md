# CRM Back-end

Microservice mimarisi ile geliştirilen CRM projesinin back-end altyapısı.

## Yapı

```
back-end/
├── config-server/              # Spring Cloud Config Server (port 8888)
├── discovery-server/           # Eureka Server (port 8761)
├── api-gateway/                # Spring Cloud Gateway (port 8080) + merkezi Swagger UI aggregation
├── shared-contracts/           # Servisler arasi ortak DTO/contract + ortak Feign hata/retry altyapisi
├── shared-events/              # Ortak outbox/inbox entity+repository + event sinif tanimlari
├── customer-service/           # Musteri agregati (onboarding, billing account, Redis+Caffeine cache)
├── party-service/              # Kisi (individual/party role) verisi
├── contact-info-service/       # Adres + iletisim bilgisi (contact medium)
├── order-service/               # Siparis/sepet
├── lookup-service/              # Referans veri (REST-only, event yok)
└── product-service/             # Urun katalog/kampanya/teklif (REST-only, event yok)
```

**Not:** Merkezi konfigürasyonlar (`configs/<servis-adi>/application[-<profil>].yml`)
bu repo'nun içinde DEĞİL, ayrı bir git deposunda tutulur — Config Server oradan
klonlayıp servislere sunar (bkz. "Konfigürasyon ve Ortam Profilleri").

**Not:** Altyapı (Postgres/Kafka/Redis/Keycloak) tanımları, tüm servisleri +
front-end'i container olarak ayağa kaldıran `docker-compose.yml` ve
başlatma/durdurma script'leri front-end ile birlikte kullanıldığı için
`back-end/` içinde DEĞİL, tamamen `<repo-root>/infra/` altında yaşar
(postgres-init, keycloak, debezium config/data dosyalarıyla birlikte):

```
infra/
├── docker-compose.yml
├── postgres-init/ , keycloak/ , debezium/    # altyapı config/data dosyalari
└── run/
    ├── dev/    start.bat / stop.bat   # infra container, servisler native mvnw ile (hizli iterasyon)
    ├── test/   start.bat / stop.bat   # tum stack container, SPRING_PROFILE=test
    └── prod/   start.bat / stop.bat   # tum stack container, SPRING_PROFILE=prod
```

Hangisini çalıştıracağını seçmek, o ortamın klasörüne girip `start.bat`
çalıştırmak kadar basit (bkz. "Uygulamaları Başlatma Sırası").

## Sürümler

- Java 21/25, Spring Boot 3.5.x, Spring Cloud 2025.0.x (Northfields), Resilience4j
- PostgreSQL 16, Kafka 4 (KRaft), Debezium 3.1, Redis 7, Keycloak 26

## Servis Tablosu

| Servis | Sorumluluk | Outbox/Kafka | Feign ile çağırdıkları | Cache |
|---|---|---|---|---|
| customer-service | Müşteri onboarding, adres/billing account | ✅ yayınlar + dinler | party, contact-info, lookup | Redis (`customers`) + Caffeine (`lookups`) |
| party-service | Individual/party role, national ID | ✅ yayınlar + dinler | lookup | - |
| contact-info-service | Adres + contact medium | ✅ yayınlar + dinler | customer, lookup | - |
| order-service | Sipariş/sepet | ✅ yayınlar (dinlemiyor) | customer, contact-info, lookup | - |
| lookup-service | Genel tip/durum/type-value referans verisi | yok (REST-only) | - | - |
| product-service | Ürün katalog/kampanya/teklif | yok (REST-only) | - | - |

## Altyapıyı Başlatma (Podman)

`podman compose` bir compose sağlayıcısına ihtiyaç duyar. `docker-compose` veya
`podman-compose` kurulu değilse: `pip install podman-compose`

Tüm stack'i (infra + her servis + front-end) build edip ayağa kaldırmak için
`infra/run/prod/start.bat` (veya `test/`) çalıştırılır (bkz. yukarıdaki not).
Sadece infra servisleri elle başlatılacaksa:

```bash
cd <repo-root>/infra
podman compose -f docker-compose.yml up -d postgres kafka kafka-ui debezium debezium-connectors redis redis-commander keycloak
```

| Servis | Adres | Notlar |
|---|---|---|
| PostgreSQL | localhost:5432 | user/pass: `crm` / `crm`, `wal_level=logical` (CDC için) |
| Kafka | localhost:9092 | Container içinden `kafka:29092` |
| Kafka UI | http://localhost:8090 | Topic/mesaj izleme (`kafbat/kafka-ui` imajı) |
| Debezium Connect | http://localhost:8083 | REST API |
| Redis | localhost:6379 | `customer-service` cache'i için |
| Redis Commander | http://localhost:8081 | Redis'teki key/value'lari tarayan web UI |
| Keycloak | http://localhost:8180 | admin/admin, `crm` realm otomatik import edilir |

## Uygulamaları Başlatma Sırası

Her projede Maven wrapper (`mvnw`) mevcuttur, ayrıca Maven kurulumu gerekmez.

Config Server tüm konfigürasyonların kaynağı olduğu için ilk o başlatılır;
diğer tüm uygulamalar (Eureka dahil) ayarlarını ondan çeker.

```bash
# 1. Config Server - http://localhost:8888
cd back-end/config-server && ./mvnw spring-boot:run

# 2. Discovery Server (Eureka) - http://localhost:8761
cd back-end/discovery-server && ./mvnw spring-boot:run

# 3. API Gateway - http://localhost:8080
cd back-end/api-gateway && ./mvnw spring-boot:run

# 4. İş servisleri (herhangi bir sırada)
cd back-end/customer-service && ./mvnw spring-boot:run
cd back-end/party-service && ./mvnw spring-boot:run
cd back-end/contact-info-service && ./mvnw spring-boot:run
cd back-end/order-service && ./mvnw spring-boot:run
cd back-end/lookup-service && ./mvnw spring-boot:run
cd back-end/product-service && ./mvnw spring-boot:run
```

Doğrulama:

- Eureka paneli: http://localhost:8761 (servisler kayıtlı görünmeli)
- Config Server: `curl http://localhost:8888/customer-service/dev`
- Gateway (token'sız 401 dönmeli): `curl -i http://localhost:8080/api/test`
- Merkezi Swagger UI: http://localhost:8080/swagger-ui.html — sağ üstteki
  dropdown'dan customer/contact-info/lookup servisleri arasında geçiş yapılabilir
  (party/order/product henüz bu aggregation'a eklenmedi, bkz. `api-gateway`'deki
  `SwaggerAggregatorConfig`).

## Konfigürasyon ve Ortam Profilleri

Merkezi konfigürasyonlar **ayrı bir git deposunda** tutulur (Config Server'ın
`config-server/src/main/resources/application.yml`'deki `spring.cloud.config.server.git.uri`
alanına bakın); bu repo'nun `back-end/configs/` diye bir klasörü yoktur. Her
servisin kendi klasörü şu 4 dosyadan oluşur:

```
configs/<servis-adi>/
├── application.yml          # ortamdan bağımsız ayarlar
├── application-dev.yml      # dev ortamı
├── application-test.yml     # test ortamı
└── application-prod.yml     # prod ortamı
```

Bir servis Config Server'a bağlandığında önce `application.yml`, sonra aktif
profile göre `application-<profil>.yml` yüklenir; profil dosyası ortamdan
bağımsız dosyayı ezer. Aktif profil varsayılan olarak `dev`'dir; değiştirmek için:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
# veya
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Servislerin kendi `application.yml`'i sadece `spring.application.name` ve
`spring.config.import: configserver:...` içerir — Config Server ayakta değilse
veya import başarısızsa uygulama **başlamaz** (yerel fallback değer tutulmaz).

## Keycloak ile Token Alma

Realm import dosyası (proje kökünde `infra/keycloak/crm-realm.json`) şunları içerir:

- Client'lar: `crm-client` (interaktif login, confidential), `crm-client-short`
  (30sn token TTL, expiry testleri için), `customer-service-m2m` (sadece
  service-account, servisler arası Feign çağrılarının JWT'siz senaryolarında —
  örn. istekle tetiklenmeyen Kafka listener'larında — kullanılır)
- Rol: `CRM_AGENT`
- Kullanıcı: `salesperson` / `password` (rolü: `CRM_AGENT`)

```bash
curl -X POST http://localhost:8180/realms/crm/protocol/openid-connect/token \
  -d "client_id=crm-client" \
  -d "client_secret=crm-client-secret" \
  -d "grant_type=password" \
  -d "username=salesperson" \
  -d "password=password"
```

Dönen `access_token` değeri isteklerde `Authorization: Bearer <token>` olarak kullanılır.

## Servisler Arası Senkron Çağrılar (Feign)

customer/party/contact-info/order-service birbirini Feign ile çağırır. Tek bir
downstream'in yavaşlaması/çökmesi tüm zinciri askıya almasın diye:

- **Timeout:** `feign.client.config.default.connectTimeout/readTimeout` (2s/4s,
  Config Server'daki `application.yml`'de).
- **Circuit breaker:** `spring.cloud.openfeign.circuitbreaker.enabled=true` +
  `resilience4j.circuitbreaker.configs.default` (10 isteklik pencere, %50 hata
  eşiği, 10s açık kalma). Sadece gerçek 5xx/timeout/bağlantı hatası "hata"
  sayılır — 404/409/400 gibi normal iş kuralı cevapları (`FeignException.FeignClientException`)
  `ignoreExceptions` ile hariç tutulur, yoksa art arda "bulunamadı" testleri bile
  circuit'i açardı.
  - `spring.cloud.circuitbreaker.resilience4j.disable-time-limiter: true` **kritik** —
    aksi halde Spring Cloud OpenFeign her çağrıyı ayrı bir thread'de çalıştırır ve
    `FeignConfig`'teki `JwtTokenPropagationInterceptor` (kullanıcı JWT'sini
    `SecurityContextHolder`'dan okur, thread-local) farklı thread'de boş context
    bulup sessizce M2M service-account token'ına düşer.
- **Retry:** Sadece **GET** çağrıları, sadece bağlantı seviyesi hatalarda (timeout,
  connection refused) retry edilir — `shared-contracts`'taki `GetOnlyRetryer`.
  POST/PUT/DELETE hiç retry edilmez (mükerrer kayıt riski).
- **Ortak hata yakalama:** Her servisin `GlobalExceptionHandler`'ı
  `shared-contracts`'taki `AbstractDownstreamExceptionHandler`'ı extend eder —
  downstream'den dönen `FeignException`'ın gerçek status/mesajını olduğu gibi
  yansıtır, circuit OPEN'ken `CallNotPermittedException`'ı 503'e çevirir. Yeni
  bir servis Feign client eklediğinde bu handler'ı extend etmesi yeterli.

## Caching (customer-service)

`customer-service`'te iki katmanlı cache var (`CacheConfig.java`):

- **Caffeine** (local, in-process): `lookups` cache'i, lookup-service'e yapılan
  çözümleme çağrılarını 30dk TTL ile tutar.
- **Redis** (distributed, `@Primary`): `customers` cache'i, `GenericJackson2JsonRedisSerializer`
  ile JSON serialize edilir, 10dk TTL. Sadece DTO/record cache'lenir (JPA entity değil).
- Redis erişilemez olduğunda cache "best-effort" kalsın diye `CachingConfigurer.errorHandler()`
  ile hata loglanıp yutulur — Redis'in çökmesi API'yi aşağı çekmez.
- `spring.data.redis.connect-timeout`/`timeout` (2s) ile Redis'in kendisi de
  Feign'dekiyle tutarlı bir timeout disiplinine tabi.

## Outbox / Inbox / Debezium

1. Outbox/Kafka kullanan servisler (customer, party, contact-info, order) kendi
   veritabanında (proje kökündeki) `infra/debezium/outbox-table.sql`'deki `outbox`/`inbox`
   tablolarını Flyway migration ile oluşturur (lookup-service ve product-service
   REST-only olduğu için bu tablolara sahip değil).
2. İş mantığı ile outbox insert'ü **aynı transaction** içinde yapılır
   (`shared-events`'teki `OutboxEventPublisher`).
3. Debezium, WAL üzerinden outbox tablosunu izler ve EventRouter SMT ile
   `<aggregate_type>-events` topic'ine yayınlar (örn. `customer-events`).
4. Connector kaydı **compose ayağa kalktığında otomatik** yapılır
   (`debezium-connectors` container'ı, proje kökündeki `infra/debezium/*-connector.json`
   dosyalarını registre eder). Elle tekrar kaydetmek gerekirse:

```bash
cd infra/debezium
./register-connectors.sh
```

Yeni servis için (proje kökünde) `infra/debezium/customer-outbox-connector.json` kopyalanıp
`database.dbname`, `topic.prefix` ve `slot.name` alanları güncellenir.

## Yeni Microservice Ekleme Adımları

1. [start.spring.io](https://start.spring.io) üzerinden Java 21+ / Boot 3.5.x projesi oluştur
   (bağımlılıklar: Web, JPA, PostgreSQL, Eureka Client, Config Client, Kafka, Redis, Lombok, Actuator).
2. Proje kökündeki `infra/postgres-init/01-create-databases.sql` dosyasına veritabanını ekle
   (mevcut PostgreSQL volume'ü varsa veritabanını elle oluştur).
3. Merkezi config deposunda `configs/<servis-adi>/` klasörünü oluştur; içine
   `application.yml`, `application-dev.yml`, `application-test.yml` ve
   `application-prod.yml` dosyalarını ekle (bkz. "Konfigürasyon ve Ortam Profilleri").
4. Merkezi config deposundaki `configs/api-gateway/application.yml` içine route tanımı ekle.
5. Başka servisleri Feign ile çağıracaksa: `shared-contracts`'a bağımlılık ekle,
   `GlobalExceptionHandler`'ı `AbstractDownstreamExceptionHandler`'dan türet,
   `@EnableFeignClients(defaultConfiguration = DefaultFeignRetryConfig.class)`
   kullan, Config'e `feign.client.config` + `resilience4j.circuitbreaker` +
   `spring.cloud.circuitbreaker.resilience4j.disable-time-limiter: true` ekle
   (bkz. "Servisler Arası Senkron Çağrılar" bölümü).
6. Event yayınlayacaksa: outbox/inbox tablolarını migration ile oluştur,
   `shared-events`'e event sınıfını ekle, Debezium connector JSON'ını ekleyip kaydet.
7. Kullanıcıya dönen hata mesajları için `messages/messages.properties` (fallback),
   `messages_en.properties`, `messages_tr.properties` üçlüsünü oluştur ve
   `MessageKeys` sabitleri üzerinden kullan; log mesajları için `LogMessages`
   sabitleri kullan — kodda literal string bırakılmaz.

## Bilinen Sınırlamalar

- `postgres-init`de `billing_db`/`notification_db` oluşturuluyor ama karşılık
  gelen bir servis yok (planlanan ama henüz yazılmamış, ya da temizlenmesi
  gereken artık).
- `api-gateway`'deki Swagger UI aggregation'ı sadece customer/contact-info/lookup
  servislerini kapsıyor.
- **Kafka'nın tüketici (consumer) tarafı kısmen broker'a kilitli.** Event
  yayınlama (outbox → Debezium) hiç Kafka client'ına dokunmaz, tamamen
  broker-agnostic. Tüketici tarafında da her `@KafkaListener` artık ince bir
  **adapter**'dır (örn. `PartyEventListener`, `ContactMediumEventListener`,
  `CustomerEventListener` — customer/party/contact-info-service) ve gerçek iş
  mantığını (idempotency + event işleme) broker'dan habersiz bir `XxxEventHandler`
  sınıfına devreder (örn. `PartyEventHandler`). Başka bir broker'a (örn.
  RabbitMQ) geçilirse **sadece bu 4 adapter sınıfı** yeniden yazılır — `@RetryableTopic`'in
  RabbitMQ'da native DLX/dead-letter-exchange modeliyle karşılığı ayrı kurulmalı —
  ama iş mantığı (handler sınıfları + testleri) hiç değişmez. Debezium'un
  outbox EventRouter'ı yine de temelde Kafka Connect'e göre kurulu; CDC tarafını
  değiştirmek ayrı bir iştir.
- Outbox tabloları (customer/party/contact-info/order-service) hiç temizlenmiyor
  — Debezium işlediği satırları silmiyor, tablo süresiz büyüyor. Zamanla
  index/tablo boyutu performansı etkileyebilir; `@Scheduled` bir temizlik job'ı
  (örn. "N günden eski satırları sil") eklenmesi önerilir.
- Servisler arası bir correlation/trace ID (`X-Request-Id`, MDC) yok — bir
  isteğin customer→party→contact-info arasında nerede başarısız olduğunu
  izlemek için ayrı ayrı loglara zaman damgasıyla bakmak gerekiyor.
