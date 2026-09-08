package com.grupobolivar.ecommerce.checkout.domain.discount;

import java.math.BigDecimal;

/**
 * Configuración del motor, en tipos del dominio (sin anotaciones de Spring). La capa
 * de infraestructura lee las propiedades externas y construye este record antes de
 * pasarlo al {@link DiscountRuleFactory}.
 */
public record DiscountConfig(
		String categoryName,
		BigDecimal categoryPercent,
		BigDecimal volumeThreshold,
		BigDecimal volumePercent,
		BigDecimal capPercent) {
}
