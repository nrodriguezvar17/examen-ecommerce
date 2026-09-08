# Arquitectura — Core E-Commerce con Descuentos Acumulativos

> Documento exigido por el punto **4.1** del enunciado. Responde de forma justificada:
> por qué este stack y diseño de carpetas, qué trade-offs se asumieron, cómo se aislaron
> las reglas matemáticas del motor de descuentos, y qué patrones de diseño se implementaron.

---

## 1. Contexto y objetivos de diseño

Se construye un MVP de checkout de e-commerce con un **motor de descuentos acumulativos
en cascada** y persistencia de órdenes. Lo que se evalúa no es la cantidad de código sino
el **criterio de ingeniería**: fundamentación de la arquitectura, patrones de
desacoplamiento, robustez del tipado, modularidad y una estrategia de pruebas con
cobertura ≥ 80 % en las capas lógicas.

Objetivos de diseño priorizados:

| # | Objetivo | Consecuencia de diseño |
|---|---|---|
| 1 | Que las reglas de negocio sean **exactas y verificables** | Motor de dominio puro + `BigDecimal` + tabla de casos como oráculo |
| 2 | Que las reglas estén **aisladas** de la persistencia y del transporte | Hexagonal-lite: dominio sin frameworks, puertos e infraestructura afuera |
| 3 | Que el catálogo de reglas sea **extensible** sin tocar el motor | Strategy + Factory + Pipeline |
| 4 | Que el frontend sea **reactivo** (subtotal en vivo, alerta del 35 %) | Store con signals (Observer) + Facade |
| 5 | **Realismo de entorno corporativo** (la prueba se evalúa por repo + sustentación) | Misma base de datos (PostgreSQL) en dev, test y producción; esquema con Flyway |

---

## 2. Stack seleccionado y justificación

*(Responde a: «¿Por qué seleccionó ese stack tecnológico y diseño de carpetas para el problema?»)*

| Capa | Elección | Versión |
|---|---|---|
| Frontend | **Angular** (standalone components + signals) | 22 |
| Backend | **Spring Boot + Java** (build con **Gradle wrapper**) | Spring Boot 4.1, Java 17 |
| Acceso a datos | **Spring Data JPA / Hibernate** + **Flyway** (migraciones) | — |
| Base de datos | **PostgreSQL 16** | `compose.yaml` en ejecución · Testcontainers en tests |
| Test backend | **JUnit 5 + Mockito + JaCoCo** (gate de cobertura) + **Testcontainers** | — |
| Test frontend | **Vitest** (runner oficial de Angular 22) + cobertura v8 | — |
| CI | **GitHub Actions** | — |

### 2.1. Por qué Angular + Spring Boot + JPA

El enunciado permite elegir el stack libremente (4.1). Se eligió **Angular + Spring Boot +
JPA** porque es el estándar tecnológico del **Grupo Bolívar** (Seguros Bolívar, Davivienda):
elegir la herramienta que el equipo domina reduce el riesgo de entrega en un ejercicio con
tiempo acotado y permite una **defensa técnica sólida** en la sustentación. Más allá de la
familiaridad, cada pieza aporta algo concreto al problema:

- **Java (tipado nominal fuerte)** cubre el requisito 4.2 («tipado estricto de extremo a
  extremo, sin `any`») sin esfuerzo: el lenguaje no tiene un equivalente a `any`. Los DTId
  de la API se modelan con `record` y validación declarativa (Bean Validation).
- **Spring Boot** impone una separación de responsabilidades natural
  (`Controller → Service → Repository`) y su **inyección de dependencias** hace que los
  patrones *Strategy* y *Factory* del motor de descuentos sean idiomáticos, no forzados.
- **Spring Data JPA + Flyway** resuelven la persistencia de la orden (HU3): repositorio
  declarativo, `@Transactional` para que *validar stock → descontar stock → persistir* sea
  atómico, y el esquema versionado en migraciones (`db/migration`) — la forma correcta en
  un entorno bancario, no `ddl-auto`.
