# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A microservice CRM system (Turkish enterprise style — most identifiers are Turkish/domain
abbreviations, e.g. `GNL_TP`, `RSRC_SPEC`, `CUST_ACCT`). Sales reps onboard customers, manage
address/contact info, and create orders; services stay in sync via an event-driven outbox
pattern. Full architecture diagram and rationale: **[README.md](README.md)**. Deep operational
detail (Feign resilience config, caching, outbox/Debezium, adding a new service, known
limitations) is in **[back-end/README.md](back-end/README.md)** — read it before touching
cross-service concerns.

Docs are written in Turkish; code identifiers mix English and Turkish/abbreviated domain terms.

## Architecture

```
FE (Angular) --HTTPS--> api-gateway --JWT--> Keycloak
                            |
        +-------------------+-------------------+
        v          v            v         v          v
   customer    party      contact-info  order     lookup / product
```

- **api-gateway**: single entry point, Keycloak JWT validation, routing, aggregated Swagger UI
  (both the route proxying AND the Swagger UI's service dropdown are driven by one list —
  `SwaggerAggregatorConfig.AGGREGATED_SERVICES` — covering all 6 business services, plus the
  gateway's own `AuthController` docs added separately since those aren't proxied. The dropdown
  is populated by `SwaggerUiDropdownCustomizer`, a `BeanPostProcessor` that overwrites springdoc's
  auto-configured `SwaggerUiConfigProperties.urls` at startup, so a stale/missing entry in the
  external config-server `springdoc.swagger-ui.urls` property can no longer cause a service to be
  silently missing from the dropdown — add a new service to `AGGREGATED_SERVICES` and both the
  route and the tab appear).
- **config-server**: serves centralized config pulled from a *separate* git repo (not in this
  repo — see `spring.cloud.config.server.git.uri` in `config-server/application.yml`). Every
  other service's own `application.yml` only sets `spring.application.name` +
  `spring.config.import: configserver:...`; if Config Server is down, **the service refuses to
  start** (no local fallback values).
- **discovery-server**: Eureka registry; all services discover each other through it.
- **Business services**: `customer-service`, `party-service`, `contact-info-service`,
  `order-service` publish/consume domain events via outbox+Kafka and call each other
  synchronously via Feign. `lookup-service` and `product-service` are REST-only CRUD, no
  outbox/events.
- **shared-contracts**: cross-service DTOs/contracts + shared Feign error handling
  (`AbstractDownstreamExceptionHandler`) and retry config (`GetOnlyRetryer`,
  `DefaultFeignRetryConfig`).
- **shared-events**: shared outbox/inbox JPA entities+repositories (`OutboxEvent`,
  `OutboxEventPublisher`, `InboxEvent`) and cross-service event class definitions
  (`CustomerOnboardedEvent`, `PartyEvent`, `ContactMediumEvent`, `OrderSubmittedEvent`, `KafkaTopics`).

### Service-internal layering (Spring, per business service)

Each business service (see `customer-service` as the reference) follows a "core-first" layering,
not standard Spring package-by-feature:

```
api/controllers          # REST controllers
business/abstracts        # service interfaces
business/concretes        # service implementations
business/dtos/{requests,responses}
business/rules            # business rule validation classes
business/exceptions
dataAccess/abstracts       # Spring Data repositories
entities/{abstracts,concretes}
mapper                     # MapStruct mappers
messaging                  # Kafka listener adapters + outbox publish glue
clients/controllers        # Feign clients to other services
config, constants, security, utils
```

- Kafka consumption follows an **adapter/handler split**: a thin `@KafkaListener` adapter (e.g.
  `PartyEventListener`, `ContactMediumEventListener`, `CustomerEventListener`) delegates real
  idempotency + processing logic to a broker-agnostic `XxxEventHandler`. If the broker ever
  changes, only the adapter classes need rewriting.
- User-facing error messages go through `messages/messages*.properties` (`messages.properties`
  fallback, `messages_en.properties`, `messages_tr.properties`) referenced via `MessageKeys`
  constants; log messages go through `LogMessages` constants — no literal strings in code for
  either.
- Lookup values (`GNL_TP`/`GNL_ST`/`TYPE_VALUE` in lookup-service) are **never hardcoded as
  IDs** — always resolved dynamically via lookup-service's `resolve/{entCodeName}/{shrtCode}`
  endpoints (optionally cached, see `LookupCacheServiceImpl` Caffeine pattern in
  customer-service). Details: `back-end/lookup-service/LOOKUP_SERVICE_INTEGRATION.md`.

### Cross-service call resilience (Feign)

