package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado inmutable del motor de descuentos. Es lo que consume la capa de API para
 * armar la respuesta (HU2: desglose, % efectivo, ahorro total, valor final; HU4: {@code capReached}).
 *
 * @param originalTotal  subtotal original (antes de descuentos)
 * @param lines          descuentos aplicados, en orden de precedencia
 * @param totalDiscount  ahorro total consolidado (ya truncado al tope si corresponde)
 * @param effectiveRate  porcentaje de descuento efectivo sobre el total original (0..1, 4 decimales)
 * @param finalTotal     valor final a pagar
 * @param capReached     true si se alcanzó/superó el tope del 35% y se truncó (dispara la alerta de HU4)
 */
public record DiscountBreakdown(
		Money originalTotal,
		List<DiscountLine> lines,
		Money totalDiscount,
		BigDecimal effectiveRate,
		Money finalTotal,
		boolean capReached) {

	public DiscountBreakdown {
		lines = List.copyOf(lines);
	}
}
