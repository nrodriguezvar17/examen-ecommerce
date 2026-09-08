package com.grupobolivar.ecommerce.checkout.api;

import com.grupobolivar.ecommerce.checkout.application.CheckoutService;
import com.grupobolivar.ecommerce.checkout.application.QuoteCommand;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HU2 — dynamic discount quote (no side effects). */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

	private final CheckoutService checkout;

	public CheckoutController(CheckoutService checkout) {
		this.checkout = checkout;
	}

	@PostMapping("/quote")
	public QuoteResponse quote(@Valid @RequestBody QuoteRequest request) {
		QuoteCommand command = new QuoteCommand(
				request.items().stream()
						.map(item -> new QuoteCommand.Line(item.productId(), item.quantity()))
						.toList(),
				request.couponCode());
		return QuoteResponse.from(checkout.quote(command));
	}
}
