package com.grupobolivar.ecommerce.checkout.domain.order;

import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;

/** Raw discount produced by one rule (before the 35% cap), as persisted on the order. */
public record OrderDiscount(DiscountType type, BigDecimal rate, Money amount) {
}
