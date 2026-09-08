package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** {@link CouponCatalog} backed by the {@code coupons} table. */
@Component
public class JpaCouponCatalog implements CouponCatalog {

	private final CouponJpaRepository coupons;

	public JpaCouponCatalog(CouponJpaRepository coupons) {
		this.coupons = coupons;
	}

	@Override
	public Optional<Coupon> findActive(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}
		return coupons.findById(code.trim().toUpperCase())
				.filter(this::isCurrentlyValid)
				.map(entity -> new Coupon(entity.getCode(), entity.getDiscountRate(), true));
	}

	private boolean isCurrentlyValid(CouponEntity coupon) {
		if (!coupon.isActive()) {
			return false;
		}
		Instant now = Instant.now();
		if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
			return false;
		}
		return coupon.getValidUntil() == null || !now.isAfter(coupon.getValidUntil());
	}
}
