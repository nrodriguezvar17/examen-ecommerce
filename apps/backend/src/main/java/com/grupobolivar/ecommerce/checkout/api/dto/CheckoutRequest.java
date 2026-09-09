package com.grupobolivar.ecommerce.checkout.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Body of {@code POST /api/checkout}. Same shape as the quote request. */
public record CheckoutRequest(@NotEmpty List<@Valid CartItemRequest> items, String couponCode) {
}
