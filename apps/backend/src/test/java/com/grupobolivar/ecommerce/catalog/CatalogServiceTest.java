package com.grupobolivar.ecommerce.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grupobolivar.ecommerce.catalog.application.CatalogService;
import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

	@Mock
	ProductRepository repository;

	@Test
	void returnsTheActiveProductsFromTheRepository() {
		Product product = new Product(1, "TEC-X", "n", "Display", "desc",
				new BigDecimal("10.00"), "Tecnología", 5, new BigDecimal("4.00"), 2);
		when(repository.findAllActive()).thenReturn(List.of(product));

		assertThat(new CatalogService(repository).listAvailableProducts()).containsExactly(product);
	}
}
