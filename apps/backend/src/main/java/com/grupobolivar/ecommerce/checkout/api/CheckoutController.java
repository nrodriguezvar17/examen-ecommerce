package com.grupobolivar.ecommerce.checkout.api;

import com.grupobolivar.ecommerce.checkout.api.dto.CartItemRequest;
import com.grupobolivar.ecommerce.checkout.api.dto.CheckoutRequest;
import com.grupobolivar.ecommerce.checkout.api.dto.OrderConfirmationResponse;
import com.grupobolivar.ecommerce.checkout.api.dto.QuoteRequest;
import com.grupobolivar.ecommerce.checkout.api.dto.QuoteResponse;
import com.grupobolivar.ecommerce.checkout.application.CheckoutService;
import com.grupobolivar.ecommerce.checkout.application.command.CheckoutCommand;
import com.grupobolivar.ecommerce.checkout.application.command.QuoteCommand;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** HU2 — discount quote (no side effects); HU3 — confirm the order. */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

	private final CheckoutService checkout;

	public CheckoutController(CheckoutService checkout) {
		this.checkout = checkout;
	}

	@PostMapping("/quote")
	public QuoteResponse quote(@Valid @RequestBody QuoteRequest request) {
		return QuoteResponse.from(checkout.quote(new QuoteCommand(toLines(request.items()),
				request.couponCode())));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderConfirmationResponse confirm(@Valid @RequestBody CheckoutRequest request) {
		return OrderConfirmationResponse.from(checkout.checkout(
				new CheckoutCommand(toLines(request.items()), request.couponCode())));
	}

	private static List<QuoteCommand.Line> toLines(List<CartItemRequest> items) {
		return items.stream()
				.map(item -> new QuoteCommand.Line(item.productId(), item.quantity()))
				.toList();
	}
}
