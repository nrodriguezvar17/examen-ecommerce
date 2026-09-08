package com.grupobolivar.ecommerce.catalog.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for the product catalog. */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

	List<ProductEntity> findByActiveTrueOrderBySkuAsc();
}
