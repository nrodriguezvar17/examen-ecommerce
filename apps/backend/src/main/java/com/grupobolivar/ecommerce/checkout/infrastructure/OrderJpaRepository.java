package com.grupobolivar.ecommerce.checkout.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

	Optional<OrderEntity> findByRadicado(String radicado);
}
