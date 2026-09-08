package com.grupobolivar.ecommerce.checkout.application;

import java.util.Optional;

/** Port: resolves a product by id. Implemented by the catalog infrastructure. */
public interface ProductStockPort {

	Optional<ProductSnapshot> findById(long productId);
}
