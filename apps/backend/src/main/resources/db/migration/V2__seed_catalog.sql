-- Catálogo pre-configurado para la demo (HU1). Incluye productos de "Tecnología"
-- (disparan el descuento de categoría) y dos con stock bajo para poder demostrar
-- el rechazo por stock insuficiente (409).

insert into products (name, unit_price, category, stock) values
    ('Laptop Pro 14"',        1200.00, 'Tecnología', 8),
    ('Mouse inalámbrico',       25.00, 'Tecnología', 40),
    ('Teclado mecánico',        80.00, 'Tecnología', 15),
    ('Auriculares Bluetooth',   60.00, 'Tecnología', 3),
    ('Monitor 27"',            300.00, 'Tecnología', 5),
    ('Cuaderno A5',              5.00, 'Papelería', 100),
    ('Bolígrafo gel (x3)',      4.50, 'Papelería', 2),
    ('Novela best-seller',     18.00, 'Libros', 25);
