package com.grupobolivar.ecommerce.checkout.application.command;

import java.util.List;

/** Input to {@link CheckoutService#checkout}: the cart lines and an optional coupon code. */
public record CheckoutCommand(List<QuoteCommand.Line> items, String couponCode) {
}
