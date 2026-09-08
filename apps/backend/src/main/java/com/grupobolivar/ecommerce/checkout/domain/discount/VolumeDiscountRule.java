package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;

/**
 * Regla 2 - Descuento por Volumen: si el total corriente (ya con el descuento de
 * categoría aplicado) <b>supera</b> el umbral configurado (p. ej. $100), aplica un
 * porcentaje adicional sobre ese total corriente. El umbral es estricto: exactamente
 * $100 no dispara el descuento.
 */
public final class VolumeDiscountRule implements DiscountRule {

	private final Money threshold;
	private final BigDecimal percent;

	public VolumeDiscountRule(Money threshold, BigDecimal percent) {
		this.threshold = threshold;
		this.percent = percent;
	}

	@Override
	public DiscountType type() {
		return DiscountType.VOLUME;
	}

	@Override
	public Money computeDiscount(DiscountContext context, Money runningTotal) {
		if (!runningTotal.isGreaterThan(threshold)) {
			return Money.ZERO;
		}
		return runningTotal.applyRate(percent);
	}
}
