package com.grupobolivar.ecommerce.catalog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

/**
 * Read model backed by the {@code product_rating_summary} view. Uses {@code @Subselect} so
 * Hibernate schema validation does not treat it as a table.
 */
@Entity
@Immutable
@Subselect("select product_id, review_count, rating_average from product_rating_summary")
@Synchronize({ "products", "product_reviews" })
public class ProductRatingView {

	@Id
	@Column(name = "product_id")
	private Long productId;

	@Column(name = "review_count", nullable = false)
	private long reviewCount;

	@Column(name = "rating_average", nullable = false)
	private BigDecimal ratingAverage;

	protected ProductRatingView() {
	}

	public long getReviewCount() {
		return reviewCount;
	}

	public BigDecimal getRatingAverage() {
		return ratingAverage;
	}
}
