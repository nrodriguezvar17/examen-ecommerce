package com.grupobolivar.ecommerce.catalog.application;

import com.grupobolivar.ecommerce.catalog.api.ProductResponse;
import com.grupobolivar.ecommerce.catalog.infrastructure.ProductJpaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of the catalog: exposes the active products for HU1. */
@Service
public class CatalogService {

	private final ProductJpaRepository products;

	public CatalogService(ProductJpaRepository products) {
		this.products = products;
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> listAvailableProducts() {
		return products.findByActiveTrueOrderBySkuAsc().stream()
				.map(ProductResponse::from)
				.toList();
	}
}
