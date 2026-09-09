package com.grupobolivar.ecommerce.catalog.application;

/** Thrown when a product id has no match in the catalog. Maps to HTTP 404. */
public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(long id) {
		super("No existe un producto con id " + id);
	}
}