- **Angular** trae `strict` + `strictTemplates` y, sobre todo, **signals**: el subtotal en
  vivo (HU1) y la alerta reactiva del 35 % (HU4) se expresan como estado derivado
  (`computed`) sin *callbacks* manuales. El tooling de test y cobertura viene integrado.

### 2.2. Por qué PostgreSQL como base de datos

El enunciado admite persistir «en memoria, SQLite o JSON», pero también deja **libre** la
elección de stack y evalúa el **criterio de ingeniería**. La corrección es por revisión de
repositorio + sustentación (no "clonar y ejecutar en 2 minutos"), así que se prioriza el
**realismo de entorno corporativo** sobre la mínima fricción:

- **PostgreSQL 16** es la base relacional estándar para servicios nuevos en el entorno del
  Grupo Bolívar. Entregar el ejercicio contra el motor real hace que el código de
  persistencia (tipos `numeric`, `timestamptz`, transacciones, índices) sea el que se
  defendería en Davivienda, no una aproximación.
- **Una sola base de datos en dev, test y producción.** Se evita el clásico *dialect
  drift* de usar H2 en tests y otro motor en producción (comportamientos distintos en
  fechas, `numeric`, *upserts*, *locking*). Los tests de integración corren contra
  PostgreSQL real vía **Testcontainers**.
- **Esquema versionado con Flyway** (`src/main/resources/db/migration/`): `V1__schema.sql`
  crea las tablas, `V2__seed_catalog.sql` carga el catálogo de la demo. Hibernate queda en
  `ddl-auto: validate` (solo verifica que las entidades cuadren con el esquema). Es la
  práctica correcta en banca; `ddl-auto: update` no es aceptable en producción.
- **Arranque sin fricción real**: `apps/backend/compose.yaml` define el contenedor y
  `spring-boot-docker-compose` lo **levanta y detiene automáticamente** en `./gradlew
  bootRun`. Único prerrequisito nuevo: Docker (herramienta corporativa estándar).
- **En la sustentación** se puede mostrar la persistencia con `psql` o cualquier cliente
  contra `localhost:5432`, y el `flyway_schema_history` como evidencia del control de
  esquema.

El dominio y los repositorios siguen sin conocer el motor concreto (JPA + puertos): si
mañana el estándar fuese Oracle, el cambio se limita al driver y a `application.yml`.

### 2.3. Diseño de carpetas — monorepo

Estructura exacta pedida por el enunciado (punto 3):

```
examen-ecommerce/
├── apps/
│   ├── backend/          # Spring Boot 4 + Java 17 (Gradle)
│   │   ├── compose.yaml  # PostgreSQL 16 (lo levanta spring-boot-docker-compose en bootRun)
│   │   └── src/main/resources/db/migration/   # V1__schema.sql, V2__seed_catalog.sql (Flyway)
│   └── frontend/         # Angular 22 (standalone)
├── packages/
│   └── fixtures/         # discount-cases.json: oráculo de cálculo compartido por back y front
├── docs/
│   ├── arquitectura.md   # este documento
│   └── ia.md             # gobernanza de IA
├── .github/workflows/    # CI: build + tests + gates de cobertura
├── .claude/              # sub-agentes y skills usados (ver docs/ia.md)
└── README.md
```

- **Monorepo** porque back y front evolucionan juntos en este MVP y una única clonación /
  un único historial de commits facilitan la evaluación. No se usa una herramienta de
  monorepo (Nx, Turborepo): son dos apps heterogéneas (Java + TS) sin código ejecutable
  compartido; el peso de esa herramienta no se justifica.
- **`packages/fixtures/`**: un único artefacto compartido de verdad — la tabla
  `carrito + cupón → desglose esperado`. La consumen las pruebas de JUnit y las de Vitest,
  de modo que **ambos lados verifican contra los mismos números**.

#### Backend — *package-by-feature* + capas

