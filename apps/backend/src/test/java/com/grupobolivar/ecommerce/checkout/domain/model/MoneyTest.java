package com.grupobolivar.ecommerce.checkout.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

	@Test
	void normalisesToTwoDecimalsWithHalfUpRounding() {
		assertThat(Money.of("10.005").value()).isEqualByComparingTo("10.01");
		assertThat(Money.of("10.004").value()).isEqualByComparingTo("10.00");
	}

	@Test
	void addsSubtractsAndMultiplies() {
		Money ten = Money.of("10.00");
		assertThat(ten.plus(Money.of("5.50")).value()).isEqualByComparingTo("15.50");
		assertThat(ten.minus(Money.of("3.25")).value()).isEqualByComparingTo("6.75");
		assertThat(ten.times(3).value()).isEqualByComparingTo("30.00");
	}

	@Test
	void appliesARateRoundingTheResult() {
		assertThat(Money.of("745.75").applyRate(new BigDecimal("0.15")).value())
				.isEqualByComparingTo("111.86");
	}

	@Test
	void comparesAmounts() {
		assertThat(Money.of("100.01").isGreaterThan(Money.of("100.00"))).isTrue();
		assertThat(Money.of("100.00").isGreaterThan(Money.of("100.00"))).isFalse();
		assertThat(Money.of("0.01").isPositive()).isTrue();
		assertThat(Money.ZERO.isPositive()).isFalse();
	}

	@Test
	void computesTheRateOfABaseWithFourDecimals() {
		assertThat(Money.of("236.11").rateOf(Money.of("870.00"))).isEqualByComparingTo("0.2714");
		assertThat(Money.of("50.00").rateOf(Money.ZERO)).isEqualByComparingTo("0.0000");
	}

	@Test
	void equalityIgnoresTrailingZeros() {
		assertThat(Money.of("10.0")).isEqualTo(Money.of("10.00"));
		assertThat(Money.of("10.00")).isNotEqualTo(Money.of("10.01"));
	}
}
