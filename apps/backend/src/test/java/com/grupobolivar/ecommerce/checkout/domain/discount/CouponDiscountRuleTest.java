package com.grupobolivar.ecommerce.checkout.domain.discount;

import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.context;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.item;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.noCoupons;
import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CouponDiscountRuleTest {

	private final CouponCatalog welcome = code ->
			"WELCOME2026".equals(code)
					? Optional.of(new Coupon(code, new BigDecimal("0.15"), true))
					: Optional.empty();

	@Test
	void appliesFifteenPercentOfTheRunningTotalForAnActiveCoupon() {
		DiscountContext ctx = context("WELCOME2026", item("Papelería", "1.00", 1));

		assertThat(new CouponDiscountRule(welcome).computeDiscount(ctx, Money.of("745.75")).value())
				.isEqualByComparingTo("111.86");
	}

	@Test
	void doesNotApplyWhenNoCouponCodeIsEntered() {
		DiscountContext ctx = context("  ", item("Papelería", "1.00", 1));

		assertThat(new CouponDiscountRule(welcome).computeDiscount(ctx, Money.of("745.75")))
				.isEqualTo(Money.ZERO);
	}

	@Test
	void doesNotApplyWhenTheCouponIsUnknownOrInactive() {
		DiscountContext ctx = context("BLACKFRIDAY2025", item("Papelería", "1.00", 1));

		assertThat(new CouponDiscountRule(noCoupons()).computeDiscount(ctx, Money.of("745.75")))
				.isEqualTo(Money.ZERO);
	}

	@Test
	void isNamedAsTheCouponDiscount() {
		assertThat(new CouponDiscountRule(welcome).type()).isEqualTo(DiscountType.COUPON);
	}
}
