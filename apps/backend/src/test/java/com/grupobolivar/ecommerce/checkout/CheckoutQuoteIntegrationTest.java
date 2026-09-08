package com.grupobolivar.ecommerce.checkout;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** End-to-end quote against the real seed (Testcontainers PostgreSQL). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CheckoutQuoteIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void quotesACartWithCategoryAndCouponDiscounts() throws Exception {
		// productId 2 = Mouse (25.00, Tecnología) x2 ; productId 6 = Cuaderno (5.00, Papelería) x4
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 2 },
								             { "productId": 6, "quantity": 4 } ],
								  "couponCode": "welcome2026" }"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.originalTotal").value(70.00))
				.andExpect(jsonPath("$.totalDiscount").value(14.75))
				.andExpect(jsonPath("$.finalTotal").value(55.25))
				.andExpect(jsonPath("$.capReached").value(false));
	}

	@Test
	void ignoresAnExpiredCoupon() throws Exception {
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 2 } ],
								  "couponCode": "BLACKFRIDAY2025" }"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lines[?(@.type == 'COUPON')]").isEmpty());
	}

	@Test
	void returns404ForAnUnknownProduct() throws Exception {
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("{ \"items\": [ { \"productId\": 999999, \"quantity\": 1 } ] }"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
