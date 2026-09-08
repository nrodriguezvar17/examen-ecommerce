package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.List;

/**
 * Rule 4 - Absolute discount cap: the consolidated total discount may never exceed a
 * percentage of the original total (35% in the assignment). If the rule cascade produces a
 * larger discount, it is truncated exactly at the cap and the final amount is recomputed on
 * that limit.
 *
 * <p>This is a post-processor of {@link DiscountPipeline}, not a {@link DiscountRule}: it
 * does not discount, it bounds.
 */
public final class AbsoluteCapPolicy {

	private final BigDecimal capRate;

	public AbsoluteCapPolicy(BigDecimal capRate) {
		this.capRate = capRate;
	}

	/**
	 * @param originalTotal total before discounts
	 * @param lines         discounts computed by the cascade
	 * @param runningTotal  total after applying the cascade
	 * @return the final breakdown, truncated to the cap if applicable
	 */
	public DiscountBreakdown enforce(Money originalTotal, List<DiscountLine> lines, Money runningTotal) {
		Money rawDiscount = originalTotal.minus(runningTotal);
		BigDecimal rawRate = rawDiscount.rateOf(originalTotal);

		if (rawRate.compareTo(capRate) > 0) {
			Money cappedDiscount = originalTotal.applyRate(capRate);
			Money cappedFinal = originalTotal.minus(cappedDiscount);
			return new DiscountBreakdown(originalTotal, lines, cappedDiscount, capRate, cappedFinal, true);
		}

		return new DiscountBreakdown(originalTotal, lines, rawDiscount, rawRate, runningTotal, false);
	}
}
