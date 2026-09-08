package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;

/**
 * Rule 2 - Volume discount: if the running total (already with the category discount
 * applied) is <b>strictly greater</b> than the configured threshold (e.g. $100), applies an
 * extra percentage over that running total. The threshold is strict: exactly $100 does not
 * trigger the discount.
 */
public final class VolumeDiscountRule implements DiscountRule {

	private final Money threshold;
	private final BigDecimal percent;

	public VolumeDiscountRule(Money threshold, BigDecimal percent) {
		this.threshold = threshold;
		this.percent = percent;
	}

	@Override
	public DiscountType type() {
		return DiscountType.VOLUME;
	}

	@Override
	public Money computeDiscount(DiscountContext context, Money runningTotal) {
		if (!runningTotal.isGreaterThan(threshold)) {
			return Money.ZERO;
		}
		return runningTotal.applyRate(percent);
	}
}
