package com.grupobolivar.ecommerce.checkout.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.checkout.application.CheckoutService;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountBreakdown;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountLine;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import com.grupobolivar.ecommerce.shared.api.GlobalExceptionHandler;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CheckoutController.class)
@Import(GlobalExceptionHandler.class)
class CheckoutControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	CheckoutService checkoutService;

	@Test
	void returnsTheBreakdownAsJson() throws Exception {
		DiscountBreakdown breakdown = new DiscountBreakdown(
				Money.of("70.00"),
				List.of(new DiscountLine(DiscountType.CATEGORY, Money.of("5.00")),
						new DiscountLine(DiscountType.COUPON, Money.of("9.75"))),
				Money.of("14.75"), new BigDecimal("0.2107"), Money.of("55.25"), false);
		when(checkoutService.quote(any())).thenReturn(breakdown);

		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 2 } ], "couponCode": "WELCOME2026" }"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.originalTotal").value(70.00))
				.andExpect(jsonPath("$.lines[0].type").value("CATEGORY"))
				.andExpect(jsonPath("$.lines[1].amount").value(9.75))
				.andExpect(jsonPath("$.finalTotal").value(55.25))
				.andExpect(jsonPath("$.capReached").value(false));
	}

	@Test
	void rejectsAnEmptyItemsList() throws Exception {
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("{ \"items\": [], \"couponCode\": null }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void rejectsANonPositiveQuantity() throws Exception {
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("{ \"items\": [ { \"productId\": 2, \"quantity\": 0 } ] }"))
				.andExpect(status().isBadRequest());
	}
}
