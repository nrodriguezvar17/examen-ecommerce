-- Datos de referencia (no cambian con el uso normal): estados de la orden,
-- categorías con su tasa de descuento (Regla 1) y cupones (Regla 3).

insert into order_statuses (code, label, sort_order, is_final) values
    ('COMPRADO',   'Comprado',   1, false),
    ('ENVIADO',    'Enviado',    2, false),
    ('EN_REPARTO', 'En reparto', 3, false),
    ('ENTREGADO',  'Entregado',  4, true);

insert into categories (name, discount_rate) values
    ('Tecnología', 0.1000),   -- Regla 1: 10% a los productos de esta categoría
    ('Papelería',  0.0000),
    ('Libros',     0.0000),
    ('Hogar',      0.0000),
    ('Deportes',   0.0000);

insert into coupons (code, description, discount_rate, active, valid_from, valid_until) values
    ('WELCOME2026', 'Cupón de bienvenida 2026', 0.1500, true, null, null),
    -- Cupón registrado pero inactivo y fuera de vigencia: cubre el edge case
    -- "cupones no registrados o expirados" del enunciado.
    ('BLACKFRIDAY2025', 'Black Friday 2025', 0.2500, false,
        timestamptz '2025-11-28 00:00:00-05', timestamptz '2025-11-30 23:59:59-05');
