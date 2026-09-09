package com.grupobolivar.ecommerce.catalog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Maps the {@code products} table plus its 1:1 detail and rating rows. */
@Entity
@Table(name = "products")
public class ProductEntity {

	@Id
	private Long id;

	@Column(nullable = false)
	private String sku;

	@Column(nullable = false)
	private String name;

	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false)
	private boolean active;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;

	@OneToOne(optional = false)
	@PrimaryKeyJoinColumn
	private ProductDetailEntity detail;

	@OneToOne(optional = false)
	@PrimaryKeyJoinColumn
	private ProductRatingView rating;

	protected ProductEntity() {
	}

	public Long getId() {
		return id;
	}

	public String getSku() {
		return sku;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public int getStock() {
		return stock;
	}

	public boolean isActive() {
		return active;
	}

	public CategoryEntity getCategory() {
		return category;
	}

	public ProductDetailEntity getDetail() {
		return detail;
	}

	public ProductRatingView getRating() {
		return rating;
	}
}
