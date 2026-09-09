-- Cupón de prueba para poder DEMOSTRAR la Regla 4 (tope absoluto del 35 %).
--
-- Con las reglas del enunciado (10 % categoría -> 5 % volumen -> 15 % cupón) el descuento
-- efectivo máximo es ~27,3 % (cascada) / 30 % (aditivo), así que el tope del 35 % es
-- inalcanzable con WELCOME2026 -- ver docs/arquitectura.md §3.1. AbsoluteCapPolicy se
-- mantiene como invariante defensiva; este cupón agresivo (50 %) hace que la cascada
-- supere el 35 %, la política la trunca exactamente en 35 % y el desglose marca
-- capReached = true, que es lo que dispara la alerta persistente de HU4 en el frontend.
insert into coupons (code, description, discount_rate, active, valid_from, valid_until) values
    ('MEGADESCUENTO', 'Cupón de prueba — demo del tope del 35 % (HU4)', 0.5000, true, null, null);
