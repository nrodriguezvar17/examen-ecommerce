package com.grupobolivar.ecommerce.catalog;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupobolivar.ecommerce.catalog.api.CatalogController;
import com.grupobolivar.ecommerce.catalog.application.CatalogService;
import com.grupobolivar.ecommerce.catalog.application.ProductNotFoundException;
import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductDetail;
import com.grupobolivar.ecommerce.catalog.domain.Review;
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

@WebMvcTest(CatalogController.class)
@Import(GlobalExceptionHandler.class)
class CatalogControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	CatalogService catalogService;

	private static Product mouse() {
		return new Product(
				2, "TEC-MOU-001", "Mouse inalámbrico", "Mouse inalámbrico silencioso",
				"Mouse óptico inalámbrico.", "https://picsum.photos/seed/mouse/600/400",
				new BigDecimal("25.00"), "Tecnología", 40, new BigDecimal("5.00"), 1);
	}

	@Test
	void returnsTheCatalogAsJson() throws Exception {
		when(catalogService.listAvailableProducts()).thenReturn(List.of(mouse()));

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].sku").value("TEC-MOU-001"))
				.andExpect(jsonPath("$[0].displayName").value("Mouse inalámbrico silencioso"))
				.andExpect(jsonPath("$[0].imageUrl").value("https://picsum.photos/seed/mouse/600/400"))
				.andExpect(jsonPath("$[0].unitPrice").value(25.00))
				.andExpect(jsonPath("$[0].category").value("Tecnología"))
				.andExpect(jsonPath("$[0].stock").value(40));
	}

	@Test
	void returnsTheProductDetailWithReviews() throws Exception {
		ProductDetail detail = new ProductDetail(
				mouse(), "LogiCorp",
				List.of(new Review(9, 5, "Silencioso", "El clic casi no se oye.", "Sofía R.",
						Instant.parse("2026-01-10T12:00:00Z"))));
		when(catalogService.getProductDetail(2L)).thenReturn(detail);

		mockMvc.perform(get("/api/products/2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.brand").value("LogiCorp"))
				.andExpect(jsonPath("$.imageUrl").value("https://picsum.photos/seed/mouse/600/400"))
				.andExpect(jsonPath("$.reviews[0].reviewerName").value("Sofía R."))
				.andExpect(jsonPath("$.reviews[0].rating").value(5));
	}

	@Test
	void returns404WhenTheProductDoesNotExist() throws Exception {
		when(catalogService.getProductDetail(eq(999L))).thenThrow(new ProductNotFoundException(999));

		mockMvc.perform(get("/api/products/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
