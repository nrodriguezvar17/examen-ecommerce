package com.grupobolivar.ecommerce.catalog.domain;

import java.util.List;

/**
 * Full product view for the detail screen: the catalog {@link Product} plus the fields that
 * live in the 1:1 detail table ({@code brand}, {@code imageUrl}) and the list of reviews.
 *
 * @param product core catalog data (id, price, stock, rating summary…)
 * @param brand   manufacturer / brand (may be {@code null})
 * @param imageUrl storefront image URL (may be {@code null})
 * @param reviews  reviews, newest first
 */
public record ProductDetail(
		Product product,
		String brand,
		String imageUrl,
		List<Review> reviews) {

	public ProductDetail {
		reviews = List.copyOf(reviews);
	}
}
