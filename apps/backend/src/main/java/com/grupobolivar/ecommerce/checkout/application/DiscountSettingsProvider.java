package com.grupobolivar.ecommerce.checkout.application;

import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountConfig;

/**
 * Port: supplies the current discount configuration (discounted category and its rate from
 * the catalog, volume threshold/rate and the cap from application config).
 */
public interface DiscountSettingsProvider {

	DiscountConfig current();
}
