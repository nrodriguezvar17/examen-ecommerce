package com.grupobolivar.ecommerce.checkout.api;

import jakarta.validation.constraints.Positive;

/** A cart line in a checkout/quote request. */
public record CartItemRequest(@Positive long productId, @Positive int quantity) {
}
