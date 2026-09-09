-- La imagen del producto pasa de la tabla de detalle (product_details) a la cabecera
-- (products): así el listado del catálogo (GET /api/products) puede mostrarla sin cargar
-- el detalle. El detalle sigue haciendo el JOIN products ⋈ product_details para la marca,
-- la descripción larga y las reseñas.
alter table products add column image_url varchar(500);

update products p
set image_url = d.image_url
from product_details d
where d.product_id = p.id;

alter table product_details drop column image_url;
