package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.List;

/**
 * Patrón <b>Factory</b>: centraliza la construcción del {@link DiscountPipeline} con
 * las reglas en su <b>orden de precedencia</b> (Categoría -> Volumen -> Cupón) y el
 * tope. El resto del sistema no instancia reglas a mano ni conoce el orden.
 */
public final class DiscountRuleFactory {

	private DiscountRuleFactory() {
	}

	public static DiscountPipeline createPipeline(DiscountConfig config, CouponCatalog couponCatalog) {
		List<DiscountRule> orderedRules = List.of(
				new CategoryDiscountRule(config.categoryName(), config.categoryPercent()),
				new VolumeDiscountRule(Money.of(config.volumeThreshold()), config.volumePercent()),
				new CouponDiscountRule(couponCatalog));

		return new DiscountPipeline(orderedRules, new AbsoluteCapPolicy(config.capPercent()));
	}
}
