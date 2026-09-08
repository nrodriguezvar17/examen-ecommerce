package com.grupobolivar.ecommerce.checkout.domain.model;

import java.util.Objects;

/**
 * Línea del carrito ya validada. El dominio no conoce IDs de base de datos ni JSON:
 * recibe datos planos y calcula.
 *
 * @param productId  identificador del producto en el catálogo
 * @param name       nombre para el desglose
 * @param unitPrice  precio unitario
 * @param quantity   cantidad (> 0, se valida en el borde de la aplicación)
 * @param category   categoría del producto (p. ej. "Tecnología")
 */
public record CartItem(long productId, String name, Money unitPrice, int quantity, String category) {

	public CartItem {
		Objects.requireNonNull(unitPrice, "unitPrice");
		Objects.requireNonNull(category, "category");
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity debe ser > 0, fue: " + quantity);
		}
	}

	public Money lineTotal() {
		return unitPrice.times(quantity);
	}

	public boolean isCategory(String categoryName) {
		return this.category.equalsIgnoreCase(categoryName);
	}
}
