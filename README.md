# examen-ecommerce

Prueba técnica Full Stack — **Core E-Commerce con Sistema de Descuentos Acumulativos**
(Banco Davivienda · Grupo Bolívar).

MVP de checkout: catálogo de productos → carrito reactivo → **motor de descuentos
acumulativos en cascada** con tope del 35 % → persistencia de la orden.

Las 4 historias de usuario están implementadas de extremo a extremo:

| HU | Qué cubre | Dónde |
|---|---|---|
| **HU1** | Catálogo pre-configurado + carrito en tiempo real (subtotal en vivo) | `apps/frontend` · `GET /api/products` |
| **HU2** | Aplicar cupón + desglose (categoría / volumen / cupón, % efectivo, ahorro, total) | `POST /api/checkout/quote` |
| **HU3** | Checkout backend: valida stock → recalcula cascada + tope → decrementa stock → persiste orden (`@Transactional`) | `POST /api/checkout` |
| **HU4** | Alerta persistente al alcanzar el 35 %: *"¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)"* | `savings-limit-alert.component` |

Justificación del stack, trade-offs, aislamiento del motor y patrones de diseño (Strategy,
Factory, Chain/Pipeline, Observer, Repository): **[`docs/arquitectura.md`](docs/arquitectura.md)**.
Gobernanza de IA (skills, agentes, bitácora de correcciones): **[`docs/ia.md`](docs/ia.md)**.
Modelo de datos (3FN, migraciones): **[`docs/modelo-datos.md`](docs/modelo-datos.md)**.

## Arquitectura de un vistazo

| Capa | Tecnología |
|---|---|
| Frontend | Angular 22 (standalone + signals + router), Vitest, ESLint (`no-explicit-any` como error) |
| Backend | Spring Boot 4.1 + Java 17, Gradle wrapper. Dominio hexagonal + Repository Pattern |
| Datos | Spring Data JPA + **PostgreSQL 16** + **Flyway**. `compose.yaml` al ejecutar; **Testcontainers** en tests |
| Pruebas | JUnit 5 + Mockito + JaCoCo (gate 80 %) + Testcontainers · Vitest (umbral 80 %) |
| CI | GitHub Actions (`./gradlew check` + `npm test` + lint + build) |

## Estructura del monorepo

```
apps/
  backend/    # API Spring Boot (catalog · checkout · shared)
  frontend/   # SPA Angular (core · features · shared)
packages/
  fixtures/   # discount-cases.json — oráculo de cálculo compartido por back y front
docs/         # arquitectura.md · ia.md · modelo-datos.md
.claude/      # sub-agentes y skills (ver docs/ia.md)
```

---

# 1. Instalación

### Prerrequisitos

- **JDK 17** (probado con Temurin 17). No hace falta instalar Gradle: se usa el wrapper.
- **Node.js** 20.19+ / 22.12+ / 24, y npm.
- **Docker** en marcha (Docker Desktop). Lo usan tanto `bootRun` (levanta PostgreSQL) como
  los tests de integración del backend (Testcontainers).

### Puesta en marcha

```bash
git clone <repo-url> && cd examen-ecommerce

# 1) Backend  → http://localhost:8080   (levanta PostgreSQL y aplica Flyway solo)
cd apps/backend && ./gradlew bootRun

# 2) Frontend → http://localhost:4200   (en otra terminal)
cd apps/frontend && npm install && npm start
```

`spring-boot-docker-compose` levanta `compose.yaml` (PostgreSQL 16) al arrancar el backend
(`lifecycle-management: start-only` → el contenedor sigue vivo al parar la app). **Flyway**
aplica las migraciones `V1`–`V7` (`src/main/resources/db/migration/`); el esquema queda en
3FN y con datos de ejemplo (10 productos, reseñas, 3 cupones). El frontend hace *proxy* de
`/api` a `:8080` (`apps/frontend/proxy.conf.json`), así que no hay que configurar URLs.

- API: `http://localhost:8080/api/...` · Health: `http://localhost:8080/actuator/health`
- Front: `http://localhost:4200` (landing `/`, tienda `/store`, mis compras `/shopping`)

---

# 2. Configuración y variables de entorno

Todo funciona **sin configurar nada** con los valores por defecto. Para apuntar a otro
entorno (p. ej. un PostgreSQL corporativo) basta con variables de entorno — no se toca el
código ni `application.yml`:

