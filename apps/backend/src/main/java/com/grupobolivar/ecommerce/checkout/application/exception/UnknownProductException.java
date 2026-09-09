package com.grupobolivar.ecommerce.checkout.application.exception;

/** Raised when a checkout line references a product that is not in the catalog. Maps to HTTP 404. */
public class UnknownProductException extends RuntimeException {

	public UnknownProductException(long productId) {
		super("El producto " + productId + " no existe");
	}
}
