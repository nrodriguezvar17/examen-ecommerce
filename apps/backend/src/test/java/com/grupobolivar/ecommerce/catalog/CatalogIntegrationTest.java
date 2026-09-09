package com.grupobolivar.ecommerce.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** End-to-end catalog: {@code GET /api/products} against the real seed (Testcontainers). */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CatalogIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void servesTheSeededCatalog() throws Exception {
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(10)))
				.andExpect(jsonPath("$[0].sku").value("DEP-BAL-005"))
				.andExpect(jsonPath("$[?(@.sku == 'TEC-LAP-014')].ratingAverage").value(Matchers.contains(4.5)));
	}

	@Test
	void servesTheProductDetailWithBrandAndReviews() throws Exception {
		String catalog = mockMvc.perform(get("/api/products"))
				.andReturn().getResponse().getContentAsString();
		java.util.List<Integer> ids = JsonPath.read(catalog, "$[?(@.sku == 'TEC-LAP-014')].id");
		int laptopId = ids.get(0);

		mockMvc.perform(get("/api/products/{id}", laptopId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sku").value("TEC-LAP-014"))
				.andExpect(jsonPath("$.brand").value("NovaTech"))
				.andExpect(jsonPath("$.reviews", Matchers.hasSize(2)));

		mockMvc.perform(get("/api/products/999999"))
				.andExpect(status().isNotFound());
	}
}
