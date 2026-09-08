package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.List;

/**
 * Immutable result of the discount engine. The API layer consumes it to build the response
 * (HU2: breakdown, effective rate, total savings, final amount; HU4: {@code capReached}).
 *
 * @param originalTotal original subtotal (before discounts)
 * @param lines         discounts applied, in precedence order
 * @param totalDiscount consolidated savings (already truncated to the cap if applicable)
 * @param effectiveRate effective discount rate over the original total (0..1, 4 decimals)
 * @param finalTotal    final amount to pay
 * @param capReached    true when the 35% cap was reached/exceeded and truncated (drives HU4)
 */
public record DiscountBreakdown(
		Money originalTotal,
		List<DiscountLine> lines,
		Money totalDiscount,
		BigDecimal effectiveRate,
		Money finalTotal,
		boolean capReached) {

	public DiscountBreakdown {
		lines = List.copyOf(lines);
	}
}
