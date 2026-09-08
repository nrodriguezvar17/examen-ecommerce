package com.grupobolivar.ecommerce.catalog.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Maps the {@code categories} table. */
@Entity
@Table(name = "categories")
public class CategoryEntity {

	@Id
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "discount_rate", nullable = false)
	private BigDecimal discountRate;

	protected CategoryEntity() {
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}
}
