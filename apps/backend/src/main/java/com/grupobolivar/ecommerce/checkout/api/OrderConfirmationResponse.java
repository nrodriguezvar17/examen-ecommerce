package com.grupobolivar.ecommerce.checkout.api;

import com.grupobolivar.ecommerce.checkout.application.OrderConfirmation;
import java.time.Instant;

/** Response of {@code POST /api/checkout}: the persisted order's key data + the breakdown. */
public record OrderConfirmationResponse(
		String radicado,
		Instant createdAt,
		String status,
		QuoteResponse breakdown) {

	public static OrderConfirmationResponse from(OrderConfirmation confirmation) {
		return new OrderConfirmationResponse(
				confirmation.radicado(),
				confirmation.createdAt(),
				confirmation.status().name(),
				QuoteResponse.from(confirmation.breakdown()));
	}
}
