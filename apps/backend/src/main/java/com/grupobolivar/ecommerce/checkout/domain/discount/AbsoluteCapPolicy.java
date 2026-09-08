package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.List;

/**
 * Regla 4 - Límite de Descuento Absoluto: el descuento total consolidado nunca puede
 * superar un porcentaje del valor total original (35% en el enunciado). Si la cascada
 * de reglas produce un descuento mayor, se trunca exactamente en el tope y el valor
 * final se recalcula sobre ese límite.
 *
 * <p>Es un post-procesador del {@link DiscountPipeline}, no una {@link DiscountRule}:
 * no descuenta, acota.
 */
public final class AbsoluteCapPolicy {

	private final BigDecimal capRate;

	public AbsoluteCapPolicy(BigDecimal capRate) {
		this.capRate = capRate;
	}

	/**
	 * @param originalTotal total antes de descuentos
	 * @param lines         descuentos calculados por la cascada
	 * @param runningTotal  total tras aplicar la cascada
	 * @return el desglose final, truncado al tope si corresponde
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
