package com.grupobolivar.ecommerce.catalog.domain;

import java.util.List;

/**
 * Full product view for the detail screen: the catalog {@link Product} (which already
 * carries the image) plus the {@code brand} from the 1:1 detail table and the reviews.
 *
 * @param product core catalog data (id, price, stock, image, rating summary…)
 * @param brand   manufacturer / brand (may be {@code null})
 * @param reviews reviews, newest first
 */
public record ProductDetail(
		Product product,
		String brand,
		List<Review> reviews) {

	public ProductDetail {
		reviews = List.copyOf(reviews);
	}
}
