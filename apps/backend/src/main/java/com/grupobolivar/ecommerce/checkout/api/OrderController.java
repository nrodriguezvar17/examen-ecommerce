package com.grupobolivar.ecommerce.checkout.api;

import com.grupobolivar.ecommerce.checkout.application.OrderQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Order lookup — used in the demo to show the persisted order. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderQueryService orders;

	public OrderController(OrderQueryService orders) {
		this.orders = orders;
	}

	@GetMapping("/{radicado}")
	public OrderResponse get(@PathVariable String radicado) {
		return OrderResponse.from(orders.findByRadicado(radicado));
	}
}
