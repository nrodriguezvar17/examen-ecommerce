package com.grupobolivar.ecommerce.catalog.infrastructure;

import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of {@link ProductRepository}. The only class that touches
 * {@link ProductEntity}; it maps the entity plus its 1:1 detail and rating rows to the
 * domain {@link Product}.
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

	private final ProductJpaRepository products;

	public ProductRepositoryAdapter(ProductJpaRepository products) {
		this.products = products;
	}

	@Override
	public List<Product> findAllActive() {
		return products.findByActiveTrueOrderBySkuAsc().stream().map(this::toDomain).toList();
	}

	@Override
	public Optional<Product> findById(long id) {
		return products.findById(id).map(this::toDomain);
	}

	private Product toDomain(ProductEntity entity) {
		return new Product(
				entity.getId(),
				entity.getSku(),
				entity.getName(),
				entity.getDetail().getDisplayName(),
				entity.getDetail().getDescription(),
				entity.getUnitPrice(),
				entity.getCategory().getName(),
				entity.getStock(),
				entity.getRating().getRatingAverage(),
				entity.getRating().getReviewCount());
	}
}
