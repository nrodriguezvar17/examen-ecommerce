package com.grupobolivar.ecommerce.checkout.domain.order;

/** Order lifecycle. The name matches the {@code order_statuses.code} value. */
public enum OrderStatus {
	COMPRADO,
	ENVIADO,
	EN_REPARTO,
	ENTREGADO
}
