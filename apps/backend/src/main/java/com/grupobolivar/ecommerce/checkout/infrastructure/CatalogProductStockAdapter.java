package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import com.grupobolivar.ecommerce.checkout.application.ProductSnapshot;
import com.grupobolivar.ecommerce.checkout.application.ProductStockPort;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adapts the catalog's {@link ProductRepository} to the checkout's {@link ProductStockPort}.
 * Checkout depends on a catalog <em>domain</em> contract, not on JPA.
 */
@Component
public class CatalogProductStockAdapter implements ProductStockPort {

	private final ProductRepository products;

	public CatalogProductStockAdapter(ProductRepository products) {
		this.products = products;
	}

	@Override
	public Optional<ProductSnapshot> findById(long productId) {
		return products.findById(productId).map(product -> new ProductSnapshot(
				product.id(),
				product.displayName(),
				Money.of(product.unitPrice()),
				product.category(),
				product.stock()));
	}
}