```
com.grupobolivar.ecommerce
├── catalog/                     # producto, stock, seed del catálogo
│   ├── domain/  application/  infrastructure/  api/
├── checkout/
│   ├── domain/
│   │   ├── model/               # Money, CartItem, Cart               (Java puro)
│   │   ├── discount/            # motor de descuentos                 (Java puro)
│   │   │   ├── DiscountRule            (Strategy)
│   │   │   ├── CategoryDiscountRule · VolumeDiscountRule · CouponDiscountRule
│   │   │   ├── DiscountPipeline        (Chain of Responsibility)
│   │   │   ├── AbsoluteCapPolicy       (post-procesador del tope 35 %)
│   │   │   ├── DiscountRuleFactory     (Factory)
│   │   │   └── DiscountContext · DiscountBreakdown · DiscountLine
│   │   └── coupon/              # Coupon, CouponCatalog (puerto)
│   ├── application/             # CheckoutService + puertos (OrderRepository, ProductStockPort)
│   ├── infrastructure/          # entidades JPA, adaptadores, InMemoryCouponCatalog, config
│   └── api/                     # CheckoutController, DTOs (record), GlobalExceptionHandler
└── shared/
```

Se agrupa **por feature** (catalog / checkout) y dentro de cada feature por **capa**
(`domain / application / infrastructure / api`). Esto mantiene junto lo que cambia junto y
deja explícita la dirección de las dependencias: `api → application → domain`, y
`infrastructure → domain` (implementa sus puertos). El dominio no depende de nadie.

#### Frontend — core / features / shared

```
src/app/
├── core/
│   ├── models/     # interfaces del contrato REST (Product, Cart, DiscountBreakdown)
│   ├── api/        # catalog-api.service, checkout-api.service (HttpClient)
│   └── state/      # cart.store (signals, Observer) · checkout.facade (Facade)
├── features/
│   ├── catalog/    # listado de productos
│   ├── cart/       # carrito y subtotal en vivo (HU1)
│   └── checkout/   # cupón + desglose (HU2) · alerta del 35 % (HU4)
└── shared/         # pipes, componentes de presentación
```

---

## 3. Trade-offs asumidos

*(Responde a: «¿Qué trade-offs de arquitectura asumió?»)*

| Trade-off | Decisión | Se gana | Se cede |
|---|---|---|---|
| Simplicidad **vs.** extensibilidad del motor | Strategy + Factory + Pipeline en vez de un método con `if`s | Cada regla se testea aislada; agregar una regla no toca las demás | Más clases y más indirección para 3 reglas |
| Exactitud **vs.** rendimiento de cálculo | `BigDecimal` (escala 2, HALF_UP) en vez de `double` | Totales exactos y reproducibles; sin deriva de coma flotante en la cascada | Aritmética algo más lenta (irrelevante a esta escala) |
| Realismo **vs.** fricción de arranque | PostgreSQL real (compose + Testcontainers) en vez de H2 embebida | Paridad dev/test/prod, sin *dialect drift*; el código de persistencia es el "de verdad" | Requiere Docker para ejecutar y para los tests de integración |
| Migraciones **vs.** velocidad inicial | Flyway (`ddl-auto: validate`) en vez de `ddl-auto: update` | Esquema explícito, versionado y revisable; práctica correcta en banca | Hay que mantener los scripts `V__*.sql` a la par de las entidades |
| Superficie de API **vs.** aislamiento de efectos | Dos endpoints: `POST /checkout/quote` (sin efectos) y `POST /checkout` (con efectos) | HU2/HU4 se recalculan en vivo sin mutar stock; los efectos colaterales viven en un solo lugar | Un endpoint más que mantener y documentar |
| Velocidad de entrega **vs.** cobertura amplia | La verificación de cobertura del 80 % se restringe a `checkout.domain` + `checkout.application` (y equivalentes de catalog) | El gate protege la lógica que importa; no penaliza DTOs/entidades sin lógica | La cobertura global reportada es menor que la de las capas esenciales |
| Runner de test del frontend | **Vitest**, el default de Angular 22 (no se migra a Jest ni se vuelve a Karma) | Cero configuración que justificar; rápido en CI; soportado oficialmente | Ecosistema de *matchers*/mocks más nuevo que el de Jest |

