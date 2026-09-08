package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor de descuentos acumulativos. Aplica las {@link DiscountRule} en cadena
 * (patrón <b>Chain of Responsibility / Pipeline</b>): cada regla opera sobre el total
 * corriente resultante de la anterior — de ahí la cascada multiplicativa, no aditiva —
 * y al final delega en {@link AbsoluteCapPolicy} el tope del 35%.
 *
 * <p>Clase pura: sin Spring, sin JPA, sin HTTP. Recibe un {@link DiscountContext} y
 * devuelve un {@link DiscountBreakdown}.
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
