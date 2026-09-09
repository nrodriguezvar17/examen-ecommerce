-- picsum.photos devolvía fotos aleatorias (paisajes/retratos), no del producto.
-- loremflickr.com sirve una foto real de Flickr que coincide con una o más etiquetas,
-- así cada producto muestra algo de su tipo. ?lock=<n> fija la imagen para que no cambie
-- entre recargas.
update products p
set image_url = v.url
from (values
    ('TEC-LAP-014', 'https://loremflickr.com/600/400/laptop?lock=14'),
    ('TEC-MOU-001', 'https://loremflickr.com/600/400/computer,mouse?lock=1'),
    ('TEC-KEY-002', 'https://loremflickr.com/600/400/keyboard?lock=2'),
    ('TEC-AUR-003', 'https://loremflickr.com/600/400/headphones?lock=3'),
    ('TEC-MON-027', 'https://loremflickr.com/600/400/computer,monitor?lock=27'),
    ('PAP-CUA-A05', 'https://loremflickr.com/600/400/notebook?lock=5'),
    ('PAP-BOL-X03', 'https://loremflickr.com/600/400/pen?lock=3'),
    ('LIB-NOV-001', 'https://loremflickr.com/600/400/book?lock=1'),
    ('HOG-LAM-001', 'https://loremflickr.com/600/400/desk,lamp?lock=1'),
    ('DEP-BAL-005', 'https://loremflickr.com/600/400/soccer,ball?lock=5')
) as v(sku, url)
where p.sku = v.sku;
