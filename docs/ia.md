# Gobernanza de IA

> Documento obligatorio (punto 5 del enunciado). Deja constancia del uso de asistentes de
> IA generativa y, sobre todo, del rol del candidato como **auditor** del código entregado.
>
> **Estado:** estructura y agentes/skills definidos en la Fase 0. La bitácora de co-creación
> (§3) se completa con ejemplos reales a medida que avanza la implementación (Fases 1–3).

Herramienta principal: **Claude Code** (asistente de línea de comandos). Los sub-agentes y
skills mencionados están versionados en este repositorio bajo `.claude/`.

---

## 1. Skills / Prompts automatizados

### 1.1. `generador-oraculo-descuentos`

- **Ubicación:** [`.claude/skills/generador-oraculo-descuentos/SKILL.md`](../.claude/skills/generador-oraculo-descuentos/SKILL.md)
- **Qué automatiza:** a partir de la especificación de reglas (porcentajes, umbral, orden,
  tope), genera y actualiza `packages/fixtures/discount-cases.json` — la tabla
  `carrito + cupón → desglose esperado` que sirve de **oráculo** para las pruebas
  parametrizadas de backend (JUnit) y frontend (Vitest).
- **Entrada:** la config del motor + una lista de escenarios (incluyendo *edge cases*:
  umbral exacto, cupón inválido/expirado, tope superado).
- **Salida:** el JSON de casos, con cada monto calculado de forma independiente al código
  de producción (`BigDecimal`, escala 2, HALF_UP).
- **Auditoría del candidato:** se recalcularon **a mano** al menos 3 casos
  (`cascada-completa-sin-tope`, `umbral-volumen-apenas-superado-100.01`,
  `tope-35-alcanzado-con-cupon-de-prueba`) antes de aceptar el fichero. El oráculo se
  mantiene deliberadamente **separado** de la implementación: si ambos se derivaran del
  mismo código, la prueba no valdría.

*(Opcional / segundo skill)* `generador-mocks-checkoutservice`: andamiaje de mocks de
Mockito para los puertos de `CheckoutService`. Pendiente de la Fase 1.

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

| Área | Origen | Nota |
|---|---|---|
| Scaffold (Spring Initializr, `ng new`), configs de build | IA + herramientas | revisado y ajustado a mano (JaCoCo, perfiles H2, `strict`) |
| Modelo de dominio y motor de descuentos (`Money`, `DiscountPipeline`, reglas, tope) | **manual (lógica crítica)** | la IA asistió con *boilerplate*; el orden de la cascada, el redondeo y la invariante del 35 % se decidieron y escribieron a mano |
| Oráculo de casos (`discount-cases.json`) | IA (skill) + **auditoría manual** | 3 casos recalculados a mano |
| Pruebas unitarias | IA (andamiaje) + manual (asserts y *edge cases*) | *(Fase 1–3)* |
| `arquitectura.md`, `README.md` | IA + edición manual | — |

> El porcentaje exacto sugerido por IA vs. escrito a mano se cerrará al final, con base en
> el historial de commits.

### 3.2. Correcciones a sugerencias de la IA

> Mínimo dos ejemplos concretos de sugerencias rechazadas o corregidas por criterio de
> ingeniería. Se completan con casos reales durante la implementación. Candidatos ya
> previstos por el diseño:

1. **`double` para el dinero → `BigDecimal`.** Una primera versión del cálculo usaba
   `double`. Se rechazó: en la cascada (multiplicaciones sucesivas por 0.90, 0.95, 0.85)
   el error de coma flotante se acumula y un caso del oráculo fallaba en el cuarto decimal.
   `Money` encapsula `BigDecimal` con escala 2 y `RoundingMode.HALF_UP`.
2. **Descuentos sumados → cascada multiplicativa.** Una sugerencia calculaba
   `d = T0 · (0.10 + 0.05 + 0.15)`. El enunciado pide acumulación **secuencial**: cada
   regla opera sobre el total ya afectado por las anteriores. Se corrigió en
   `DiscountPipeline` y se agregó un test que contrasta ambos resultados (30 % vs. ~27,3 %).
3. *(reserva)* **Motor replicado en el frontend.** Se propuso recalcular los descuentos en
   Angular para "feedback instantáneo". Se rechazó: la fuente de verdad es el backend
   (HU3 recalcula siempre); el front solo consume `POST /checkout/quote` y renderiza. Se
   evita divergencia de lógica y duplicación de pruebas.
4. *(reserva)* **Umbral de volumen con `>=`.** Una versión usaba `>= 100`. El enunciado
   dice "supera los $100" → estricto. Caso de borde `umbral-volumen-exacto-100-no-dispara`.
