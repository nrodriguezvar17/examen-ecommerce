package com.grupobolivar.ecommerce.catalog.api;

import com.grupobolivar.ecommerce.catalog.infrastructure.ProductEntity;
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

	public static ProductResponse from(ProductEntity entity) {
		return new ProductResponse(
				entity.getId(),
				entity.getSku(),
				entity.getName(),
				entity.getDetail().getDisplayName(),
				entity.getDetail().getDescription(),
				entity.getUnitPrice(),
				entity.getCategory().getName(),
				entity.getStock(),
				entity.getRating().getRatingAverage(),
				entity.getRating().getReviewCount());
	}
}
