package com.grupobolivar.ecommerce.checkout.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/** Maps the {@code coupons} table. */
@Entity
@Table(name = "coupons")
public class CouponEntity {

	@Id
	private String code;

	private String description;

	@Column(name = "discount_rate", nullable = false)
	private BigDecimal discountRate;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "valid_from")
	private Instant validFrom;

	@Column(name = "valid_until")
	private Instant validUntil;

	protected CouponEntity() {
	}

	public String getCode() {
		return code;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public boolean isActive() {
		return active;
	}

	public Instant getValidFrom() {
		return validFrom;
	}

	public Instant getValidUntil() {
		return validUntil;
	}
}
