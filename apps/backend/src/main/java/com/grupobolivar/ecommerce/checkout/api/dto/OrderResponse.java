package com.grupobolivar.ecommerce.checkout.api.dto;

import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderDiscount;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderLine;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Full order detail returned by {@code GET /api/orders/{radicado}}. */
public record OrderResponse(
		String radicado,
		Instant createdAt,
		String status,
		String couponCode,
		List<Line> lines,
		List<Discount> discounts,
		BigDecimal originalTotal,
		BigDecimal totalDiscount,
		BigDecimal effectiveRate,
		BigDecimal finalTotal,
		boolean capReached) {

	public record Line(long productId, String productName, BigDecimal unitPrice,
			String category, int quantity, BigDecimal lineTotal) {
	}

	public record Discount(String type, BigDecimal rate, BigDecimal amount) {
	}

	public static OrderResponse from(Order order) {
		return new OrderResponse(
				order.radicado(), order.createdAt(), order.status().name(), order.couponCode(),
				order.lines().stream().map(OrderResponse::line).toList(),
				order.discounts().stream().map(OrderResponse::discount).toList(),
				order.originalTotal().value(), order.totalDiscount().value(),
				order.effectiveRate(), order.finalTotal().value(), order.capReached());
	}

	private static Line line(OrderLine line) {
		return new Line(line.productId(), line.productName(), line.unitPrice().value(),
				line.categoryName(), line.quantity(), line.lineTotal().value());
	}

	private static Discount discount(OrderDiscount discount) {
		return new Discount(discount.type().name(), discount.rate(), discount.amount().value());
	}
}
