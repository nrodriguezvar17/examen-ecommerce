package com.grupobolivar.ecommerce.checkout.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/** Body of {@code POST /api/checkout/quote}. */
public record QuoteRequest(@NotEmpty List<@Valid Item> items, String couponCode) {

	public record Item(@Positive long productId, @Positive int quantity) {
	}
}
