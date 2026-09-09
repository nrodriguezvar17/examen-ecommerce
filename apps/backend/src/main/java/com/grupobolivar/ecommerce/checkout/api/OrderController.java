package com.grupobolivar.ecommerce.checkout.api;

import com.grupobolivar.ecommerce.checkout.api.dto.OrderResponse;
import com.grupobolivar.ecommerce.checkout.application.OrderQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Order read API: the recent-orders list ("Mis compras") and the per-order detail. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderQueryService orders;

	public OrderController(OrderQueryService orders) {
		this.orders = orders;
	}

	@GetMapping
	public List<OrderResponse> list() {
		return orders.findRecent().stream().map(OrderResponse::from).toList();
	}

	@GetMapping("/{radicado}")
	public OrderResponse get(@PathVariable String radicado) {
		return OrderResponse.from(orders.findByRadicado(radicado));
	}
}
