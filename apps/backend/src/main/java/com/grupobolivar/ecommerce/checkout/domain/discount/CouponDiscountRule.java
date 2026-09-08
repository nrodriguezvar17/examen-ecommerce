package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * Rule 3 - Coupon discount: if an active coupon code was entered, applies its percentage
 * over the running total (already affected by rules 1 and 2). A missing, inactive or blank
 * coupon produces no discount.
 */
public final class CouponDiscountRule implements DiscountRule {

	private final CouponCatalog couponCatalog;

	public CouponDiscountRule(CouponCatalog couponCatalog) {
		this.couponCatalog = couponCatalog;
	}

	@Override
	public DiscountType type() {
		return DiscountType.COUPON;
	}

	@Override
	public Money computeDiscount(DiscountContext context, Money runningTotal) {
		if (!context.hasCoupon()) {
			return Money.ZERO;
		}
		return couponCatalog.findActive(context.couponCode())
				.map(coupon -> runningTotal.applyRate(coupon.percent()))
				.orElse(Money.ZERO);
	}
}
