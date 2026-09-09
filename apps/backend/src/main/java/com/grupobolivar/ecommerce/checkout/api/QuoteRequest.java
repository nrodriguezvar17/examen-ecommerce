package com.grupobolivar.ecommerce.checkout.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Body of {@code POST /api/checkout/quote}. */
public record QuoteRequest(@NotEmpty List<@Valid CartItemRequest> items, String couponCode) {
}
