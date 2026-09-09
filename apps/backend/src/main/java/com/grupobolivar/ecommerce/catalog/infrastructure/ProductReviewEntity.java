package com.grupobolivar.ecommerce.catalog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.Immutable;

/** Maps the {@code product_reviews} table. Read-only. */
@Entity
@Immutable
@Table(name = "product_reviews")
public class ProductReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(nullable = false)
	private short rating;

	private String title;

	private String comment;

	@Column(name = "reviewer_name", nullable = false)
	private String reviewerName;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	protected ProductReviewEntity() {
	}

	public Long getId() {
		return id;
	}

	public short getRating() {
		return rating;
	}

	public String getTitle() {
		return title;
	}

	public String getComment() {
		return comment;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
