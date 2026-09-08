package com.grupobolivar.ecommerce.checkout.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, String> {
}
