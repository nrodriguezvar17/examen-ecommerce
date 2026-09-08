package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Cart;

/**
 * Entrada del motor de descuentos: datos planos, sin HTTP ni persistencia.
 *
 * @param cart       carrito validado
 * @param couponCode código de cupón ingresado (puede ser {@code null} o vacío)
 */
public record DiscountContext(Cart cart, String couponCode) {

	public boolean hasCoupon() {
		return couponCode != null && !couponCode.isBlank();
	}
}
