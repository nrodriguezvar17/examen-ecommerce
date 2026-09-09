package com.grupobolivar.ecommerce.checkout.domain.order;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;

/**
 * A persisted order line — a snapshot of what was bought (name, price and category at
 * purchase time), so the order does not change if the product changes later.
 */
public record OrderLine(
		long productId,
		String productName,
		Money unitPrice,
		String categoryName,
		int quantity,
		Money lineTotal) {
}
