package com.grupobolivar.ecommerce.checkout.domain.discount;

import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Cart;
import com.grupobolivar.ecommerce.checkout.domain.model.CartItem;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Small builders shared by the discount-engine unit tests. */
final class DiscountFixtures {

	static final String TECH = "Tecnología";

	private static final AtomicLong IDS = new AtomicLong(1);

	private DiscountFixtures() {
	}

	static CartItem item(String category, String unitPrice, int quantity) {
		return new CartItem(IDS.getAndIncrement(), "product", Money.of(unitPrice), quantity, category);
	}

	static Cart cart(CartItem... items) {
		return new Cart(List.of(items));
	}

	static DiscountContext context(String couponCode, CartItem... items) {
		return new DiscountContext(cart(items), couponCode);
	}

	static BigDecimal rate(String value) {
		return new BigDecimal(value);
	}

	/** In-memory catalog: only the given codes exist and are active. */
	static CouponCatalog couponsActive(Map<String, String> codeToRate) {
		return code -> Optional.ofNullable(codeToRate.get(code))
				.map(r -> new Coupon(code, new BigDecimal(r), true));
	}

	static CouponCatalog noCoupons() {
		return code -> Optional.empty();
	}
}
