package com.grupobolivar.ecommerce.catalog.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewJpaRepository extends JpaRepository<ProductReviewEntity, Long> {

	List<ProductReviewEntity> findByProductIdOrderByCreatedAtDescIdDesc(long productId);
}
