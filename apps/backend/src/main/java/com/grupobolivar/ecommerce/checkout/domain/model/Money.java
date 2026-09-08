package com.grupobolivar.ecommerce.checkout.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable monetary value object. All discount-engine arithmetic uses {@link BigDecimal}
 * with scale 2 and HALF_UP rounding to guarantee the "exact itemised totals" required by
 * HU3 and to avoid the floating-point drift of {@code double}.
 */
public final class Money {

	public static final Money ZERO = Money.of(BigDecimal.ZERO);

	private static final int SCALE = 2;
	private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
	private static final int RATE_SCALE = 4;

	private final BigDecimal amount;

	private Money(BigDecimal raw) {
		this.amount = raw.setScale(SCALE, ROUNDING);
	}

	public static Money of(BigDecimal value) {
		return new Money(Objects.requireNonNull(value, "value"));
	}

	public static Money of(String value) {
		return new Money(new BigDecimal(value));
	}

	public Money plus(Money other) {
		return new Money(this.amount.add(other.amount));
	}

	public Money minus(Money other) {
		return new Money(this.amount.subtract(other.amount));
	}

	public Money times(int quantity) {
		return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
	}

	/** Applies a rate (e.g. 0.10) to this amount. */
	public Money applyRate(BigDecimal rate) {
		return new Money(this.amount.multiply(rate));
	}

	public boolean isPositive() {
		return this.amount.signum() > 0;
	}

	public boolean isGreaterThan(Money other) {
		return this.amount.compareTo(other.amount) > 0;
	}

	/** This amount as a fraction of {@code base}, with 4 decimals (0 when base is 0). */
	public BigDecimal rateOf(Money base) {
		if (base.amount.signum() == 0) {
			return BigDecimal.ZERO.setScale(RATE_SCALE, ROUNDING);
		}
		return this.amount.divide(base.amount, RATE_SCALE, ROUNDING);
	}

	public BigDecimal value() {
		return this.amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Money money)) {
			return false;
		}
		return this.amount.compareTo(money.amount) == 0;
	}

	@Override
	public int hashCode() {
		return this.amount.stripTrailingZeros().hashCode();
	}

	@Override
	public String toString() {
		return this.amount.toPlainString();
	}
}
