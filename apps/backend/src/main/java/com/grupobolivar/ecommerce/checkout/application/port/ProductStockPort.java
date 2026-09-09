package com.grupobolivar.ecommerce.checkout.application.port;

import java.util.Optional;

/** Port: reads and decrements product stock. Implemented by the catalog infrastructure. */
public interface ProductStockPort {

	Optional<ProductSnapshot> findById(long productId);

	/**
	 * Atomically decrements the product's stock by {@code quantity}.
	 *
	 * @return {@code false} if there were not enough units (lost a concurrent race)
	 */
	boolean decrementStock(long productId, int quantity);
}
