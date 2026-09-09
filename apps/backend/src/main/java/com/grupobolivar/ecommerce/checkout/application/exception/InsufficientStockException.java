package com.grupobolivar.ecommerce.checkout.application.exception;

/** Raised when a checkout line asks for more units than are in stock. Maps to HTTP 409. */
public class InsufficientStockException extends RuntimeException {

	private final long productId;

	public InsufficientStockException(long productId, String productName, int requested, int available) {
		super("Stock insuficiente para '" + productName + "': se pidieron " + requested
				+ " y hay " + available);
		this.productId = productId;
	}

	public long getProductId() {
		return productId;
	}
}
