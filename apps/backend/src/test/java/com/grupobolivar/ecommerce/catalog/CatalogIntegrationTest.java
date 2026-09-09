package com.grupobolivar.ecommerce.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
