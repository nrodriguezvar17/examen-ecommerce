---
name: generador-oraculo-descuentos
description: Genera y actualiza packages/fixtures/discount-cases.json — la tabla carrito+cupón -> desglose esperado que sirve de oráculo para las pruebas parametrizadas de backend y frontend. Calcula cada monto de forma independiente al código de producción.
---

# Skill: Generador del oráculo de descuentos

## Propósito

Producir un conjunto de casos `entrada -> salida esperada` para el motor de descuentos
acumulativos, calculados **de forma independiente** a la implementación, de modo que las
pruebas de backend (JUnit `@ParameterizedTest`) y frontend (Vitest) verifiquen contra la
misma verdad numérica.

## Reglas de cálculo (del enunciado)

Dado el carrito con líneas `(unitPrice, quantity, category)` y un `coupon`:

1. `T0` = Σ(unitPrice · quantity).
2. **Categoría:** `d1 = round2(0.10 · Σ(lineas de categoría "Tecnología"))`. `T1 = T0 − d1`.
3. **Volumen:** si `T1 > 100` (estricto): `d2 = round2(0.05 · T1)`, si no `d2 = 0`. `T2 = T1 − d2`.
4. **Cupón:** si `coupon` existe y está **activo**: `d3 = round2(pct(coupon) · T2)`, si no `d3 = 0`. `T3 = T2 − d3`.
5. **Tope:** `rawD = T0 − T3`; `rawRate = round4(rawD / T0)`.
   - Si `rawRate > 0.35`: `totalDiscount = round2(0.35 · T0)`, `finalTotal = T0 − totalDiscount`,
     `effectiveRate = 0.3500`, `capReached = true`.
   - Si no: `totalDiscount = rawD`, `finalTotal = T3`, `effectiveRate = rawRate`, `capReached = false`.
   - En ambos casos `lines` guarda `d1, d2, d3` (el descuento **crudo** por regla).

`round2` = HALF_UP a 2 decimales; `round4` = HALF_UP a 4 decimales. Aplicar el redondeo
**en cada paso**, no solo al final.

## Escenarios a cubrir (mínimo)

- Cascada completa sin tope (Tecnología + otros, subtotal > 100, `WELCOME2026`).
- Sin productos de Tecnología (d1 = 0).
- Subtotal ≤ 100 (d2 = 0) y umbral **exacto** 100.00 (no dispara) vs. 100.01 (dispara).
- Cupón inexistente y cupón inactivo/expirado (ambos → d3 = 0).
- Tope del 35 % alcanzado (usar el cupón de prueba `MEGADESCUENTO` = 0.50).

## Salida

Sobrescribe `packages/fixtures/discount-cases.json` respetando el esquema existente
(`rules`, `cases[].items`, `cases[].coupon`, `cases[].expected`).

## Verificación obligatoria (rol auditor)

Antes de dar por buena la salida, **recalcular a mano** al menos 3 casos, uno de ellos el
del tope. Dejar constancia en `docs/ia.md` §1.1.
