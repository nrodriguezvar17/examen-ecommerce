package com.grupobolivar.ecommerce.checkout.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Immutable cart. Exposes the aggregates the discount engine needs: the original total and
 * the per-category subtotal.
 */
public record Cart(List<CartItem> items) {

	public Cart {
		Objects.requireNonNull(items, "items");
		if (items.isEmpty()) {
			throw new IllegalArgumentException("Cart must not be empty");
		}
		items = List.copyOf(items);
	}

	/** Sum of (unitPrice * quantity) over every line: the "original subtotal". */
	public Money originalTotal() {
		return items.stream().map(CartItem::lineTotal).reduce(Money.ZERO, Money::plus);
	}

	/** Subtotal of the lines that belong to the given category. */
	public Money subtotalForCategory(String categoryName) {
		return items.stream()
				.filter(item -> item.isCategory(categoryName))
				.map(CartItem::lineTotal)
				.reduce(Money.ZERO, Money::plus);
	}

	public boolean hasCategory(String categoryName) {
		return items.stream().anyMatch(item -> item.isCategory(categoryName));
	}
}
