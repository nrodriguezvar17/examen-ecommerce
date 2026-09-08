package com.grupobolivar.ecommerce.checkout.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountConfig;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.math.BigDecimal;
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

	CheckoutService service;

	@BeforeEach
	void setUp() {
		service = new CheckoutService(products, settings, coupons);
		lenient().when(settings.current()).thenReturn(new DiscountConfig(
				"Tecnología", new BigDecimal("0.10"), new BigDecimal("100.00"),
				new BigDecimal("0.05"), new BigDecimal("0.35")));
		lenient().when(coupons.findActive(anyString())).thenReturn(Optional.empty());
	}

	private void stockAvailable() {
		lenient().when(products.findById(2L)).thenReturn(Optional.of(
				new ProductSnapshot(2, "Mouse", Money.of("25.00"), "Tecnología", 40)));
		lenient().when(products.findById(6L)).thenReturn(Optional.of(
				new ProductSnapshot(6, "Cuaderno", Money.of("5.00"), "Papelería", 100)));
	}

	@Test
	void rejectsAnEmptyCart() {
		assertThatThrownBy(() -> service.quote(new QuoteCommand(List.of(), null)))
				.isInstanceOf(EmptyCartException.class);
		assertThatThrownBy(() -> service.quote(new QuoteCommand(null, "WELCOME2026")))
				.isInstanceOf(EmptyCartException.class);
	}

	@Test
	void rejectsAnUnknownProduct() {
		when(products.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.quote(
				new QuoteCommand(List.of(new QuoteCommand.Line(999L, 1)), null)))
				.isInstanceOf(UnknownProductException.class)
				.hasMessageContaining("999");
	}

	@Test
	void quotesTheCascadeResolvingPricesServerSide() {
		stockAvailable();
		when(coupons.findActive("WELCOME2026"))
				.thenReturn(Optional.of(new Coupon("WELCOME2026", new BigDecimal("0.15"), true)));

		DiscountBreakdown result = service.quote(new QuoteCommand(
				List.of(new QuoteCommand.Line(2L, 2), new QuoteCommand.Line(6L, 4)), "WELCOME2026"));

		// T0 = 50 + 20 = 70 ; category 10% of 50 = 5 ; volume: 65 !> 100 -> 0 ; coupon 15% of 65 = 9.75
		assertThat(result.originalTotal().value()).isEqualByComparingTo("70.00");
		assertThat(amountOf(result, DiscountType.CATEGORY)).isEqualByComparingTo("5.00");
		assertThat(amountOf(result, DiscountType.VOLUME)).isEqualByComparingTo("0");
		assertThat(amountOf(result, DiscountType.COUPON)).isEqualByComparingTo("9.75");
		assertThat(result.finalTotal().value()).isEqualByComparingTo("55.25");
		assertThat(result.capReached()).isFalse();
	}

	@Test
	void ignoresAnInactiveCoupon() {
		stockAvailable();

		DiscountBreakdown result = service.quote(new QuoteCommand(
				List.of(new QuoteCommand.Line(2L, 2)), "BLACKFRIDAY2025"));

		assertThat(amountOf(result, DiscountType.COUPON)).isEqualByComparingTo("0");
	}

	private static BigDecimal amountOf(DiscountBreakdown breakdown, DiscountType type) {
		return breakdown.lines().stream()
				.filter(line -> line.type() == type)
				.map(line -> line.amount().value())
				.findFirst()
				.orElse(BigDecimal.ZERO);
	}
}
