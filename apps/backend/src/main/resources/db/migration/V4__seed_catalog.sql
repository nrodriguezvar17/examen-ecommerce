-- Catálogo de la demo (HU1) + clientes de prueba para el pseudo-login.
-- "Auriculares Bluetooth" (stock 3) y "Bolígrafos de gel" (stock 2) permiten
-- demostrar el rechazo por stock insuficiente (409).

insert into products (sku, name, unit_price, category_id, stock) values
    ('TEC-LAP-014', 'Laptop Pro 14"',           1200.00, (select id from categories where name = 'Tecnología'),  8),
    ('TEC-MOU-001', 'Mouse inalámbrico',           25.00, (select id from categories where name = 'Tecnología'), 40),
    ('TEC-KEY-002', 'Teclado mecánico',            80.00, (select id from categories where name = 'Tecnología'), 15),
    ('TEC-AUR-003', 'Auriculares Bluetooth',       60.00, (select id from categories where name = 'Tecnología'),  3),
    ('TEC-MON-027', 'Monitor 27 pulgadas',        300.00, (select id from categories where name = 'Tecnología'),  5),
    ('PAP-CUA-A05', 'Cuaderno A5',                  5.00, (select id from categories where name = 'Papelería'),  100),
    ('PAP-BOL-X03', 'Bolígrafos de gel (x3)',       4.50, (select id from categories where name = 'Papelería'),    2),
    ('LIB-NOV-001', 'La sombra del viento',        18.00, (select id from categories where name = 'Libros'),      25),
    ('HOG-LAM-001', 'Lámpara de escritorio LED',   35.00, (select id from categories where name = 'Hogar'),       20),
    ('DEP-BAL-005', 'Balón de fútbol talla 5',     22.00, (select id from categories where name = 'Deportes'),    12);

insert into product_details (product_id, display_name, description, brand, image_url)
select p.id, d.display_name, d.description, d.brand, d.image_url
from (values
    ('TEC-LAP-014', 'Laptop Pro 14" (Core i7, 16 GB)', 'Portátil ultraligero de 14" con 16 GB de RAM y SSD de 512 GB.', 'NovaTech',       'https://picsum.photos/seed/laptop/600/400'),
    ('TEC-MOU-001', 'Mouse inalámbrico silencioso',    'Mouse óptico inalámbrico con clic silencioso y 12 meses de batería.', 'NovaTech',  'https://picsum.photos/seed/mouse/600/400'),
    ('TEC-KEY-002', 'Teclado mecánico RGB',            'Teclado mecánico con switches marrones y retroiluminación RGB.', 'NovaTech',       'https://picsum.photos/seed/keyboard/600/400'),
    ('TEC-AUR-003', 'Auriculares Bluetooth ANC',       'Over-ear con cancelación activa de ruido y 30 h de autonomía.', 'AudioMax',        'https://picsum.photos/seed/headphones/600/400'),
    ('TEC-MON-027', 'Monitor 27" QHD 144 Hz',          'Monitor IPS 27" 2560x1440 a 144 Hz.', 'ViewPro',                                   'https://picsum.photos/seed/monitor/600/400'),
    ('PAP-CUA-A05', 'Cuaderno A5 tapa dura',           'Cuaderno A5 de 120 hojas, papel de 90 g.', 'PapelCo',                              'https://picsum.photos/seed/notebook/600/400'),
    ('PAP-BOL-X03', 'Bolígrafos de gel (pack x3)',     'Pack de 3 bolígrafos de gel, tinta negra, trazo 0.7 mm.', 'PapelCo',              'https://picsum.photos/seed/pens/600/400'),
    ('LIB-NOV-001', 'La sombra del viento',            'Novela best-seller, edición de bolsillo.', 'Editorial Sur',                        'https://picsum.photos/seed/book/600/400'),
    ('HOG-LAM-001', 'Lámpara de escritorio LED',       'Lámpara LED regulable, 3 temperaturas de color y puerto USB.', 'CasaLuz',         'https://picsum.photos/seed/lamp/600/400'),
    ('DEP-BAL-005', 'Balón de fútbol talla 5',         'Balón cosido a máquina, talla 5, uso recreativo.', 'SportOne',                     'https://picsum.photos/seed/ball/600/400')
) as d(sku, display_name, description, brand, image_url)
join products p on p.sku = d.sku;

insert into product_reviews (product_id, rating, title, comment, reviewer_name)
select p.id, r.rating, r.title, r.comment, r.reviewer_name
from (values
    ('TEC-LAP-014', 5, 'Excelente',           'Muy rápida y liviana, la batería dura todo el día.', 'Laura G.'),
    ('TEC-LAP-014', 4, 'Buena compra',        'Cumple, aunque el ventilador se escucha bajo carga.', 'Pedro M.'),
    ('TEC-MOU-001', 5, 'Silencioso de verdad','El clic casi no se oye, ideal para la oficina.', 'Sofía R.'),
    ('TEC-AUR-003', 4, 'Buen ANC',            'Cancela bien el ruido del transporte. Cómodos.', 'Diego T.'),
    ('TEC-AUR-003', 3, 'Correctos',           'El sonido es plano, esperaba más graves.', 'Marta V.'),
    ('TEC-MON-027', 5, 'Impecable',           'Colores muy buenos y los 144 Hz se notan.', 'Andrés P.'),
    ('PAP-CUA-A05', 5, 'Papel de calidad',    'No traspasa la tinta y muy buen precio.', 'Camila S.'),
    ('LIB-NOV-001', 5, 'Un clásico',          'Engancha desde la primera página.', 'Julián O.'),
    ('HOG-LAM-001', 4, 'Buena luz',           'Ilumina bien y no calienta.', 'Renata C.'),
    ('DEP-BAL-005', 4, 'Cumple',              'Para jugar los fines de semana va perfecto.', 'Nicolás F.')
) as r(sku, rating, title, comment, reviewer_name)
join products p on p.sku = r.sku;

insert into customers (username, full_name, email) values
    ('ana',    'Ana Torres',  'ana.torres@example.com'),
    ('carlos', 'Carlos Ruiz', 'carlos.ruiz@example.com');
