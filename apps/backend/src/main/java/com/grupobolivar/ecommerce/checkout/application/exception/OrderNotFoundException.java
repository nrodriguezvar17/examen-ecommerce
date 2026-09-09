package com.grupobolivar.ecommerce.checkout.application.exception;

/** Raised when no order matches the given radicado. Maps to HTTP 404. */
public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(String radicado) {
		super("No existe una orden con radicado " + radicado);
	}
}
