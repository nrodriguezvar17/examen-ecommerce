package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.catalog.infrastructure.ProductJpaRepository;
import com.grupobolivar.ecommerce.checkout.application.ProductSnapshot;
import com.grupobolivar.ecommerce.checkout.application.ProductStockPort;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Adapts the catalog's product repository to the checkout's {@link ProductStockPort}. */
@Component
public class CatalogProductStockAdapter implements ProductStockPort {

	private final ProductJpaRepository products;

	public CatalogProductStockAdapter(ProductJpaRepository products) {
		this.products = products;
	}

	@Override
	public Optional<ProductSnapshot> findById(long productId) {
		return products.findById(productId).map(product -> new ProductSnapshot(
				product.getId(),
				product.getDetail().getDisplayName(),
				Money.of(product.getUnitPrice()),
				product.getCategory().getName(),
				product.getStock()));
	}
}
