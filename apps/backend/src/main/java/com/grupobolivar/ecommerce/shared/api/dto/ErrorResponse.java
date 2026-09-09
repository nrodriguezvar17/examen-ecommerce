package com.grupobolivar.ecommerce.shared.api.dto;

import java.time.Instant;

/** Uniform error body for the REST API. */
public record ErrorResponse(int status, String message, Instant timestamp) {

	public static ErrorResponse of(int status, String message) {
		return new ErrorResponse(status, message, Instant.now());
	}
}
