package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Cart;

/**
 * Input to the discount engine: plain data, no HTTP and no persistence.
 *
 * @param cart       validated cart
 * @param couponCode coupon code entered by the user (may be {@code null} or blank)
 */
public record DiscountContext(Cart cart, String couponCode) {

	public boolean hasCoupon() {
		return couponCode != null && !couponCode.isBlank();
	}
}
