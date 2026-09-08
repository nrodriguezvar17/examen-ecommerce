package com.grupobolivar.ecommerce.checkout.domain.model;

import java.util.Objects;

/**
 * A validated cart line. The domain knows nothing about database IDs or JSON: it receives
 * plain data and computes.
 *
 * @param productId product identifier in the catalog
 * @param name      name for the breakdown
 * @param unitPrice unit price
 * @param quantity  quantity (&gt; 0, validated at the application boundary)
 * @param category  product category (e.g. "Tecnología")
 */
public record CartItem(long productId, String name, Money unitPrice, int quantity, String category) {

	public CartItem {
		Objects.requireNonNull(unitPrice, "unitPrice");
		Objects.requireNonNull(category, "category");
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be > 0, was: " + quantity);
		}
	}

	public Money lineTotal() {
		return unitPrice.times(quantity);
	}

	public boolean isCategory(String categoryName) {
		return this.category.equalsIgnoreCase(categoryName);
	}
}
