package com.grupobolivar.ecommerce.shared.api;

import com.grupobolivar.ecommerce.catalog.application.ProductNotFoundException;
import com.grupobolivar.ecommerce.checkout.application.EmptyCartException;
import com.grupobolivar.ecommerce.checkout.application.InsufficientStockException;
import com.grupobolivar.ecommerce.checkout.application.OrderNotFoundException;
import com.grupobolivar.ecommerce.checkout.application.UnknownProductException;
import com.grupobolivar.ecommerce.shared.api.dto.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Translates domain/validation errors into the REST error contract. */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmptyCartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleEmptyCart(EmptyCartException exception) {
		return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
	}

	@ExceptionHandler({
			UnknownProductException.class, OrderNotFoundException.class, ProductNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFound(RuntimeException exception) {
		return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage());
	}

	@ExceptionHandler(InsufficientStockException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleInsufficientStock(InsufficientStockException exception) {
		return ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidBody(MethodArgumentNotValidException exception) {
		String detail = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
				detail.isBlank() ? "Petición inválida" : detail);
	}
}
