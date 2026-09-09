package com.grupobolivar.ecommerce.catalog.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the product catalog, owned by the domain. The implementation
 * ({@code ProductRepositoryAdapter} in {@code infrastructure}) maps JPA entities to
 * {@link Product}; callers never see a persistence type. It is the single point of contact
 * with the product tables — both this feature and {@code checkout} go through it.
 */
public interface ProductRepository {

	/** Active products, ordered by SKU. */
	List<Product> findAllActive();

	Optional<Product> findById(long id);
}
