package com.grupobolivar.ecommerce.catalog.api;

import com.grupobolivar.ecommerce.catalog.domain.ProductDetail;
import com.grupobolivar.ecommerce.catalog.domain.Review;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Full product view returned by {@code GET /api/products/{id}}. */
public record ProductDetailResponse(
		long id,
		String sku,
		String name,
		String displayName,
		String description,
		String brand,
		String imageUrl,
		BigDecimal unitPrice,
		String category,
		int stock,
		BigDecimal ratingAverage,
		long reviewCount,
		List<ReviewResponse> reviews) {

	public record ReviewResponse(long id, int rating, String title, String comment,
			String reviewerName, Instant createdAt) {
	}

	public static ProductDetailResponse from(ProductDetail detail) {
		var product = detail.product();
		return new ProductDetailResponse(
				product.id(), product.sku(), product.name(), product.displayName(),
				product.description(), detail.brand(), product.imageUrl(), product.unitPrice(),
				product.category(), product.stock(), product.ratingAverage(), product.reviewCount(),
				detail.reviews().stream().map(ProductDetailResponse::review).toList());
	}

	private static ReviewResponse review(Review review) {
		return new ReviewResponse(review.id(), review.rating(), review.title(), review.comment(),
				review.reviewerName(), review.createdAt());
	}
}
