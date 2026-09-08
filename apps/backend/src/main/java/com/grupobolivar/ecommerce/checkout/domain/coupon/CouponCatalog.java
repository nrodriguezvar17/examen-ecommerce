package com.grupobolivar.ecommerce.checkout.domain.coupon;

import java.util.Optional;

/**
 * Port: source of coupons. The implementation (in-memory, database, external service...)
 * lives in {@code infrastructure} and is injected into the domain, so the discount engine
 * does not depend on how coupons are stored.
 */
public interface CouponCatalog {

	/** Returns the coupon if it exists and is active; {@link Optional#empty()} otherwise. */
	Optional<Coupon> findActive(String code);
}