### 3.1. Observación de ingeniería sobre el tope del 35 %

Con el catálogo de reglas del enunciado, el descuento **máximo alcanzable** es:

```
cascada:  1 − (1 − 0.10)·(1 − 0.05)·(1 − 0.15) = 1 − 0.90·0.95·0.85 ≈ 0.27325  → 27,33 %
aditivo:  0.10 + 0.05 + 0.15                                              = 0.30     → 30,00 %
```

Es decir, **el tope del 35 % es teóricamente inalcanzable con `WELCOME2026`** (además, la
regla 1 aplica solo al subtotal de la categoría, con lo que el efecto real es aún menor).
Aun así, `AbsoluteCapPolicy` se implementa como **invariante defensiva** — protege el
margen ante futuras reglas o cupones más agresivos — y los porcentajes viven en
configuración. Para poder **probar y demostrar** el truncamiento se registra un cupón de
prueba (`MEGADESCUENTO`, 50 %). El truncamiento se cubre además con un test directo de
`AbsoluteCapPolicy` con un descuento sintético > 35 %.

---

## 4. Aislamiento del motor de descuentos

*(Responde a: «¿Cómo aisló las reglas matemáticas del motor de descuentos de los detalles
de persistencia y controladores de la API?»)*

1. **Dominio sin frameworks.** Los paquetes `checkout/domain/model` y
   `checkout/domain/discount` son Java puro: no importan `org.springframework.*`,
   `jakarta.persistence.*` ni tipos de la capa web. Está declarado en
   `checkout/domain/package-info.java` y se respeta por revisión de código.
2. **Entrada y salida como datos planos.** El motor recibe un `DiscountContext`
   (un `Cart` de records + el código de cupón) y devuelve un `DiscountBreakdown`
   (value object inmutable). No conoce HTTP, JSON ni filas de base de datos.
3. **Dependencias hacia afuera como puertos.** Lo único que el dominio necesita del
   exterior — el catálogo de cupones — se expresa como la interfaz
   `CouponCatalog`. La implementación (`InMemoryCouponCatalog`, que lee la
   configuración) vive en `infrastructure`.
4. **Orquestación separada del cálculo.** `CheckoutService` (capa `application`) coordina
   *validar stock → invocar el motor → descontar stock → persistir* y depende de
   interfaces (`OrderRepository`, `ProductStockPort`). El **cálculo** vive entero en
   `DiscountPipeline`; el servicio no hace aritmética de descuentos.
5. **Controladores delgados.** `CheckoutController` solo mapea DTO ↔ dominio y delega.
   Sin lógica de negocio.

Resultado: el motor se prueba instanciándolo con datos en memoria, sin Spring, sin base de
datos y sin levantar el contexto — los tests del `DiscountPipeline` son unitarios puros.

---

## 5. Patrones de diseño implementados

*(Responde a: «Describir e implementar explícitamente en el código al menos dos patrones
de diseño.» — se implementan cuatro.)*

### 5.1. Strategy — `DiscountRule` (backend)

`checkout/domain/discount/DiscountRule.java` define
`Money computeDiscount(DiscountContext, Money runningTotal)`. Cada descuento acumulativo
es una implementación independiente:

- `CategoryDiscountRule` — 10 % sobre el subtotal de la categoría "Tecnología".
- `VolumeDiscountRule` — 5 % si el total corriente **supera** $100 (umbral estricto).
- `CouponDiscountRule` — 15 % si hay un cupón activo (consulta el puerto `CouponCatalog`).

Cada estrategia se testea en aislamiento. Agregar un descuento nuevo = una clase nueva.

### 5.2. Factory — `DiscountRuleFactory` (backend)

