package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * One entry of the breakdown: how much a given rule discounted.
 */
public record DiscountLine(DiscountType type, Money amount) {
}
