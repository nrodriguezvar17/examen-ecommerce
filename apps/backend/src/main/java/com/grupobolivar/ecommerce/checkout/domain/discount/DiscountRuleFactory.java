package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.List;

/**
 * <b>Factory</b> pattern: centralises building the {@link DiscountPipeline} with the rules
 * in their <b>precedence order</b> (Category -&gt; Volume -&gt; Coupon) and the cap. The
 * rest of the system never instantiates rules by hand nor knows the order.
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
