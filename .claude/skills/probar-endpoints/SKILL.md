---
name: probar-endpoints
description: Smoke test de la API REST del backend. Ejercita cada endpoint contra la app en ejecución (localhost:8080), cubre los happy paths y los edge cases del enunciado (carrito vacío/corrupto, cupón inválido/expirado, stock insuficiente, tope del 35%) y contrasta los montos con packages/fixtures/discount-cases.json. Devuelve una tabla passed/failed.
---

# Skill: Probar endpoints

## Requisitos previos

1. Base de datos arriba y migrada:
   ```bash
   cd apps/backend && docker compose up -d
   ```
2. Backend en ejecución:
   ```bash
   cd apps/backend && ./gradlew bootRun
   ```
   Debe responder `GET http://localhost:8080/actuator/health` → `{"status":"UP"}`.
3. `curl` disponible (o usar `.claude/skills/probar-endpoints/requests.http` desde el IDE).

## Catálogo de endpoints

| Método | Ruta | Para qué sirve | Historia | Estado |
|---|---|---|---|---|
| `GET` | `/actuator/health` | Liveness del servicio | — | implementado (actuator) |
| `GET` | `/api/products` | Lista el catálogo (`id, sku, displayName, description, unitPrice, category, stock, ratingAverage, reviewCount`) | HU1 | Fase 1 |
| `POST` | `/api/checkout/quote` | Calcula el desglose de descuentos de un carrito + cupón. **Sin efectos**: no valida stock, no persiste, no decrementa | HU2, HU4 | Fase 1 |
| `POST` | `/api/checkout` | Valida stock → recalcula el motor → decrementa stock → persiste la orden (`@Transactional`). Devuelve el `radicado` y el desglose | HU3 | Fase 1 |
| `GET` | `/api/orders/{radicado}` | Detalle de una orden: líneas, desglose de descuentos, estado e historial | — | Fase 1 |
| `GET` | `/api/customers/{username}` | Pseudo-login: busca el cliente por usuario. `404` si no existe (no lo crea) | — | Fase 1 |
| `GET` | `/api/customers/{username}/orders` | Compras del cliente con su estado actual | — | Fase 1 |
| `PATCH` | `/api/orders/{radicado}/status` | Avanza el estado de la orden (`COMPRADO → ENVIADO → EN_REPARTO → ENTREGADO`). Solo hacia adelante; `409` si la transición es inválida | — | Fase 1 (demo) |

> Mientras los controladores no existan (`src/main/java/**/api/` solo tiene `.gitkeep`),
> únicamente responde `/actuator/*`. Este skill es el plan de prueba listo para usarse en
> cuanto la Fase 1 los implemente.

## Formas de request/response (provisional — se fija en Fase 1)

`POST /api/checkout/quote`
```json
// request
{ "items": [ { "productId": 1, "quantity": 1 }, { "productId": 6, "quantity": 4 } ],
  "couponCode": "WELCOME2026" }

// response 200
{ "originalTotal": 870.00,
  "lines": [ { "type": "CATEGORY", "amount": 85.00 },
             { "type": "VOLUME",   "amount": 39.25 },
             { "type": "COUPON",   "amount": 111.86 } ],
  "totalDiscount": 236.11, "effectiveRate": 0.2714,
  "finalTotal": 633.89, "capReached": false }
```

`POST /api/checkout` = igual que `quote` + `"customerUsername": "ana"`.
Response `201` = el desglose + `"radicado": "ORD-...", "status": "COMPRADO", "createdAt": "..."`.

## Escenarios a ejecutar

Ejecutar cada uno con `curl -s -o body.json -w "%{http_code}"` y verificar código + cuerpo.

| # | Escenario | Llamada | Se espera |
|---|---|---|---|
| 1 | Health | `GET /actuator/health` | `200` · `status: UP` |
| 2 | Catálogo | `GET /api/products` | `200` · 10 productos · cada uno con `category` y `stock` |
| 3 | Quote happy path | `POST /api/checkout/quote` carrito Tecnología+Papelería + `WELCOME2026` | `200` · montos == caso `cascada-completa-sin-tope` del fixture |
| 4 | Quote sin cupón | idem sin `couponCode` | `200` · sin línea `COUPON` |
| 5 | Cupón inexistente | `couponCode: "NOEXISTE"` | `200` · sin línea `COUPON` (no error) |
| 6 | Cupón expirado | `couponCode: "BLACKFRIDAY2025"` | `200` · sin línea `COUPON` |
| 7 | Carrito vacío | `items: []` | `400` (o `422`) · cuerpo de error tipado |
| 8 | Dato corrupto | `quantity: -2` / `productId` inexistente | `400` / `404` |
| 9 | Checkout happy path | `POST /api/checkout` + `customerUsername: "ana"` | `201` · `radicado` con formato `^[A-Z]{2,5}-[0-9]{14,17}$` · stock del producto decrementado (verificar con `GET /api/products`) |
| 10 | Stock insuficiente | `POST /api/checkout` con `productId` de "Auriculares Bluetooth" y `quantity: 99` (stock 3) | `409` · stock **sin** cambios |
| 11 | Orden persistida | `GET /api/orders/{radicado}` del paso 9 | `200` · líneas + `orderDiscounts` + `statusHistory` con una entrada `COMPRADO` |
| 12 | Login OK / KO | `GET /api/customers/ana` → `200`; `GET /api/customers/nadie` → `404` | según indicado |
| 13 | Compras del cliente | `GET /api/customers/ana/orders` | `200` · incluye la orden del paso 9 con `status: "COMPRADO"` |
| 14 | Avanzar estado | `PATCH /api/orders/{radicado}/status` body `{ "status": "ENVIADO" }` | `200` · luego repetir con `{ "status": "COMPRADO" }` → `409` (no retrocede) |
| 15 | Tope del 35 % | Con las reglas del enunciado es inalcanzable (máx ≈ 27,3 %). Reiniciar el backend con `./gradlew bootRun --args='--discount.cap.percent=0.20'` y repetir el paso 3 | `200` · `capReached: true` · `effectiveRate: 0.2000` · `totalDiscount == originalTotal * 0.20` |

## Salida esperada del skill

Tabla Markdown con una fila por escenario: `#`, descripción, `HTTP` obtenido, `PASSED`/`FAILED`,
y en los fallos el detalle (código o campo que no cuadró). Al final: `N/15 PASSED`.

No modificar código de producción para que un escenario pase; si algo falla, reportarlo.
