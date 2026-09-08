package com.grupobolivar.ecommerce.checkout.domain.discount;

import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.TECH;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.context;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.item;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.rate;
import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import org.junit.jupiter.api.Test;

class CategoryDiscountRuleTest {

	private final CategoryDiscountRule rule = new CategoryDiscountRule(TECH, rate("0.10"));

	@Test
	void discountsTenPercentOfTheCategorySubtotalOnly() {
		DiscountContext ctx = context(null,
				item(TECH, "800.00", 1),
				item(TECH, "25.00", 2),
				item("Papelería", "5.00", 4));

		// 10% of (800 + 50) = 85.00; the 20.00 of Papelería is ignored.
		assertThat(rule.computeDiscount(ctx, Money.of("870.00")).value()).isEqualByComparingTo("85.00");
	}

	@Test
	void doesNotApplyWhenThereAreNoCategoryItems() {
		DiscountContext ctx = context(null,
				item("Libros", "30.00", 2),
				item("Papelería", "3.00", 5));

		assertThat(rule.computeDiscount(ctx, Money.of("75.00"))).isEqualTo(Money.ZERO);
	}

	@Test
	void isNamedAsTheCategoryDiscount() {
		assertThat(rule.type()).isEqualTo(DiscountType.CATEGORY);
	}
}
