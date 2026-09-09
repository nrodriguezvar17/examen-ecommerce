package com.grupobolivar.ecommerce.checkout;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.TestcontainersConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * HU3 (persistencia + stock) y HU4 (tope del 35 %) end-to-end contra el seed real
 * (Testcontainers). Cada test hace rollback para no ensuciar la base.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class CheckoutIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void confirmsAnOrderPersistsItAndDecrementsStock() throws Exception {
		String body = mockMvc.perform(post("/api/checkout")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 3 } ],
								  "couponCode": "WELCOME2026" }"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.radicado").value(Matchers.matchesRegex("^[A-Z]{2,5}-[0-9]{14,17}$")))
				.andExpect(jsonPath("$.status").value("COMPRADO"))
				.andReturn().getResponse().getContentAsString();

		String radicado = new ObjectMapper().readTree(body).get("radicado").asString();

		mockMvc.perform(get("/api/orders/{radicado}", radicado))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lines[0].productId").value(2))
				.andExpect(jsonPath("$.lines[0].quantity").value(3))
				.andExpect(jsonPath("$.discounts[?(@.type == 'CATEGORY')]").exists());

		// Mouse (id 2) starts with stock 40 in the seed.
		mockMvc.perform(get("/api/products"))
				.andExpect(jsonPath("$[?(@.id == 2)].stock").value(Matchers.contains(37)));
	}

	@Test
	void capsTheDiscountAtThirtyFivePercentAndPersistsTheFlag() throws Exception {
		// Mouse (id 2, Tecnología, 25.00) x5 = 125.00. Cascade 10% -> 5% -> 50% (MEGADESCUENTO)
		// exceeds 35%, so AbsoluteCapPolicy truncates: final = 125.00 * 0.65 = 81.25 (HU4).
		mockMvc.perform(post("/api/checkout/quote")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 5 } ],
								  "couponCode": "MEGADESCUENTO" }"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.capReached").value(true))
				.andExpect(jsonPath("$.effectiveRate").value(0.35))
				.andExpect(jsonPath("$.totalDiscount").value(43.75))
				.andExpect(jsonPath("$.finalTotal").value(81.25));

		String confirmation = mockMvc.perform(post("/api/checkout")
						.contentType("application/json")
						.content("""
								{ "items": [ { "productId": 2, "quantity": 5 } ],
								  "couponCode": "MEGADESCUENTO" }"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.breakdown.capReached").value(true))
				.andReturn().getResponse().getContentAsString();

		String radicado = new ObjectMapper().readTree(confirmation).get("radicado").asString();
		mockMvc.perform(get("/api/orders/{radicado}", radicado))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.capReached").value(true))
				.andExpect(jsonPath("$.effectiveRate").value(0.35))
				.andExpect(jsonPath("$.finalTotal").value(81.25));
	}

	@Test
	void rejectsWithConflictWhenStockIsInsufficient() throws Exception {
		// Auriculares Bluetooth (id 4) has stock 3.
		mockMvc.perform(post("/api/checkout")
						.contentType("application/json")
						.content("{ \"items\": [ { \"productId\": 4, \"quantity\": 99 } ] }"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409));

		mockMvc.perform(get("/api/products"))
				.andExpect(jsonPath("$[?(@.id == 4)].stock").value(Matchers.contains(3)));
	}
}
