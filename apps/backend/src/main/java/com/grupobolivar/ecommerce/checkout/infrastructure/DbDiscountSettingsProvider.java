package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.catalog.infrastructure.CategoryEntity;
import com.grupobolivar.ecommerce.catalog.infrastructure.CategoryJpaRepository;
import com.grupobolivar.ecommerce.checkout.application.DiscountSettingsProvider;
import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountConfig;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/** Builds the {@link DiscountConfig} from the catalog (category rate) and app config. */
@Component
public class DbDiscountSettingsProvider implements DiscountSettingsProvider {

	private final CategoryJpaRepository categories;
	private final DiscountProperties properties;

	public DbDiscountSettingsProvider(CategoryJpaRepository categories, DiscountProperties properties) {
		this.categories = categories;
		this.properties = properties;
	}

	@Override
	public DiscountConfig current() {
		CategoryEntity discounted = categories
				.findFirstByDiscountRateGreaterThanOrderByDiscountRateDesc(BigDecimal.ZERO)
				.orElseThrow(() -> new IllegalStateException("No hay categoría con descuento configurada"));

		return new DiscountConfig(
				discounted.getName(),
				discounted.getDiscountRate(),
				properties.volume().threshold(),
				properties.volume().percent(),
				properties.cap().percent());
	}
}
