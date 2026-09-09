# Gobernanza de IA

> Documento obligatorio (punto 5 del enunciado). Deja constancia del uso de asistentes de
> IA generativa y, sobre todo, del rol del candidato como **auditor** del código entregado.

Herramienta principal: **Claude Code** (asistente de línea de comandos, modelo Sonnet). Los
sub-agentes y skills mencionados están versionados en este repositorio bajo `.claude/` y son
parte de la entrega. Herramienta secundaria: una segunda IA (ChatGPT) se usó una vez como
**revisor externo** del `arquitectura.md` (ver corrección #6).

---

## 1. Skills / Prompts automatizados

### 1.1. `generador-oraculo-descuentos`

- **Ubicación:** [`.claude/skills/generador-oraculo-descuentos/SKILL.md`](../.claude/skills/generador-oraculo-descuentos/SKILL.md)
- **Qué automatiza:** a partir de la especificación de reglas (porcentajes, umbral, orden,
  tope), genera y actualiza `packages/fixtures/discount-cases.json` — la tabla
  `carrito + cupón → desglose esperado` que sirve de **oráculo** para las pruebas
  parametrizadas de backend (`DiscountPipelineTest`, JUnit `@ParameterizedTest`) y frontend
  (specs de Vitest sobre el mapeo del desglose).
- **Entrada:** la config del motor + una lista de escenarios (incluyendo *edge cases*:
  umbral exacto `100.00` vs `100.01`, cupón inválido/expirado, tope superado).
- **Salida:** el JSON de casos, con cada monto calculado **de forma independiente** al
  código de producción (`BigDecimal`, escala 2, `HALF_UP`).
- **Auditoría del candidato:** se recalcularon **a mano** 3 casos
  (`cascada-completa-sin-tope`, `umbral-volumen-apenas-superado-100.01`,
  `tope-35-alcanzado-con-cupon-de-prueba`) antes de aceptar el fichero. El oráculo se
  mantiene deliberadamente **separado** de la implementación: si ambos se derivaran del
  mismo código, la prueba no valdría.

### 1.2. `probar-endpoints`

- **Ubicación:** [`.claude/skills/probar-endpoints/SKILL.md`](../.claude/skills/probar-endpoints/SKILL.md)
  (+ `requests.http` para el IDE).
- **Qué automatiza:** un *smoke test* de la API REST contra el backend en ejecución.
  Ejercita cada endpoint, cubre los *edge cases* del enunciado (carrito vacío/corrupto,
  cupón inválido/expirado, stock insuficiente, tope del 35 %) y contrasta los montos con
  `packages/fixtures/discount-cases.json`.
- **Salida:** tabla `passed/failed` por escenario. Regla dura: **no se modifica código de
  producción** para que un escenario pase.

---

## 2. Agents / Sub-agentes

### 2.1. `auditor-cobertura`

- **Ubicación:** [`.claude/agents/auditor-cobertura.md`](../.claude/agents/auditor-cobertura.md)
- **Rol:** ejecutar la batería de pruebas de back y front, leer los reportes de cobertura
  (`build/reports/jacoco/test/jacocoTestReport.xml` y el `lcov` de Vitest) y **fallar** si
  algún paquete/carpeta de las capas lógicas esenciales queda por debajo del 80 %.
- **Reglas duras:**
  1. Nunca bajar los umbrales de cobertura para "pasar".
  2. Reportar las ramas/líneas sin cubrir y proponer **tests concretos** para ellas.
  3. No proponer tests triviales que inflen el número sin verificar comportamiento.

### 2.2. `revisor-tipado`

- **Ubicación:** [`.claude/agents/revisor-tipado.md`](../.claude/agents/revisor-tipado.md)
- **Rol:** revisar los *diffs* buscando violaciones de tipado y de calidad:
  `any` en TypeScript sin justificación en línea, `double`/`float` para dinero en Java,
  `@SuppressWarnings` sin motivo, concatenación de SQL, DTOs de *request* sin Bean
  Validation, imports de `org.springframework` / `jakarta.persistence` dentro de
  `checkout/domain`.

---

## 3. Bitácora de co-creación

### 3.1. Reparto IA / manual

Estimación sobre el histórico de commits: **~70 % del volumen de código lo generó la IA**
(scaffolding, DTOs, entidades JPA, andamiaje de tests, plantillas de componentes, SCSS,
migraciones, borradores de documentación). El **~30 % restante — que es la lógica crítica —
se decidió y escribió a mano**, con la IA como asistente supervisado:

| Área | Origen | Nota |
|---|---|---|
| Orden de la cascada de descuentos y redondeo (`DiscountPipeline`, `Money`) | **manual** | precedencia Categoría→Volumen→Cupón, `BigDecimal` escala 2 `HALF_UP` en cada paso |
| Invariante del tope del 35 % + análisis de inalcanzabilidad (`AbsoluteCapPolicy`) | **manual** | se demostró que el máximo real es ~27,3 % y se implementó el tope igual, como defensa |
| Concurrencia de stock en HU3 (`@Transactional` + `UPDATE ... WHERE stock >= :qty`) | **manual** | decisión de arquitectura, ver `arquitectura.md` §4.1 |
| Frontera hexagonal: puertos en el dominio, adaptadores en infraestructura | **manual** | `ProductRepository`, `CouponCatalog`, `ProductStockPort` + sus adaptadores |
| Oráculo compartido (`discount-cases.json`) | IA (skill) + **auditoría manual** | 3 casos recalculados a mano |
| Elección de stack y BD (PostgreSQL + Flyway + Testcontainers), modelo de datos 3FN | **manual** | justificado en `arquitectura.md` y `modelo-datos.md` |
| Scaffold, DTOs, entidades, andamiaje de tests, componentes, SCSS, migraciones | IA | revisado y ajustado a mano |
| `arquitectura.md`, `modelo-datos.md`, `README.md`, este documento | IA (borrador) + edición y verificación manual | — |

### 3.2. Correcciones a sugerencias de la IA

Ejemplos concretos de sugerencias de la IA que se **rechazaron o corrigieron** por criterio
de ingeniería (el enunciado pide mínimo dos; se documentan seis reales):

1. **`double` para el dinero → `BigDecimal`.** Una primera versión del cálculo usaba
   `double`. Se rechazó: en la cascada (multiplicaciones sucesivas por `0.90`, `0.95`,
   `0.85`) el error de coma flotante se acumula y un caso del oráculo fallaba en el cuarto
   decimal. `Money` encapsula `BigDecimal` con escala 2 y `RoundingMode.HALF_UP`.

2. **Descuentos sumados → cascada multiplicativa.** Una sugerencia calculaba
   `d = T0 · (0.10 + 0.05 + 0.15)`. El enunciado pide acumulación **secuencial**: cada regla
   opera sobre el total ya afectado por las anteriores. Se corrigió en `DiscountPipeline` y
   se añadió un test que contrasta ambos resultados (30 % aditivo vs. ~27,3 % en cascada).

3. **`Money.ZERO` con orden de inicialización roto.** La IA declaró
   `public static final Money ZERO = Money.of(ZERO)` **antes** de las constantes `SCALE` /
   `ROUNDING` de la misma clase. En Java los `static` se inicializan en orden textual → al
   construir `ZERO` esas constantes valían `0`/`null` y `setScale(0, null)` lanzaba
   `ExceptionInInitializerError`. Detectado al correr los primeros tests reales del motor;
   se reordenaron las declaraciones (constantes primero, `ZERO` después).

4. **Doble Jackson en Spring Boot 4.** Para leer el oráculo en los tests, la IA añadió la
   dependencia `com.fasterxml.jackson.core:jackson-databind` (Jackson **2**). Spring Boot 4
   ya trae Jackson **3** (`tools.jackson`), así que quedaban dos versiones en el *classpath*
   y el IDE marcaba `JsonNode cannot be resolved`. Se quitó la dependencia y se migró
   `DiscountPipelineTest` a `tools.jackson.databind.JsonNode` + `JsonMapper.builder()`
   (`asText()` → `asString()`).

5. **Lectura *stale* tras un `@Modifying` masivo.** El decremento de stock
   (`UPDATE ProductEntity ... WHERE stock >= :qty`) actualiza la fila en la BD pero **no**
   la caché de primer nivel de Hibernate. Un test de integración que, dentro de la misma
   `@Transactional`, hacía `POST /api/checkout` y luego `GET /api/products`, seguía viendo
   el stock viejo. Se corrigió con `@Modifying(clearAutomatically = true, flushAutomatically = true)`.

6. **Diagrama e imágenes desalineados con el código.** Una segunda IA (revisión externa de
   `arquitectura.md`) marcó que el diagrama de componentes decía `InMemoryCouponCatalog`
   cuando la implementación real es `JpaCouponCatalog` sobre PostgreSQL, y que la
   descripción de `ProductStockPort` lo llamaba "adaptador" en vez de "puerto". Ambos se
   corrigieron. En la misma línea, las imágenes semilla usaban `picsum.photos` (fotos
   aleatorias de paisaje); se cambiaron a `loremflickr.com` con etiqueta por producto tras
   observación del candidato.

> Todas las correcciones anteriores están reflejadas en el historial de commits del
> repositorio (mensajes descriptivos, una por *commit* o grupo lógico).
