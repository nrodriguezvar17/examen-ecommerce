package com.grupobolivar.ecommerce.checkout.domain.order;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Order aggregate: an immutable historical record of a confirmed checkout.
 *
 * @param radicado      business reference ({@code ORD-<yyyyMMddHHmmssSSS>})
 * @param customerId    owning customer, or {@code null} for a guest checkout
 * @param status        current lifecycle status
 * @param createdAt     creation instant
 * @param couponCode    coupon actually applied, or {@code null}
 * @param lines         purchased lines (snapshots)
 * @param discounts     raw discount per rule (before the cap)
 * @param originalTotal subtotal before discounts
 * @param totalDiscount consolidated discount (already capped at 35% when applicable)
 * @param effectiveRate {@code totalDiscount / originalTotal}
 * @param finalTotal    amount paid
 * @param capReached    whether the 35% cap truncated the discount
 */
public record Order(
		String radicado,
		Long customerId,
		OrderStatus status,
		Instant createdAt,
		String couponCode,
		List<OrderLine> lines,
		List<OrderDiscount> discounts,
		Money originalTotal,
		Money totalDiscount,
		BigDecimal effectiveRate,
		Money finalTotal,
		boolean capReached) {

	public Order {
		lines = List.copyOf(lines);
		discounts = List.copyOf(discounts);
	}
}
