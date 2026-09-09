package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderDiscount;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderLine;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** JPA implementation of {@link OrderRepository}: maps the {@link Order} aggregate to/from entities. */
@Repository
public class OrderRepositoryAdapter implements OrderRepository {

	private final OrderJpaRepository orders;

	public OrderRepositoryAdapter(OrderJpaRepository orders) {
		this.orders = orders;
	}

	@Override
	public Order save(Order order) {
		OrderEntity entity = new OrderEntity(
				order.radicado(), order.customerId(), order.status(), order.createdAt(),
				order.couponCode(), order.originalTotal().value(), order.totalDiscount().value(),
				order.effectiveRate(), order.finalTotal().value(), order.capReached());

		order.lines().forEach(line -> entity.addLine(new OrderLineEntity(
				line.productId(), line.productName(), line.unitPrice().value(),
				line.categoryName(), line.quantity(), line.lineTotal().value())));
		order.discounts().forEach(discount -> entity.addDiscount(new OrderDiscountEntity(
				discount.type(), discount.rate(), discount.amount().value())));
		entity.addStatusChange(new OrderStatusHistoryEntity(
				order.status(), order.createdAt(), "Orden creada"));

		return toDomain(orders.save(entity));
	}

	@Override
	public Optional<Order> findByRadicado(String radicado) {
		return orders.findByRadicado(radicado).map(this::toDomain);
	}

	private Order toDomain(OrderEntity entity) {
		List<OrderLine> lines = entity.getLines().stream()
				.map(line -> new OrderLine(line.getProductId(), line.getProductName(),
						Money.of(line.getUnitPrice()), line.getCategoryName(), line.getQuantity(),
						Money.of(line.getLineTotal())))
				.toList();
		List<OrderDiscount> discounts = entity.getDiscounts().stream()
				.map(discount -> new OrderDiscount(discount.getType(), discount.getRate(),
						Money.of(discount.getAmount())))
				.toList();

		return new Order(
				entity.getRadicado(), entity.getCustomerId(), entity.getStatus(),
				entity.getCreatedAt(), entity.getCouponCode(), lines, discounts,
				Money.of(entity.getOriginalTotal()), Money.of(entity.getTotalDiscount()),
				entity.getEffectiveRate(), Money.of(entity.getFinalTotal()), entity.isCapReached());
	}
}
