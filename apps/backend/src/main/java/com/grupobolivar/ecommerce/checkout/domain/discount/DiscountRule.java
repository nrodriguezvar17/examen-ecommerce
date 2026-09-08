package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * <b>Strategy</b> pattern: each cumulative discount type is encapsulated in an independent
 * implementation that can be tested in isolation. {@link DiscountPipeline} runs them as a
 * chain, passing each one the running total (already affected by the previous rules).
 */
public interface DiscountRule {

	/** Discount type this rule represents (also defines its order). */
	DiscountType type();

	/**
	 * Amount this rule discounts off the running total.
	 *
	 * @param context      cart and coupon
	 * @param runningTotal total after applying the previous rules
	 * @return the discount (or {@link Money#ZERO} if the rule does not apply)
	 */
	Money computeDiscount(DiscountContext context, Money runningTotal);
}
