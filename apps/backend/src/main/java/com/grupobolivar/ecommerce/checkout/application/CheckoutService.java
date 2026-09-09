package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountConfig;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountContext;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountLine;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountRuleFactory;
import com.grupobolivar.ecommerce.checkout.domain.model.Cart;
import com.grupobolivar.ecommerce.checkout.domain.model.CartItem;
import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderDiscount;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderLine;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderRepository;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Checkout orchestration. {@link #quote} calculates the discount breakdown with no side
 * effects (HU2). {@link #checkout} validates stock, recalculates, decrements stock and
 * persists the order in a single transaction (HU3).
 */
@Service
public class CheckoutService {

	private final ProductStockPort products;
	private final DiscountSettingsProvider settings;
	private final CouponCatalog coupons;
	private final OrderRepository orders;
	private final RadicadoGenerator radicadoGenerator;
	private final Clock clock;

	public CheckoutService(ProductStockPort products, DiscountSettingsProvider settings,
			CouponCatalog coupons, OrderRepository orders, RadicadoGenerator radicadoGenerator,
			Clock clock) {
		this.products = products;
		this.settings = settings;
		this.coupons = coupons;
		this.orders = orders;
		this.radicadoGenerator = radicadoGenerator;
		this.clock = clock;
	}

	public DiscountBreakdown quote(QuoteCommand command) {
		List<ResolvedLine> lines = resolve(command.items());
		return calculate(lines, command.couponCode());
	}

	@Transactional
	public OrderConfirmation checkout(CheckoutCommand command) {
		List<ResolvedLine> lines = resolve(command.items());
		lines.forEach(ResolvedLine::assertInStock);

		DiscountConfig config = settings.current();
		Optional<Coupon> coupon = activeCoupon(command.couponCode());
		DiscountBreakdown breakdown = calculate(lines, command.couponCode());

		Order order = new Order(
				radicadoGenerator.next(), null, OrderStatus.COMPRADO, clock.instant(),
				coupon.map(Coupon::code).orElse(null),
				lines.stream().map(ResolvedLine::toOrderLine).toList(),
				toOrderDiscounts(breakdown, config, coupon),
				breakdown.originalTotal(), breakdown.totalDiscount(),
				breakdown.effectiveRate(), breakdown.finalTotal(), breakdown.capReached());

		Order saved = orders.save(order);
		lines.forEach(line -> products.decrementStock(line.product().productId(), line.quantity()));

		return new OrderConfirmation(saved.radicado(), saved.createdAt(), saved.status(), breakdown);
	}

	private List<ResolvedLine> resolve(List<QuoteCommand.Line> items) {
		if (items == null || items.isEmpty()) {
			throw new EmptyCartException();
		}
		return items.stream().map(line -> new ResolvedLine(
				products.findById(line.productId())
						.orElseThrow(() -> new UnknownProductException(line.productId())),
				line.quantity())).toList();
	}

	private DiscountBreakdown calculate(List<ResolvedLine> lines, String couponCode) {
		Cart cart = new Cart(lines.stream().map(ResolvedLine::toCartItem).toList());
		return DiscountRuleFactory.createPipeline(settings.current(), coupons)
				.calculate(new DiscountContext(cart, couponCode));
	}

	private Optional<Coupon> activeCoupon(String couponCode) {
		return couponCode == null || couponCode.isBlank()
				? Optional.empty()
				: coupons.findActive(couponCode);
	}

	private List<OrderDiscount> toOrderDiscounts(DiscountBreakdown breakdown, DiscountConfig config,
			Optional<Coupon> coupon) {
		return breakdown.lines().stream()
				.map(line -> new OrderDiscount(line.type(), rateOf(line, config, coupon), line.amount()))
				.toList();
	}

	private BigDecimal rateOf(DiscountLine line, DiscountConfig config, Optional<Coupon> coupon) {
		return switch (line.type()) {
			case CATEGORY -> config.categoryPercent();
			case VOLUME -> config.volumePercent();
			case COUPON -> coupon.map(Coupon::percent).orElse(BigDecimal.ZERO);
		};
	}

	/** A cart line already resolved against the catalog. */
	private record ResolvedLine(ProductSnapshot product, int quantity) {

		void assertInStock() {
			if (quantity > product.stock()) {
				throw new InsufficientStockException(product.productId(), product.name(),
						quantity, product.stock());
			}
		}

		CartItem toCartItem() {
			return new CartItem(product.productId(), product.name(), product.unitPrice(),
					quantity, product.category());
		}

		OrderLine toOrderLine() {
			return new OrderLine(product.productId(), product.name(), product.unitPrice(),
					product.category(), quantity, product.unitPrice().times(quantity));
		}
	}
}
