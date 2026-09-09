package com.grupobolivar.ecommerce.catalog.domain;

import java.math.BigDecimal;

/**
 * Catalog product as the domain sees it: plain data, no JPA. The persistence details
 * ({@code ProductEntity}, its 1:1 detail and the rating view) stay in {@code infrastructure}.
 *
 * @param id            product identifier
 * @param sku           business key
 * @param name          canonical name
 * @param displayName   storefront name
 * @param description   storefront description
 * @param unitPrice     unit price
 * @param category      category name
 * @param stock         units in stock
 * @param ratingAverage average review score (0 when there are no reviews)
 * @param reviewCount   number of reviews
 */
public record Product(
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
}
