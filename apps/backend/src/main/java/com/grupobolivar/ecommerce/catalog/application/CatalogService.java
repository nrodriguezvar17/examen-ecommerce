package com.grupobolivar.ecommerce.catalog.application;

import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of the catalog: exposes the active products for HU1. */
@Service
public class CatalogService {

	private final ProductRepository products;

	public CatalogService(ProductRepository products) {
		this.products = products;
	}

	@Transactional(readOnly = true)
	public List<Product> listAvailableProducts() {
		return products.findAllActive();
	}
}
