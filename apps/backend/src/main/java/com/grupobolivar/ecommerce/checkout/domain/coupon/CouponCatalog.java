package com.grupobolivar.ecommerce.checkout.domain.coupon;

import java.util.Optional;

/**
 * Puerto: fuente de cupones. La implementación (en memoria, base de datos, servicio
 * externo...) vive en {@code infrastructure} y se inyecta en el dominio. Así el motor
 * de descuentos no depende de cómo se almacenan los cupones.
 */
public interface CouponCatalog {

	/** Devuelve el cupón si existe y está activo; {@link Optional#empty()} en caso contrario. */
	Optional<Coupon> findActive(String code);
}
