# CRM Back-end

Microservice mimarisi ile geliştirilen CRM projesinin back-end altyapısı.

## Yapı

```
back-end/
├── infra/                  # Podman compose ve altyapı dosyaları
│   ├── compose.yml         # PostgreSQL, Kafka, Debezium, Redis, Keycloak, Kafka UI
│   ├── postgres-init/      # Servis başına veritabanı oluşturan SQL
│   ├── keycloak/           # crm realm import dosyası
│   └── debezium/           # Outbox connector tanımları + outbox/inbox SQL
├── config-repo/            # Config Server'ın sunduğu merkezi konfigürasyonlar
├── config-server/          # Spring Cloud Config Server (port 8888)
├── discovery-server/       # Eureka Server (port 8761)
└── api-gateway/            # Spring Cloud Gateway (port 8080)
```

## Sürümler

- Java 21, Spring Boot 3.5.x, Spring Cloud 2025.0.x (Northfields)
- PostgreSQL 16, Kafka 4 (KRaft), Debezium 3.1, Redis 7, Keycloak 26

## Altyapıyı Başlatma (Podman)

`podman compose` bir compose sağlayıcısına ihtiyaç duyar. `docker-compose` veya
`podman-compose` kurulu değilse: `pip install podman-compose`

```bash
cd back-end/infra
podman compose -f compose.yml up -d
```

| Servis | Adres | Notlar |
|---|---|---|
| PostgreSQL | localhost:5432 | user/pass: `crm` / `crm`, `wal_level=logical` (CDC için) |
| Kafka | localhost:9092 | Container içinden `kafka:29092` |
| Kafka UI | http://localhost:8090 | Topic/mesaj izleme |
| Debezium Connect | http://localhost:8083 | REST API |
| Redis | localhost:6379 | |
| Keycloak | http://localhost:8180 | admin/admin, `crm` realm otomatik import edilir |

## Uygulamaları Başlatma Sırası

Her projede Maven wrapper (`mvnw`) mevcuttur, ayrıca Maven kurulumu gerekmez.

```bash
# 1. Discovery Server (Eureka) - http://localhost:8761
cd back-end/discovery-server && ./mvnw spring-boot:run

# 2. Config Server - http://localhost:8888
cd back-end/config-server && ./mvnw spring-boot:run

# 3. API Gateway - http://localhost:8080
cd back-end/api-gateway && ./mvnw spring-boot:run
```

Doğrulama:

- Eureka paneli: http://localhost:8761 (api-gateway kayıtlı görünmeli)
- Config Server: `curl http://localhost:8888/api-gateway/default`
- Gateway (token'sız 401 dönmeli): `curl -i http://localhost:8080/api/test`

## Keycloak ile Token Alma

Realm import dosyası iki test kullanıcısı içerir: `testuser/test123` ve `testadmin/admin123`.

```bash
curl -X POST http://localhost:8180/realms/crm/protocol/openid-connect/token \
  -d "client_id=crm-gateway" \
  -d "client_secret=crm-gateway-secret" \
  -d "grant_type=password" \
  -d "username=testuser" \
  -d "password=test123"
```

Dönen `access_token` değeri isteklerde `Authorization: Bearer <token>` olarak kullanılır.

## Outbox / Inbox / Debezium

1. Her iş servisi kendi veritabanında `infra/debezium/outbox-table.sql` içindeki
   `outbox` ve `inbox` tablolarını oluşturur (Flyway migration önerilir).
2. İş mantığı ile outbox insert'ü **aynı transaction** içinde yapılır.
3. Debezium, WAL üzerinden outbox tablosunu izler ve EventRouter SMT ile
   `<aggregate_type>-events` topic'ine yayınlar (örn. `customer-events`).
4. Connector kaydı (altyapı ayağa kalktıktan sonra bir kez):

```bash
cd back-end/infra/debezium
./register-connectors.sh
```

Yeni servis için `infra/debezium/customer-outbox-connector.json` kopyalanıp
`database.dbname`, `topic.prefix` ve `slot.name` alanları güncellenir.

## Yeni Microservice Ekleme Adımları

1. [start.spring.io](https://start.spring.io) üzerinden Java 21 / Boot 3.5.x projesi oluştur
   (bağımlılıklar: Web, JPA, PostgreSQL, Eureka Client, Config Client, Kafka, Redis, Lombok, Actuator).
2. `infra/postgres-init/01-create-databases.sql` dosyasına veritabanını ekle
   (mevcut PostgreSQL volume'ü varsa veritabanını elle oluştur).
3. `config-repo/<servis-adi>.yml` dosyasını oluştur (ortak ayarlar `application.yml`'den gelir).
4. `config-repo/api-gateway.yml` içine route tanımı ekle.
5. Outbox/inbox tablolarını migration ile oluştur, Debezium connector JSON'ını ekle ve kaydet.