customer/party/contact-info/order-service call each other via Feign with timeout (2s/4s) +
circuit breaker (Resilience4j) + GET-only retry. Key gotcha:
`spring.cloud.circuitbreaker.resilience4j.disable-time-limiter: true` is required, otherwise
OpenFeign runs calls on a separate thread and `JwtTokenPropagationInterceptor` (which reads the
user JWT from `SecurityContextHolder`, a thread-local) silently falls back to the M2M
service-account token. Any new Feign-calling service must extend
`AbstractDownstreamExceptionHandler` in its `GlobalExceptionHandler`. Full details in
back-end/README.md § "Servisler Arası Senkron Çağrılar (Feign)".

### Outbox / Debezium / Kafka

Business logic + outbox insert happen in the same DB transaction
(`shared-events.OutboxEventPublisher`). Debezium tails the outbox table via WAL and routes rows
to `<aggregate_type>-events` Kafka topics via the EventRouter SMT. Connector registration is
automatic on `podman compose up` (`debezium-connectors` container runs
`infra/debezium/register-connectors.sh`). Outbox tables are **never purged** — this is a known
gap, not a bug to silently "fix" without discussion.

### Known repo quirks (don't try to fix without asking)

- `infra/postgres-init` creates `billing_db`/`notification_db` but no corresponding service
  exists yet.
- No cross-service correlation/trace ID (`X-Request-Id`/MDC) — debugging a request across
  services means checking timestamps across separate logs.

## Commands

### Backend (Java 21/25, Spring Boot 3.5.x, Spring Cloud 2025.0.x, Maven multi-module)

Each service has its own `mvnw`; the parent `back-end/pom.xml` aggregates all modules
(`shared-contracts`, `shared-events`, `customer-service`, `party-service`, `api-gateway`,
`lookup-service`, `contact-info-service`, `discovery-server`, `config-server`, `order-service`,
`product-service`).

```bash
# Build/test everything from back-end/
cd back-end && ./mvnw install                 # build all modules (shared libs must build first)
cd back-end && ./mvnw test                     # run all tests

# Single service
cd back-end/customer-service && ./mvnw spring-boot:run
cd back-end/customer-service && ./mvnw test
cd back-end/customer-service && ./mvnw test -Dtest=ClassName#methodName   # single test

# Startup order matters (each service pulls config from Config Server on boot):
# 1. config-server (localhost:8888)  2. discovery-server/Eureka (localhost:8761)
# 3. api-gateway (localhost:8080)    4. business services, any order
```

### Front-end (Angular 22, standalone components)

```bash
cd front-end
npm install
ng serve            # dev server, http://localhost:4200
ng build             # production build -> dist/
ng test              # Karma/Jasmine unit tests
ng generate component path/to/name
```

`front-end/src/app` layout: `core/` (auth, i18n, customer API services — singleton,
injected app-wide), `features/` (routed feature components: customer CRUD, b2b, approvals,
auth/login), `layouts/` (`auth-layout`, `main-layout` shells), `shared/components/` (reusable
presentational components: navbar, sidebar, date-picker-header).

### Infra (Podman Compose)

```bash
# Full stack (infra + every service + front-end), pick one:
cd infra/run/dev  && start.bat     # infra in containers, services run natively via mvnw (fast iteration)
cd infra/run/test && start.bat     # everything containerized, SPRING_PROFILE=test
cd infra/run/prod && start.bat     # everything containerized, SPRING_PROFILE=prod
# stop.bat counterpart in each folder

# Infra only, manually:
cd infra && podman compose -f docker-compose.yml up -d postgres kafka kafka-ui debezium debezium-connectors redis redis-commander keycloak
```

| Service | Address | Notes |
|---|---|---|
| Config Server | http://localhost:8888 | `curl http://localhost:8888/customer-service/dev` to verify |
| Eureka | http://localhost:8761 | dashboard shows registered services |
| API Gateway | http://localhost:8080 | `curl -i http://localhost:8080/api/test` → 401 without token |
| Swagger UI (aggregated) | http://localhost:8080/swagger-ui.html | route proxying: customer/contact-info/lookup/order/product |
| Kafka UI | http://localhost:8090 | |
| Redis Commander | http://localhost:8081 | |
| Keycloak | http://localhost:8180 | admin/admin, `crm` realm auto-imported |
| PostgreSQL | localhost:5432 | user/pass `crm`/`crm`, `wal_level=logical` for CDC |

Get a token (realm: `crm`, user `salesperson`/`password`, role `CRM_AGENT`):

```bash
curl -X POST http://localhost:8180/realms/crm/protocol/openid-connect/token \
  -d "client_id=crm-client" -d "client_secret=crm-client-secret" \
  -d "grant_type=password" -d "username=salesperson" -d "password=password"
```

## Adding a new microservice

See back-end/README.md § "Yeni Microservice Ekleme Adımları" for the full checklist (Spring
Initializr deps, `infra/postgres-init` DB entry, central config repo entries, gateway route,
Feign/circuit-breaker wiring if calling other services, outbox/Debezium wiring if publishing
events, `messages*.properties` + `MessageKeys`/`LogMessages` for user/log strings).