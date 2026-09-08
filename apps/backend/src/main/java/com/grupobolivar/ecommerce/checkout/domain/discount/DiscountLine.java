package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * Una entrada del desglose: cuánto descontó una regla concreta.
 */
public record DiscountLine(DiscountType type, Money amount) {
}
