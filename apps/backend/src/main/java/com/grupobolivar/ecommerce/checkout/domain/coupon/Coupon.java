package com.grupobolivar.ecommerce.checkout.domain.coupon;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Cupón promocional. El porcentaje se expresa como fracción (0.15 = 15%).
 */
public record Coupon(String code, BigDecimal percent, boolean active) {

	public Coupon {
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(percent, "percent");
		if (percent.signum() < 0) {
			throw new IllegalArgumentException("percent no puede ser negativo");
		}
	}
}
