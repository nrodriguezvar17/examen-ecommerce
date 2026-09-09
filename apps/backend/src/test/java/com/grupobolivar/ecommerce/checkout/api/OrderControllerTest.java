package com.grupobolivar.ecommerce.checkout.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.checkout.application.OrderNotFoundException;
import com.grupobolivar.ecommerce.checkout.application.OrderQueryService;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import com.grupobolivar.ecommerce.checkout.domain.order.Order;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderDiscount;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderLine;
import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import com.grupobolivar.ecommerce.shared.api.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	OrderQueryService orderQueryService;

	private static Order sampleOrder() {
		return new Order(
				"ORD-20260908143025017", null, OrderStatus.COMPRADO, Instant.parse("2026-09-08T19:30:25Z"),
				"WELCOME2026",
				List.of(new OrderLine(2, "Mouse", Money.of("25.00"), "Tecnología", 2, Money.of("50.00"))),
				List.of(new OrderDiscount(DiscountType.CATEGORY, new BigDecimal("0.10"), Money.of("5.00"))),
				Money.of("50.00"), Money.of("5.00"), new BigDecimal("0.1000"), Money.of("45.00"), false);
	}

	@Test
	void listReturnsTheRecentOrders() throws Exception {
		when(orderQueryService.findRecent()).thenReturn(List.of(sampleOrder()));

		mockMvc.perform(get("/api/orders"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].radicado").value("ORD-20260908143025017"))
				.andExpect(jsonPath("$[0].lines[0].productName").value("Mouse"))
				.andExpect(jsonPath("$[0].finalTotal").value(45.00));
	}

	@Test
	void returnsTheOrderDetailAsJson() throws Exception {
		Order order = sampleOrder();
		when(orderQueryService.findByRadicado("ORD-20260908143025017")).thenReturn(order);

		mockMvc.perform(get("/api/orders/ORD-20260908143025017"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("COMPRADO"))
				.andExpect(jsonPath("$.couponCode").value("WELCOME2026"))
				.andExpect(jsonPath("$.lines[0].productName").value("Mouse"))
				.andExpect(jsonPath("$.discounts[0].type").value("CATEGORY"))
				.andExpect(jsonPath("$.finalTotal").value(45.00));
	}

	@Test
	void returns404WhenTheOrderDoesNotExist() throws Exception {
		when(orderQueryService.findByRadicado(eq("ORD-00000000000000000")))
				.thenThrow(new OrderNotFoundException("ORD-00000000000000000"));

		mockMvc.perform(get("/api/orders/ORD-00000000000000000"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
