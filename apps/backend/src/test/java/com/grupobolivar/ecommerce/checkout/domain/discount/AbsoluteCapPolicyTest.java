package com.grupobolivar.ecommerce.checkout.domain.discount;

import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.rate;
import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.List;
import org.junit.jupiter.api.Test;

class AbsoluteCapPolicyTest {

	private final AbsoluteCapPolicy policy = new AbsoluteCapPolicy(rate("0.35"));
	private final Money original = Money.of("1000.00");

	@Test
	void truncatesTheDiscountExactlyAtThirtyFivePercentWhenExceeded() {
		// Cascade left a running total of 580.00 -> raw discount 42%.
		DiscountBreakdown result = policy.enforce(original, List.of(), Money.of("580.00"));

		assertThat(result.capReached()).isTrue();
		assertThat(result.totalDiscount().value()).isEqualByComparingTo("350.00");
		assertThat(result.finalTotal().value()).isEqualByComparingTo("650.00");
		assertThat(result.effectiveRate()).isEqualByComparingTo("0.35");
	}

	@Test
	void leavesTheDiscountUntouchedWhenItEqualsTheCap() {
		DiscountBreakdown result = policy.enforce(original, List.of(), Money.of("650.00"));

		assertThat(result.capReached()).isFalse();
		assertThat(result.totalDiscount().value()).isEqualByComparingTo("350.00");
		assertThat(result.finalTotal().value()).isEqualByComparingTo("650.00");
	}

	@Test
	void leavesTheDiscountUntouchedWhenBelowTheCap() {
		DiscountBreakdown result = policy.enforce(original, List.of(), Money.of("800.00"));

		assertThat(result.capReached()).isFalse();
		assertThat(result.totalDiscount().value()).isEqualByComparingTo("200.00");
		assertThat(result.effectiveRate()).isEqualByComparingTo("0.2000");
	}
}
