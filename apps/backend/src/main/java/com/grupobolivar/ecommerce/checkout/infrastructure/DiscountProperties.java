package com.grupobolivar.ecommerce.checkout.infrastructure;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Discount tuning that lives in {@code application.yml} (not catalog data): the volume
 * threshold and rate, and the absolute cap. The category rate comes from the DB.
 */
@ConfigurationProperties(prefix = "discount")
public record DiscountProperties(Volume volume, Cap cap) {

	public record Volume(BigDecimal threshold, BigDecimal percent) {
	}

	public record Cap(BigDecimal percent) {
	}
}
