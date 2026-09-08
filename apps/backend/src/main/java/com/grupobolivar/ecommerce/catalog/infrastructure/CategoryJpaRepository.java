package com.grupobolivar.ecommerce.catalog.infrastructure;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

	/** The category eligible for the category discount (Rule 1), i.e. the one with the highest rate. */
	Optional<CategoryEntity> findFirstByDiscountRateGreaterThanOrderByDiscountRateDesc(BigDecimal rate);
}