`createPipeline(DiscountConfig, CouponCatalog)` construye la cadena de reglas **en su orden
de precedencia** (Categoría → Volumen → Cupón) y arma el `DiscountPipeline` con su
`AbsoluteCapPolicy`. El orden y la configuración quedan en un solo lugar; ningún otro
componente instancia reglas ni conoce la precedencia.

### 5.3. Chain of Responsibility / Pipeline — `DiscountPipeline` (backend)

`DiscountPipeline.calculate(...)` recorre las reglas en orden y pasa a cada una el
**total corriente** resultante de la anterior — de ahí que la acumulación sea
**multiplicativa en cascada** y no una suma de porcentajes. Al final delega en
`AbsoluteCapPolicy.enforce(...)` el tope del 35 %.

### 5.4. Observer — `CartStore` (frontend)

`core/state/cart.store.ts` mantiene el estado del carrito con **signals** de Angular y
expone estado derivado (`subtotal`, `itemCount`) como `computed(...)`. Los componentes
(listado, resumen, alerta del 35 %) **reaccionan** a los cambios sin suscripciones
manuales. Es la implementación idiomática del patrón Observer en Angular.

### Patrones de apoyo

- **Adapter** (backend): `infrastructure` adapta JPA y el catálogo a los puertos del
  dominio (`OrderRepository`, `ProductStockPort`, `CouponCatalog`).
- **Facade** (frontend): `checkout.facade.ts` oculta a los componentes la coordinación
  entre el `HttpClient` y el `CartStore` (debounce del *quote*, mapeo a *view-model*,
  manejo de errores).

---

## 6. Contratos REST y tipado estricto

- **Endpoints:**

  | Método | Ruta | Efectos | Historia |
  |---|---|---|---|
  | `GET` | `/api/products` | — | HU1 |
  | `POST` | `/api/checkout/quote` | ninguno (no toca stock ni BD) | HU2, HU4 |
  | `POST` | `/api/checkout` | valida stock → recalcula → descuenta stock → persiste (`@Transactional`) | HU3 |
  | `GET` | `/api/orders/{id}` | — | demo de persistencia |

- **Errores** vía `GlobalExceptionHandler`: `400` cuerpo inválido · `404` producto
  inexistente · `409` stock insuficiente · `422` cupón inválido/expirado.
- **Backend:** DTOs como `record` + Bean Validation (`@NotEmpty`, `@Positive`). Sin `any`
  posible en Java.
- **Frontend:** `strict: true`, `strictTemplates: true`, `noImplicitAny: true` en
  `tsconfig.json`; ESLint con `@typescript-eslint/no-explicit-any` como **error**.
  Las interfaces de `core/models` reflejan el contrato del backend.
- **Fuente de verdad numérica:** `packages/fixtures/discount-cases.json`.

---

## 7. Estrategia de pruebas y cobertura

- **Requisito:** ≥ 80 % en las capas lógicas esenciales de front (estado del carrito,
  alerta) y back (motor de descuentos, validación de stock) + tests de *edge cases*.
- **Backend** (JUnit 5 + Mockito + JaCoCo): dos niveles claramente separados.
  - *Unitarios, sin base de datos* (Java puro): un test por `DiscountRule`;
    `DiscountPipeline` con `@ParameterizedTest` alimentado por `discount-cases.json`;
    `AbsoluteCapPolicy` con entrada sintética > 35 %; `CheckoutService` con mocks de los
    puertos. Aquí vive el grueso de la cobertura exigida.
  - *Integración, contra PostgreSQL real* (Testcontainers, `@ServiceConnection`):
    `SchemaMigrationTest` valida Flyway; `@DataJpaTest` para los repositorios; *slice* de
    `@WebMvcTest` para el controlador; `@SpringBootTest` para el flujo de checkout completo.
  - El gate `jacocoTestCoverageVerification` falla el build si `LINE` o `BRANCH` < 0.80 en
    los paquetes `checkout.domain` / `checkout.application` (y equivalentes de catalog).
