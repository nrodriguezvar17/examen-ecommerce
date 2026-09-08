-- Resumen de calificaciones por producto. El promedio es dato derivado: se calcula
-- al vuelo, no se almacena (ver docs/modelo-datos.md §3.6).

create view product_rating_summary as
select p.id                                         as product_id,
       count(r.id)                                  as review_count,
       coalesce(round(avg(r.rating)::numeric, 2), 0) as rating_average
from products p
left join product_reviews r on r.product_id = p.id
group by p.id;
