package com.grupobolivar.ecommerce.checkout.api.dto;

import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import java.math.BigDecimal;
import java.util.List;

/**
 * Response of {@code POST /api/checkout/quote}. Field names match the frontend
 * {@code DiscountBreakdown} interface. {@code lines} carries the raw discount per rule;
 * {@code totalDiscount}/{@code finalTotal} are already capped at 35% when applicable.
 */
public record QuoteResponse(
		BigDecimal originalTotal,
		List<Line> lines,
		BigDecimal totalDiscount,
		BigDecimal effectiveRate,
		BigDecimal finalTotal,
		boolean capReached) {

	public record Line(String type, BigDecimal amount) {
	}

	public static QuoteResponse from(DiscountBreakdown breakdown) {
		List<Line> lines = breakdown.lines().stream()
				.map(line -> new Line(line.type().name(), line.amount().value()))
				.toList();
		return new QuoteResponse(
				breakdown.originalTotal().value(),
				lines,
				breakdown.totalDiscount().value(),
				breakdown.effectiveRate(),
				breakdown.finalTotal().value(),
				breakdown.capReached());
	}
}
