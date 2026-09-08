package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * Patrón <b>Strategy</b>: cada tipo de descuento acumulativo se encapsula en una
 * implementación independiente y testeable en aislamiento. El {@link DiscountPipeline}
 * las ejecuta en cascada, pasando a cada una el total corriente (ya afectado por las
 * reglas anteriores).
 */
public interface DiscountRule {

	/** Tipo de descuento que representa esta regla (define también su orden). */
	DiscountType type();

	/**
	 * Monto a descontar por esta regla sobre el total corriente.
	 *
	 * @param context      carrito y cupón
	 * @param runningTotal total tras aplicar las reglas anteriores
	 * @return el descuento (o {@link Money#ZERO} si la regla no aplica)
	 */
	Money computeDiscount(DiscountContext context, Money runningTotal);
}
