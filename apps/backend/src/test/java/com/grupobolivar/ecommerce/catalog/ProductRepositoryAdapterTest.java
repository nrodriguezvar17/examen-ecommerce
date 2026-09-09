package com.grupobolivar.ecommerce.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.grupobolivar.ecommerce.TestcontainersConfiguration;
import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductDetail;
import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import com.grupobolivar.ecommerce.catalog.infrastructure.ProductJpaRepository;
import com.grupobolivar.ecommerce.catalog.infrastructure.ProductRepositoryAdapter;
import com.grupobolivar.ecommerce.catalog.infrastructure.ProductReviewJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

/**
 * Verifies the JPA -> domain mapping of {@link ProductRepositoryAdapter} against the real
 * PostgreSQL seed via Testcontainers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ProductRepositoryAdapterTest {

	@Autowired
	ProductJpaRepository jpaRepository;

	@Autowired
	ProductReviewJpaRepository reviewRepository;

	private ProductRepository repository() {
		return new ProductRepositoryAdapter(jpaRepository, reviewRepository);
	}

	@Test
	void findAllActiveReturnsTheTenSeededProductsOrderedBySku() {
		List<Product> products = repository().findAllActive();

		assertThat(products).hasSize(10);
		assertThat(products).extracting(Product::sku).isSorted();
	}

	@Test
	void mapsDetailCategoryAndRatingToTheDomainModel() {
		Product laptop = repository().findAllActive().stream()
				.filter(product -> product.sku().equals("TEC-LAP-014"))
				.findFirst()
				.orElseThrow();

		assertThat(laptop.displayName()).isEqualTo("Laptop Pro 14\" (Core i7, 16 GB)");
		assertThat(laptop.category()).isEqualTo("Tecnología");
		assertThat(laptop.ratingAverage()).isEqualByComparingTo("4.50");
		assertThat(laptop.reviewCount()).isEqualTo(2);
	}

	@Test
	void findByIdReturnsTheDomainProductOrEmpty() {
		assertThat(repository().findById(2L)).isPresent();
		assertThat(repository().findById(999_999L)).isEmpty();
	}

	@Test
	void findDetailByIdMapsBrandImageAndReviewsNewestFirst() {
		long laptopId = repository().findAllActive().stream()
				.filter(product -> product.sku().equals("TEC-LAP-014"))
				.findFirst().orElseThrow().id();

		ProductDetail detail = repository().findDetailById(laptopId).orElseThrow();

		assertThat(detail.product().id()).isEqualTo(laptopId);
		assertThat(detail.product().imageUrl()).startsWith("https://picsum.photos/");
		assertThat(detail.brand()).isEqualTo("NovaTech");
		assertThat(detail.reviews()).hasSize(2);
		assertThat(detail.reviews()).allSatisfy(review -> {
			assertThat(review.rating()).isBetween(1, 5);
			assertThat(review.reviewerName()).isNotBlank();
		});
		assertThat(detail.reviews()).isSortedAccordingTo(
				(a, b) -> b.createdAt().compareTo(a.createdAt()));
		assertThat(repository().findDetailById(999_999L)).isEmpty();
	}
}
