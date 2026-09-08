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
| Datos | Spring Data JPA + **H2 embebida** (fichero al ejecutar, memoria en tests) |
| Pruebas | JUnit 5 + Mockito + JaCoCo (gate 80 %) · Vitest (umbral 80 %) |
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
- No hace falta base de datos: H2 es embebida.

## Ejecutar

### Backend — http://localhost:8080

```bash
cd apps/backend
./gradlew bootRun
```

- API: `http://localhost:8080/api/...`
- Health: `http://localhost:8080/actuator/health`
- Consola H2: `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:file:./data/ecommerce`, usuario `sa`, sin contraseña)

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
./gradlew test                       # solo pruebas
./gradlew check                      # pruebas + gate de cobertura (JaCoCo, 80% en capas lógicas)
```

Reporte HTML: `apps/backend/build/reports/jacoco/test/html/index.html`.

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
| `SPRING_PROFILES_ACTIVE` | backend | perfil de Spring; los tests usan `src/test/resources/application.yml` (H2 en memoria) |
| `SERVER_PORT` | backend | puerto HTTP (defecto `8080`) |
| `SPRING_DATASOURCE_URL` | backend | sobreescribe la URL de H2 (p. ej. para apuntar a otra ruta) |
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
