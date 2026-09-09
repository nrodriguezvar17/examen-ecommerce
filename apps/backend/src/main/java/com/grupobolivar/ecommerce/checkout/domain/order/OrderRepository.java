package com.grupobolivar.ecommerce.checkout.domain.order;

import java.util.Optional;

/** Repository for the {@link Order} aggregate. Implemented by the checkout infrastructure. */
public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findByRadicado(String radicado);
}
