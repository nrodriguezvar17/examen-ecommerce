package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.ArrayList;
import java.util.List;

/**
 * Cumulative discount engine. Applies the {@link DiscountRule}s as a chain (<b>Chain of
 * Responsibility / Pipeline</b> pattern): each rule operates on the running total left by
 * the previous one — hence the multiplicative cascade, not an additive sum — and finally
 * delegates the 35% cap to {@link AbsoluteCapPolicy}.
 *
 * <p>Pure class: no Spring, no JPA, no HTTP. Takes a {@link DiscountContext} and returns a
 * {@link DiscountBreakdown}.
 */
public final class DiscountPipeline {

	private final List<DiscountRule> rules;
	private final AbsoluteCapPolicy capPolicy;

	public DiscountPipeline(List<DiscountRule> rules, AbsoluteCapPolicy capPolicy) {
		this.rules = List.copyOf(rules);
		this.capPolicy = capPolicy;
	}

	public DiscountBreakdown calculate(DiscountContext context) {
		Money originalTotal = context.cart().originalTotal();
		Money runningTotal = originalTotal;
		List<DiscountLine> lines = new ArrayList<>();

		for (DiscountRule rule : rules) {
			Money discount = rule.computeDiscount(context, runningTotal);
			if (discount.isPositive()) {
				lines.add(new DiscountLine(rule.type(), discount));
				runningTotal = runningTotal.minus(discount);
			}
		}

		return capPolicy.enforce(originalTotal, lines, runningTotal);
	}
}
