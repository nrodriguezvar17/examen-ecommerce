package com.grupobolivar.ecommerce.catalog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Maps the {@code product_details} table (1:1 with {@code products}). Read-only here. */
@Entity
@Immutable
@Table(name = "product_details")
public class ProductDetailEntity {

	@Id
	@Column(name = "product_id")
	private Long productId;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	private String description;

	private String brand;

	@Column(name = "image_url")
	private String imageUrl;

	protected ProductDetailEntity() {
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public String getBrand() {
		return brand;
	}

	public String getImageUrl() {
		return imageUrl;
	}
}
