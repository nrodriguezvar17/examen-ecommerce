package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountContext;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountRuleFactory;
import com.grupobolivar.ecommerce.checkout.domain.model.Cart;
import com.grupobolivar.ecommerce.checkout.domain.model.CartItem;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the discount quote (HU2): resolves the cart server-side, runs the discount
 * engine and returns the breakdown. Has no side effects (no stock changes, no persistence).
 */
@Service
public class CheckoutService {

	private final ProductStockPort products;
	private final DiscountSettingsProvider settings;
	private final CouponCatalog coupons;

	public CheckoutService(ProductStockPort products, DiscountSettingsProvider settings,
			CouponCatalog coupons) {
		this.products = products;
		this.settings = settings;
		this.coupons = coupons;
	}

	public DiscountBreakdown quote(QuoteCommand command) {
		if (command.items() == null || command.items().isEmpty()) {
			throw new EmptyCartException();
		}
		Cart cart = new Cart(resolveLines(command.items()));
		return DiscountRuleFactory.createPipeline(settings.current(), coupons)
				.calculate(new DiscountContext(cart, command.couponCode()));
	}

	private List<CartItem> resolveLines(List<QuoteCommand.Line> lines) {
		return lines.stream().map(line -> {
			ProductSnapshot product = products.findById(line.productId())
					.orElseThrow(() -> new UnknownProductException(line.productId()));
			return new CartItem(product.productId(), product.name(), product.unitPrice(),
					line.quantity(), product.category());
		}).toList();
	}
}
