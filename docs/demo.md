# Guion de sustentación (20 min)

Estructura del enunciado (§6): **7 min demo · 7 min arquitectura · 6 min auditoría de IA**.

## Antes de empezar

```bash
cd apps/backend  && ./gradlew bootRun     # :8080 + PostgreSQL (Docker)
cd apps/frontend && npm start             # :4200
```

Tener abiertos: navegador en `http://localhost:4200`, una terminal, y un cliente de BD
(`docker compose exec postgres psql -U ecommerce -d ecommerce`) o la pestaña Network.

---

## 1. Demo en vivo — 7 min

| Paso | Acción | Qué se ve |
|---|---|---|
| 1 | Landing `/` → **Explorar la tienda** | describe el negocio; navegación con menú hamburguesa |
| 2 | Agregar 1 Laptop (Tecnología) + 1 Cuaderno (Papelería) | **HU1**: el subtotal se actualiza al instante, sin recargar |
| 3 | "Ver detalle" de un producto → `/producto/:id` | marca, descripción, reseñas (tablas `product_details` / `product_reviews`) |
| 4 | Cupón `WELCOME2026` → **Aplicar** | **HU2**: desglose Categoría → Volumen → Cupón, % efectivo, ahorro total, total a pagar |
| 5 | Subir cantidad de un producto por encima de su stock | el botón `+` se bloquea en el máximo (validación en cliente) |
| 6 | Intentar comprar más que el stock forzando la petición | **edge case**: el backend responde `409` y la UI lo muestra |
| 7 | Cambiar el cupón a `MEGADESCUENTO` (50 %) | **HU4**: se supera el 35 %, `AbsoluteCapPolicy` trunca, aparece la alerta persistente *"¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)"* |
| 8 | **Confirmar compra** | **HU3**: `201` con `radicado`; el stock baja |
| 9 | Menú → **Mis compras** (`/shopping`) | la orden recién creada, expandible al desglose por línea |
| 10 | En `psql`: `select radicado, final_total, cap_reached from orders order by created_at desc limit 1;` | confirmación de la persistencia desde el backend |

---

## 2. Defensa de la arquitectura — 7 min

1. **Aislamiento del motor** (`arquitectura.md` §4): `checkout/domain/discount` es Java puro
   — sin Spring, sin JPA, sin HTTP. Recibe un `DiscountContext`, devuelve un
   `DiscountBreakdown` inmutable. `CheckoutService` orquesta; los controladores solo mapean.
2. **Patrones** (§5): **Strategy** (`DiscountRule` + 3 reglas), **Factory**
   (`DiscountRuleFactory` arma la cadena en orden de precedencia), **Chain/Pipeline**
   (`DiscountPipeline` = cascada), **Observer** (`CartStore` con signals), **Repository**
   (interfaces en el dominio, adaptadores JPA en infraestructura) + Adapter y Facade.
3. **Consistencia de HU3** (§4.1): `@Transactional` + `UPDATE ... WHERE stock >= :qty`
   atómico → sin condición de carrera sobre la última unidad (mostrar el bloque `alt` del
   diagrama de secuencia).
4. **Observación del 35 %** (§3.1): con las reglas por defecto el máximo real es
   `1 − 0.90·0.95·0.85 ≈ 27,3 %`; el tope es inalcanzable, se implementa igual como
   invariante y se creó `MEGADESCUENTO` para poder demostrarlo.
5. **Oráculo compartido**: `packages/fixtures/discount-cases.json` alimenta las pruebas
   parametrizadas de JUnit **y** de Vitest → misma verdad numérica en cliente y servidor.
6. **Cobertura en consola**:
   ```bash
   cd apps/backend  && ./gradlew check     # gate JaCoCo 80% (falla si baja)
   cd apps/frontend && npm test            # Vitest, umbral 80% en angular.json
   ```

---

## 3. Auditoría de IA — 6 min

Guion sobre `docs/ia.md`:

1. **Skills** (`.claude/skills/`): `generador-oraculo-descuentos` (genera el JSON oráculo de
   forma independiente al código) y `probar-endpoints` (smoke test de la API).
2. **Agentes** (`.claude/agents/`): `auditor-cobertura` (falla si una capa lógica baja del
   80 %, nunca baja el umbral) y `revisor-tipado` (marca `any`, `double` para dinero,
   imports de framework en el dominio).
3. **Bitácora — correcciones reales** (`ia.md` §3.2), elegir 2-3 para exponer:
   - `double` → `BigDecimal` (error de coma flotante en la cascada).
   - Descuentos **sumados** → **cascada multiplicativa** (test que contrasta 30 % vs 27,3 %).
   - `Money.ZERO` con orden de inicialización de `static` roto → `ExceptionInInitializerError`.
   - Doble Jackson (2 y 3) en Spring Boot 4 → se unifica en `tools.jackson`.
   - Lectura *stale* tras `@Modifying` → `clearAutomatically = true`.
