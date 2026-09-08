package com.grupobolivar.ecommerce.checkout.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Carrito inmutable. Expone los agregados que necesita el motor de descuentos:
 * el total original y el subtotal por categoría.
 */
public record Cart(List<CartItem> items) {

	public Cart {
		Objects.requireNonNull(items, "items");
		if (items.isEmpty()) {
			throw new IllegalArgumentException("El carrito no puede estar vacío");
		}
		items = List.copyOf(items);
	}

	/** Suma de (precioUnitario x cantidad) de todas las líneas: el "subtotal original". */
	public Money originalTotal() {
		return items.stream().map(CartItem::lineTotal).reduce(Money.ZERO, Money::plus);
	}

	/** Subtotal de las líneas que pertenecen a la categoría indicada. */
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
