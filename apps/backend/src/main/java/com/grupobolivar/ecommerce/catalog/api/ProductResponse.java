package com.grupobolivar.ecommerce.catalog.api;

import com.grupobolivar.ecommerce.catalog.domain.Product;
import java.math.BigDecimal;

/**
 * Catalog item returned by {@code GET /api/products}. Field names match the frontend
 * {@code Product} interface.
 */
public record ProductResponse(
		long id,
		String sku,
		String name,
		String displayName,
		String description,
		BigDecimal unitPrice,
		String category,
		int stock,
		BigDecimal ratingAverage,
		long reviewCount) {

	public static ProductResponse from(Product product) {
		return new ProductResponse(
				product.id(),
				product.sku(),
				product.name(),
				product.displayName(),
				product.description(),
				product.unitPrice(),
				product.category(),
				product.stock(),
				product.ratingAverage(),
				product.reviewCount());
	}
}
