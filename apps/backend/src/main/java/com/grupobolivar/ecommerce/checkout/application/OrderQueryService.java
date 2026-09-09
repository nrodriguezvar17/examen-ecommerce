package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of orders: {@code GET /api/orders} and {@code GET /api/orders/{radicado}}. */
@Service
public class OrderQueryService {

	/** Cap for the "my orders" list — enough for the demo, keeps the payload bounded. */
	private static final int RECENT_LIMIT = 20;

	private final OrderRepository orders;

	public OrderQueryService(OrderRepository orders) {
		this.orders = orders;
	}

	@Transactional(readOnly = true)
	public Order findByRadicado(String radicado) {
		return orders.findByRadicado(radicado)
				.orElseThrow(() -> new OrderNotFoundException(radicado));
	}

	@Transactional(readOnly = true)
	public List<Order> findRecent() {
		return orders.findRecent(RECENT_LIMIT);
	}
}