| Variable | Ámbito | Efecto | Defecto |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | backend | JDBC URL de PostgreSQL | `jdbc:postgresql://localhost:5432/ecommerce` |
| `SPRING_DATASOURCE_USERNAME` / `_PASSWORD` | backend | credenciales de la BD | `ecommerce` / `ecommerce` |
| `SPRING_DOCKER_COMPOSE_ENABLED` | backend | `false` para **no** auto-levantar `compose.yaml` (si ya tienes PostgreSQL corriendo) | `true` |
| `SERVER_PORT` | backend | puerto HTTP | `8080` |
| `ORDER_REFERENCE_PREFIX` | backend | prefijo del radicado `<prefix>-<yyyyMMddHHmmssSSS>` | `ORD` |
| `DISCOUNT_VOLUME_THRESHOLD` / `_PERCENT` · `DISCOUNT_CAP_PERCENT` | `application.yml` | umbral y % de volumen, y % del tope | `100.00` · `0.05` · `0.35` |

La tasa de la Regla 1 (categoría) y los cupones son **datos**, no configuración: viven en
las tablas `categories.discount_rate` y `coupons` (ver `docs/modelo-datos.md`).

### Conectarse a la base de datos

```bash
cd apps/backend
docker compose up -d                                          # PostgreSQL 16 en localhost:5432
docker compose exec postgres psql -U ecommerce -d ecommerce   # cliente psql dentro del contenedor
```

| Host / Puerto | Base | Usuario / Contraseña | JDBC URL |
|---|---|---|---|
| `localhost` / `5432` | `ecommerce` | `ecommerce` / `ecommerce` | `jdbc:postgresql://localhost:5432/ecommerce` |

Migraciones por separado: `./gradlew flywayInfo` · `flywayMigrate` · `flywayClean`.
Detener la BD: `docker compose down` (`-v` para borrar también los datos).

### Cupones de referencia (seed)

| Código | Descuento | Estado | Uso |
|---|---|---|---|
| `WELCOME2026` | 15 % | activo | cupón del enunciado (flujo normal) |
| `BLACKFRIDAY2025` | 25 % | inactivo / vencido | edge case "cupón no registrado o expirado" |
| `MEGADESCUENTO` | 50 % | activo | cupón de prueba para **demostrar el tope del 35 %** (HU4) — ver `docs/arquitectura.md` §3.1 |

---

# 3. Comandos para correr el proyecto y las pruebas

### Ejecutar

| | Comando | Resultado |
|---|---|---|
| Backend | `cd apps/backend && ./gradlew bootRun` | API en `:8080` (+ PostgreSQL vía Docker) |
| Frontend (dev) | `cd apps/frontend && npm start` | SPA en `:4200` con proxy a `:8080` |
| Frontend (prod) | `cd apps/frontend && npm run build` | bundle en `apps/frontend/dist/` |

### Pruebas unitarias y de integración + cobertura (umbral 80 %)

```bash
# Backend — JUnit 5 + Mockito + Testcontainers (requiere Docker en marcha)
cd apps/backend
./gradlew test                 # solo pruebas
./gradlew check                # pruebas + gate de cobertura JaCoCo (falla si < 80 %)
#   Reporte HTML: apps/backend/build/reports/jacoco/test/html/index.html
#   El gate cubre checkout.domain / checkout.application y catalog.domain / catalog.application.

# Frontend — Vitest (ng test) + cobertura v8
cd apps/frontend
npm test                       # = ng test, modo CI + cobertura (umbral 80 % en angular.json)
npm run lint                   # ESLint, incluye @typescript-eslint/no-explicit-any
#   Reporte HTML: apps/frontend/coverage/index.html
```

Edge cases exigidos por el enunciado (§4.3), todos con test:

| Edge case | Test |
|---|---|
| Límite del 35 % superado | `AbsoluteCapPolicyTest`, caso `tope-35-...` de `DiscountPipelineTest`, `CheckoutIntegrationTest` |
| Carrito vacío / datos corruptos | `CheckoutServiceTest`, `cart.store.spec.ts` |
| Cupón no registrado / expirado | `CouponDiscountRuleTest`, `DiscountPipelineTest`, `checkout.facade.spec.ts` |
| Compra sin stock suficiente (incl. carrera concurrente) | `CheckoutServiceTest`, `CheckoutIntegrationTest` (409) |

### Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/products` | catálogo (`id, name, unitPrice, category, stock, imageUrl`) |
| `GET` | `/api/products/{id}` | detalle de un producto (marca, descripción, reseñas) |
| `POST` | `/api/checkout/quote` | calcula el desglose de descuentos, sin efectos colaterales |
| `POST` | `/api/checkout` | valida stock → recalcula → decrementa stock → persiste la orden |
| `GET` | `/api/orders` | lista las 20 órdenes más recientes ("Mis compras") |
| `GET` | `/api/orders/{radicado}` | detalle de una orden persistida |

Errores: `400` cuerpo inválido / carrito vacío · `404` producto u orden inexistente ·
`409` stock insuficiente. Un cupón inválido no es error: se ignora.
