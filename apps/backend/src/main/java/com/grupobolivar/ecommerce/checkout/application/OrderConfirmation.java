package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import java.time.Instant;

/** What {@link CheckoutService#checkout} returns: the persisted order's key data + the breakdown. */
public record OrderConfirmation(
		String radicado,
		Instant createdAt,
		OrderStatus status,
		DiscountBreakdown breakdown) {
}