- **Frontend** (Vitest): `cart.store` (alta/baja/cantidad, subtotal, carrito corrupto);
  `checkout.facade` (*quote* con debounce, mapeo, errores 4xx/5xx); componente de desglose;
  componente de alerta del 35 % (visible solo si `capReached`, texto exacto,
  `role="alert"`). Umbrales de cobertura al 80 % configurados en `angular.json`.
- **Edge cases obligatorios:** tope del 35 % superado · carrito vacío o con datos
  corruptos · cupón no registrado o expirado · compra sin stock suficiente.

Comandos: ver [`README.md`](../README.md).

---

## 8. Diagrama

### Componentes

```mermaid
flowchart LR
    subgraph FE["Frontend — Angular 22"]
        CMP["Componentes<br/>catalog · cart · checkout"]
        STORE["CartStore<br/>(signals · Observer)"]
        FACADE["CheckoutFacade<br/>(Facade)"]
        APIFE["*-api.service<br/>(HttpClient)"]
        CMP --> STORE
        CMP --> FACADE
        FACADE --> STORE
        FACADE --> APIFE
    end

    subgraph BE["Backend — Spring Boot 4"]
        CTRL["CheckoutController / CatalogController<br/>(api)"]
        SVC["CheckoutService<br/>(application)"]
        PIPE["DiscountPipeline + reglas<br/>(domain · Java puro)"]
        PORTS["Puertos:<br/>OrderRepository · ProductStockPort · CouponCatalog"]
        INFRA["Adaptadores JPA · InMemoryCouponCatalog<br/>(infrastructure)"]
        CTRL --> SVC
        SVC --> PIPE
        SVC --> PORTS
        INFRA -. implementa .-> PORTS
    end

    APIFE -->|"REST / JSON"| CTRL
    INFRA --> PG[("PostgreSQL 16<br/>compose / Testcontainers")]
```

### Secuencia — checkout con cascada y tope

```mermaid
sequenceDiagram
    actor U as Cliente
    participant FE as Frontend
    participant API as CheckoutController
    participant SVC as CheckoutService
    participant ENG as DiscountPipeline
    participant DB as PostgreSQL (JPA)

    U->>FE: aplica cupón "WELCOME2026"
    FE->>API: POST /api/checkout/quote {items, coupon}
    API->>SVC: quote(cmd)
    SVC->>ENG: calculate(context)
    Note over ENG: Categoría → Volumen → Cupón (cascada)<br/>luego AbsoluteCapPolicy (tope 35 %)
    ENG-->>SVC: DiscountBreakdown {lines, effectiveRate, finalTotal, capReached}
    SVC-->>API: breakdown
    API-->>FE: 200 {desglose}
    FE-->>U: muestra desglose; si capReached → alerta HU4

    U->>FE: "Confirmar compra"
    FE->>API: POST /api/checkout {items, coupon}
    API->>SVC: checkout(cmd)
    SVC->>DB: validar stock
    SVC->>ENG: calculate(context)  (recálculo, nunca se confía en el cliente)
    SVC->>DB: decrementar stock + persistir orden  (@Transactional)
    DB-->>SVC: orderId
    SVC-->>API: confirmación {orderId, desglose}
    API-->>FE: 201 {orderId, desglose}
```

---

## 9. Cómo ejecutar

Instrucciones completas de instalación, variables de entorno y comandos de pruebas en
[`README.md`](../README.md). En resumen:

```bash
# Backend  → http://localhost:8080  (spring-boot-docker-compose levanta PostgreSQL solo)
cd apps/backend && ./gradlew bootRun

# Frontend → http://localhost:4200
cd apps/frontend && npm install && npm start

# Pruebas + cobertura  (el backend necesita Docker en marcha para los tests de integración)
cd apps/backend  && ./gradlew check          # tests + gate JaCoCo 80%
cd apps/frontend && npm test                 # Vitest + cobertura 80%
```
