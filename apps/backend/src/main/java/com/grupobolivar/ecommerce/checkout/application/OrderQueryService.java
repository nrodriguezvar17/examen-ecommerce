package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of orders: {@code GET /api/orders/{radicado}}. */
@Service
public class OrderQueryService {

	private final OrderRepository orders;

	public OrderQueryService(OrderRepository orders) {
		this.orders = orders;
	}

	@Transactional(readOnly = true)
	public Order findByRadicado(String radicado) {
		return orders.findByRadicado(radicado)
				.orElseThrow(() -> new OrderNotFoundException(radicado));
	}
}
