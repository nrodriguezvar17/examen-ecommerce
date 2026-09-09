package com.grupobolivar.ecommerce.checkout.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grupobolivar.ecommerce.checkout.application.command.CheckoutCommand;
import com.grupobolivar.ecommerce.checkout.application.command.QuoteCommand;
import com.grupobolivar.ecommerce.checkout.application.exception.EmptyCartException;
import com.grupobolivar.ecommerce.checkout.application.exception.InsufficientStockException;
import com.grupobolivar.ecommerce.checkout.application.exception.UnknownProductException;
import com.grupobolivar.ecommerce.checkout.application.port.DiscountSettingsProvider;
import com.grupobolivar.ecommerce.checkout.application.port.ProductSnapshot;
import com.grupobolivar.ecommerce.checkout.application.port.ProductStockPort;
import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountConfig;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderRepository;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

	@Mock
	ProductStockPort products;
	@Mock
	DiscountSettingsProvider settings;
	@Mock
	CouponCatalog coupons;
	@Mock
	OrderRepository orders;

	private final Clock clock =
			Clock.fixed(Instant.parse("2026-09-08T19:30:25.017Z"), ZoneId.of("America/Bogota"));

	CheckoutService service;

	@BeforeEach
	void setUp() {
		service = new CheckoutService(products, settings, coupons, orders,
				new RadicadoGenerator("ORD", clock), clock);
		lenient().when(settings.current()).thenReturn(new DiscountConfig(
				"Tecnología", new BigDecimal("0.10"), new BigDecimal("100.00"),
				new BigDecimal("0.05"), new BigDecimal("0.35")));
		lenient().when(coupons.findActive(anyString())).thenReturn(Optional.empty());
		lenient().when(orders.save(org.mockito.ArgumentMatchers.any()))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(products.decrementStock(anyLong(), anyInt())).thenReturn(true);
	}

	private void stockAvailable() {
		lenient().when(products.findById(2L)).thenReturn(Optional.of(
				new ProductSnapshot(2, "Mouse", Money.of("25.00"), "Tecnología", 40)));
		lenient().when(products.findById(6L)).thenReturn(Optional.of(
				new ProductSnapshot(6, "Cuaderno", Money.of("5.00"), "Papelería", 100)));
	}

	@Test
	void quoteRejectsAnEmptyCart() {
		assertThatThrownBy(() -> service.quote(new QuoteCommand(List.of(), null)))
				.isInstanceOf(EmptyCartException.class);
	}

	@Test
	void quoteRejectsAnUnknownProduct() {
		when(products.findById(999L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.quote(
				new QuoteCommand(List.of(new QuoteCommand.Line(999L, 1)), null)))
				.isInstanceOf(UnknownProductException.class);
	}

	@Test
	void quoteRunsTheCascadeResolvingPricesServerSide() {
		stockAvailable();
		when(coupons.findActive("WELCOME2026"))
				.thenReturn(Optional.of(new Coupon("WELCOME2026", new BigDecimal("0.15"), true)));

		DiscountBreakdown result = service.quote(new QuoteCommand(
				List.of(new QuoteCommand.Line(2L, 2), new QuoteCommand.Line(6L, 4)), "WELCOME2026"));

		assertThat(result.originalTotal().value()).isEqualByComparingTo("70.00");
		assertThat(result.finalTotal().value()).isEqualByComparingTo("55.25");
	}

	@Test
	void checkoutPersistsTheOrderAndDecrementsStock() {
		stockAvailable();
		when(coupons.findActive("WELCOME2026"))
				.thenReturn(Optional.of(new Coupon("WELCOME2026", new BigDecimal("0.15"), true)));

		OrderConfirmation confirmation = service.checkout(new CheckoutCommand(
				List.of(new QuoteCommand.Line(2L, 2), new QuoteCommand.Line(6L, 4)), "WELCOME2026"));

		assertThat(confirmation.radicado()).isEqualTo("ORD-20260908143025017");
		assertThat(confirmation.status()).isEqualTo(OrderStatus.COMPRADO);
		assertThat(confirmation.breakdown().finalTotal().value()).isEqualByComparingTo("55.25");

		org.mockito.ArgumentCaptor<Order> orderCaptor =
				org.mockito.ArgumentCaptor.forClass(Order.class);
		verify(orders).save(orderCaptor.capture());
		Order saved = orderCaptor.getValue();
		assertThat(saved.lines()).hasSize(2);
		assertThat(saved.couponCode()).isEqualTo("WELCOME2026");
		assertThat(saved.discounts()).anySatisfy(discount -> {
			assertThat(discount.type()).isEqualTo(DiscountType.CATEGORY);
			assertThat(discount.rate()).isEqualByComparingTo("0.10");
		});

		verify(products).decrementStock(2L, 2);
		verify(products).decrementStock(6L, 4);
	}

	@Test
	void checkoutRejectsAndDoesNotPersistWhenStockIsInsufficient() {
		when(products.findById(2L)).thenReturn(Optional.of(
				new ProductSnapshot(2, "Auriculares", Money.of("60.00"), "Tecnología", 3)));

		assertThatThrownBy(() -> service.checkout(new CheckoutCommand(
				List.of(new QuoteCommand.Line(2L, 99)), null)))
				.isInstanceOf(InsufficientStockException.class)
				.hasMessageContaining("Auriculares");

		verify(orders, never()).save(org.mockito.ArgumentMatchers.any());
		verify(products, never()).decrementStock(anyLong(), anyInt());
	}

	@Test
	void checkoutRejectsWhenStockIsDrainedBetweenTheReadAndTheDecrement() {
		stockAvailable();
		when(products.decrementStock(2L, 2)).thenReturn(false);

		assertThatThrownBy(() -> service.checkout(new CheckoutCommand(
				List.of(new QuoteCommand.Line(2L, 2)), null)))
				.isInstanceOf(InsufficientStockException.class);

		verify(orders, never()).save(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void checkoutRejectsAnEmptyCart() {
		assertThatThrownBy(() -> service.checkout(new CheckoutCommand(List.of(), null)))
				.isInstanceOf(EmptyCartException.class);
		verify(orders, times(0)).save(org.mockito.ArgumentMatchers.any());
	}
}
