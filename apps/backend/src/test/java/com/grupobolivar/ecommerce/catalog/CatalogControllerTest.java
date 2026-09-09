package com.grupobolivar.ecommerce.catalog;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.catalog.api.CatalogController;
import com.grupobolivar.ecommerce.catalog.application.CatalogService;
import com.grupobolivar.ecommerce.catalog.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	CatalogService catalogService;

	@Test
	void returnsTheCatalogAsJson() throws Exception {
		Product mouse = new Product(
				2, "TEC-MOU-001", "Mouse inalámbrico", "Mouse inalámbrico silencioso",
				"Mouse óptico inalámbrico.", new BigDecimal("25.00"), "Tecnología", 40,
				new BigDecimal("5.00"), 1);
		when(catalogService.listAvailableProducts()).thenReturn(List.of(mouse));

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].sku").value("TEC-MOU-001"))
				.andExpect(jsonPath("$[0].displayName").value("Mouse inalámbrico silencioso"))
				.andExpect(jsonPath("$[0].unitPrice").value(25.00))
				.andExpect(jsonPath("$[0].category").value("Tecnología"))
				.andExpect(jsonPath("$[0].stock").value(40));
	}
}
