package com.grupobolivar.ecommerce.checkout.domain.discount;

import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.context;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.item;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.rate;
import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import org.junit.jupiter.api.Test;

class VolumeDiscountRuleTest {

	private final VolumeDiscountRule rule = new VolumeDiscountRule(Money.of("100.00"), rate("0.05"));

	private final DiscountContext anyContext = context(null, item("Papelería", "1.00", 1));

	@Test
	void doesNotApplyWhenTheRunningTotalEqualsTheThreshold() {
		assertThat(rule.computeDiscount(anyContext, Money.of("100.00"))).isEqualTo(Money.ZERO);
	}

	@Test
	void appliesWhenTheRunningTotalIsJustAboveTheThreshold() {
		// 5% of 100.01 = 5.0005 -> 5.00
		assertThat(rule.computeDiscount(anyContext, Money.of("100.01")).value())
				.isEqualByComparingTo("5.00");
	}

	@Test
	void appliesOverTheRunningTotalNotTheOriginal() {
		assertThat(rule.computeDiscount(anyContext, Money.of("785.00")).value())
				.isEqualByComparingTo("39.25");
	}

	@Test
	void isNamedAsTheVolumeDiscount() {
		assertThat(rule.type()).isEqualTo(DiscountType.VOLUME);
	}
}
