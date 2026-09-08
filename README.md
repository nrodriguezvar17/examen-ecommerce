# examen-ecommerce

Prueba técnica Full Stack — **Core E-Commerce con Sistema de Descuentos Acumulativos**
(Banco Davivienda · Grupo Bolívar).

MVP de checkout: catálogo de productos → carrito reactivo → **motor de descuentos
acumulativos en cascada** con tope del 35 % → persistencia de la orden.

> **Estado:** Fase 0 (scaffold + documentación). El motor de descuentos, la API de checkout
> y la UI se implementan en las fases siguientes. Ver [`docs/arquitectura.md`](docs/arquitectura.md).

---

## Arquitectura de un vistazo

| Capa | Tecnología |
|---|---|
| Frontend | Angular 22 (standalone + signals), Vitest, ESLint |
| Backend | Spring Boot 4.1 + Java 17, Gradle wrapper |
| Datos | Spring Data JPA + **PostgreSQL 16** + **Flyway** (migraciones). `compose.yaml` al ejecutar; **Testcontainers** en tests |
| Pruebas | JUnit 5 + Mockito + JaCoCo (gate 80 %) + Testcontainers · Vitest (umbral 80 %) |
| CI | GitHub Actions |

Justificación del stack, trade-offs, aislamiento del motor y patrones de diseño (Strategy,
Factory, Chain/Pipeline, Observer): **[`docs/arquitectura.md`](docs/arquitectura.md)**.
Gobernanza de IA: **[`docs/ia.md`](docs/ia.md)**.

## Estructura

```
apps/
  backend/    # API Spring Boot
  frontend/   # SPA Angular
packages/
  fixtures/   # discount-cases.json: oráculo de cálculo compartido por back y front
docs/         # arquitectura.md · ia.md
.claude/      # sub-agentes y skills (ver docs/ia.md)
```

## Prerrequisitos

- **JDK 17** (probado con Temurin 17). No hace falta instalar Gradle: se usa el wrapper.
- **Node.js 20.19+ / 22.12+ / 24** y npm.
- **Docker** en marcha (Docker Desktop). Lo necesitan tanto `bootRun` (levanta PostgreSQL)
  como los tests de integración del backend (Testcontainers).

## Ejecutar

### Backend — http://localhost:8080

```bash
cd apps/backend
./gradlew bootRun
```

`spring-boot-docker-compose` levanta `compose.yaml` (PostgreSQL 16) al arrancar
(`lifecycle-management: start-only` → el contenedor sigue vivo al parar la app). Flyway
aplica las migraciones `V1`–`V4` (`src/main/resources/db/migration/`, ver
[`docs/modelo-datos.md`](docs/modelo-datos.md)).

- API: `http://localhost:8080/api/...`
- Health: `http://localhost:8080/actuator/health`

#### Conectarse a la base de datos

```bash
cd apps/backend
docker compose up -d                                   # PostgreSQL 16 en localhost:5432
docker compose exec postgres psql -U ecommerce -d ecommerce   # cliente psql dentro del contenedor
```

Datos de conexión para un cliente externo (DBeaver, pgAdmin, IntelliJ…):

| | |
|---|---|
| Host / Puerto | `localhost` / `5432` |
| Base de datos | `ecommerce` |
| Usuario / Contraseña | `ecommerce` / `ecommerce` |
| JDBC URL | `jdbc:postgresql://localhost:5432/ecommerce` |

Migraciones de forma independiente: `./gradlew flywayMigrate` · `flywayInfo` · `flywayClean`.
Detener la base: `docker compose down` (con `-v` para borrar también los datos).

### Frontend — http://localhost:4200

```bash
cd apps/frontend
npm install
npm start
```

## Pruebas y cobertura

### Backend

```bash
cd apps/backend
./gradlew test                       # solo pruebas (requiere Docker en marcha)
./gradlew check                      # pruebas + gate de cobertura (JaCoCo, 80% en capas lógicas)
```

Reporte HTML: `apps/backend/build/reports/jacoco/test/html/index.html`.
Los tests unitarios del motor de descuentos son Java puro (sin BD); los de integración
usan Testcontainers y arrancan un PostgreSQL efímero.

### Frontend

```bash
cd apps/frontend
npm test                             # Vitest en modo CI + cobertura (umbral 80% en angular.json)
npm run lint                         # ESLint (incluye @typescript-eslint/no-explicit-any)
```

Reporte HTML: `apps/frontend/coverage/index.html`.

## Variables de entorno / perfiles

| Variable | Dónde | Efecto |
|---|---|---|
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | backend | apuntar a otro PostgreSQL (p. ej. el corporativo) sin tocar `application.yml` |
| `SPRING_DOCKER_COMPOSE_ENABLED` | backend | `false` para no auto-levantar `compose.yaml` (si ya tienes PostgreSQL corriendo) |
| `SERVER_PORT` | backend | puerto HTTP (defecto `8080`) |
| `discount.*` | `apps/backend/src/main/resources/application.yml` | porcentajes, umbral de volumen, tope y catálogo de cupones del motor de descuentos |

## Endpoints (contrato objetivo)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/products` | catálogo con `id, name, unitPrice, category, stock` |
| `POST` | `/api/checkout/quote` | calcula el desglose de descuentos sin efectos colaterales |
| `POST` | `/api/checkout` | valida stock, recalcula, decrementa stock y persiste la orden |
| `GET` | `/api/orders/{id}` | consulta una orden persistida |

## Cupones de referencia

| Código | Descuento | Estado | Uso |
|---|---|---|---|
| `WELCOME2026` | 15 % | activo | cupón del enunciado |
| `MEGADESCUENTO` | 50 % | activo | cupón de prueba para demostrar el truncamiento del 35 % (ver `docs/arquitectura.md` §3.1) |
