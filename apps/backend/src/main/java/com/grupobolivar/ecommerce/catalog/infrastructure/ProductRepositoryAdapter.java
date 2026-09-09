package com.grupobolivar.ecommerce.catalog.infrastructure;

import com.grupobolivar.ecommerce.catalog.domain.Product;
import com.grupobolivar.ecommerce.catalog.domain.ProductDetail;
import com.grupobolivar.ecommerce.catalog.domain.ProductRepository;
import com.grupobolivar.ecommerce.catalog.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of {@link ProductRepository}. The only class that touches
 * {@link ProductEntity}; it maps the entity plus its 1:1 detail, rating and review rows to
 * the domain {@link Product} / {@link ProductDetail}.
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

	private final ProductJpaRepository products;
	private final ProductReviewJpaRepository reviews;

	public ProductRepositoryAdapter(ProductJpaRepository products, ProductReviewJpaRepository reviews) {
		this.products = products;
		this.reviews = reviews;
	}

	@Override
	public List<Product> findAllActive() {
		return products.findByActiveTrueOrderBySkuAsc().stream().map(this::toDomain).toList();
	}

	@Override
	public Optional<Product> findById(long id) {
		return products.findById(id).map(this::toDomain);
	}

	@Override
	public Optional<ProductDetail> findDetailById(long id) {
		return products.findById(id).map(this::toDetail);
	}

	@Override
	public boolean decrementStock(long id, int quantity) {
		return products.decrementStock(id, quantity) == 1;
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

	private ProductDetail toDetail(ProductEntity entity) {
		List<Review> productReviews = reviews
				.findByProductIdOrderByCreatedAtDescIdDesc(entity.getId()).stream()
				.map(review -> new Review(review.getId(), review.getRating(), review.getTitle(),
						review.getComment(), review.getReviewerName(), review.getCreatedAt()))
				.toList();
		return new ProductDetail(
				toDomain(entity),
				entity.getDetail().getBrand(),
				entity.getDetail().getImageUrl(),
				productReviews);
	}
}
