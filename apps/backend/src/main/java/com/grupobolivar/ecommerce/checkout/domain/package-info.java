/**
 * Checkout domain core: cart model, cumulative discount engine and coupons.
 *
 * <p><b>Architecture rule:</b> this package and its subpackages are plain Java. They must
 * not import {@code org.springframework.*}, {@code jakarta.persistence.*} or web-layer
 * types. Outward dependencies (persistence, coupon catalog) are expressed as ports
 * (interfaces) implemented by the infrastructure. This keeps the mathematical rules
 * isolated from persistence and controllers (requirement 4.1 of the assignment).
 */
package com.grupobolivar.ecommerce.checkout.domain;
