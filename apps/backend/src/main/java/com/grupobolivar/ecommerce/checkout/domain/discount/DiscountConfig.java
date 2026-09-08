package com.grupobolivar.ecommerce.checkout.domain.discount;

import java.math.BigDecimal;

/**
 * Engine configuration, expressed in domain types (no Spring annotations). The
 * infrastructure layer reads the external properties and builds this record before passing
 * it to {@link DiscountRuleFactory}.
 */
public record DiscountConfig(
		String categoryName,
		BigDecimal categoryPercent,
		BigDecimal volumeThreshold,
		BigDecimal volumePercent,
		BigDecimal capPercent) {
}
