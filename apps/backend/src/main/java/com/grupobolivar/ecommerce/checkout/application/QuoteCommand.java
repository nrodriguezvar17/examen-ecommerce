package com.grupobolivar.ecommerce.checkout.application;

import java.util.List;

/** Input to {@link CheckoutService#quote}: the cart lines and an optional coupon code. */
public record QuoteCommand(List<Line> items, String couponCode) {

	public record Line(long productId, int quantity) {
	}
}
