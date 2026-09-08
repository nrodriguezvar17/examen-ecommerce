package com.grupobolivar.ecommerce.checkout.domain.coupon;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Promotional coupon. The percentage is expressed as a fraction (0.15 = 15%).
 */
public record Coupon(String code, BigDecimal percent, boolean active) {

	public Coupon {
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(percent, "percent");
		if (percent.signum() < 0) {
			throw new IllegalArgumentException("percent must not be negative");
		}
	}
}
