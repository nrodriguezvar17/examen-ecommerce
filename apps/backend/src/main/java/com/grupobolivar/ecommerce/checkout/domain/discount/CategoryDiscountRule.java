package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;

/**
 * Rule 1 - Category discount: if the cart contains at least one product of the configured
 * category (e.g. "Tecnología"), applies a percentage over the subtotal of <b>those
 * specific products</b>, not over the whole cart.
 */
public final class CategoryDiscountRule implements DiscountRule {

	private final String categoryName;
	private final BigDecimal percent;

	public CategoryDiscountRule(String categoryName, BigDecimal percent) {
		this.categoryName = categoryName;
		this.percent = percent;
	}

	@Override
	public DiscountType type() {
		return DiscountType.CATEGORY;
	}

	@Override
	public Money computeDiscount(DiscountContext context, Money runningTotal) {
		Money categorySubtotal = context.cart().subtotalForCategory(categoryName);
		if (!categorySubtotal.isPositive()) {
			return Money.ZERO;
		}
		return categorySubtotal.applyRate(percent);
	}
}
