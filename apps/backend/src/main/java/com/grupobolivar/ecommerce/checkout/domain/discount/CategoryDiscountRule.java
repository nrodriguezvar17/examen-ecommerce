package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;

/**
 * Regla 1 - Descuento de Categoría: si el carrito contiene al menos un producto de la
 * categoría configurada (p. ej. "Tecnología"), aplica un porcentaje sobre el subtotal
 * de <b>esos productos específicos</b>, no sobre todo el carrito.
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
