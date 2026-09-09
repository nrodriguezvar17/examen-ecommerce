package com.grupobolivar.ecommerce.catalog.api;

import com.grupobolivar.ecommerce.catalog.application.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HU1 — product catalog. */
@RestController
@RequestMapping("/api/products")
public class CatalogController {

	private final CatalogService catalog;

	public CatalogController(CatalogService catalog) {
		this.catalog = catalog;
	}

	@GetMapping
	public List<ProductResponse> list() {
		return catalog.listAvailableProducts().stream()
				.map(ProductResponse::from)
				.toList();
	}
}
