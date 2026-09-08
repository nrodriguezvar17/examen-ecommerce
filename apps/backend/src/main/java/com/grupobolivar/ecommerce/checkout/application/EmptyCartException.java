package com.grupobolivar.ecommerce.checkout.application;

/** Raised when a checkout request carries no cart lines. Maps to HTTP 400. */
public class EmptyCartException extends RuntimeException {

	public EmptyCartException() {
		super("El carrito no tiene productos");
	}
}
