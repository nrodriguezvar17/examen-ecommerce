package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * Regla 3 - Descuento por Cupón: si se ingresó un código de cupón activo, aplica su
 * porcentaje sobre el total corriente (ya afectado por las reglas 1 y 2). Un cupón
 * inexistente, inactivo o vacío no produce descuento.
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
