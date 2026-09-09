package com.grupobolivar.ecommerce.catalog.infrastructure;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Spring Data repository for the product catalog. */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

	List<ProductEntity> findByActiveTrueOrderBySkuAsc();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update ProductEntity p set p.stock = p.stock - :quantity "
			+ "where p.id = :id and p.stock >= :quantity")
	int decrementStock(@Param("id") long id, @Param("quantity") int quantity);
}
