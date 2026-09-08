package com.grupobolivar.ecommerce.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.TestcontainersConfiguration;
import com.grupobolivar.ecommerce.catalog.api.ProductResponse;
import com.grupobolivar.ecommerce.catalog.application.CatalogService;
import com.grupobolivar.ecommerce.catalog.infrastructure.ProductJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

/**
 * Exercises the catalog read path (repository -> service -> ProductResponse mapping)
 * against the real PostgreSQL seed via Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestcontainersConfiguration.class)
class CatalogIntegrationTest {

	@Autowired
	ProductJpaRepository repository;

	@Test
	void listsTheTenSeededProductsOrderedBySku() {
		List<ProductResponse> products = new CatalogService(repository).listAvailableProducts();

		assertThat(products).hasSize(10);
		assertThat(products).extracting(ProductResponse::sku).isSorted();
	}

	@Test
	void mapsDetailCategoryAndRatingForEachProduct() {
		List<ProductResponse> products = new CatalogService(repository).listAvailableProducts();

		ProductResponse laptop = products.stream()
				.filter(p -> p.sku().equals("TEC-LAP-014"))
				.findFirst()
				.orElseThrow();

		assertThat(laptop.displayName()).isEqualTo("Laptop Pro 14\" (Core i7, 16 GB)");
		assertThat(laptop.category()).isEqualTo("Tecnología");
		assertThat(laptop.ratingAverage()).isEqualByComparingTo("4.50");
		assertThat(laptop.reviewCount()).isEqualTo(2);

		ProductResponse keyboard = products.stream()
				.filter(p -> p.sku().equals("TEC-KEY-002"))
				.findFirst()
				.orElseThrow();
		assertThat(keyboard.reviewCount()).isZero();
		assertThat(keyboard.ratingAverage()).isEqualByComparingTo("0");
	}
}
