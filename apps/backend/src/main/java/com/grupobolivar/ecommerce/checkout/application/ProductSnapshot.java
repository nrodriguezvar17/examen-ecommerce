package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/** Product data the checkout needs, resolved server-side (never trusted from the client). */
public record ProductSnapshot(long productId, String name, Money unitPrice, String category, int stock) {
}
