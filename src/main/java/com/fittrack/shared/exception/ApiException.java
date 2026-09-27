package com.fittrack.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base para los errores de negocio de la aplicacion.
 *
 * <p>Los modulos de negocio lanzan esta excepcion (o una subclase) en lugar de crear
 * excepciones propias por cada caso, lo que evita que {@code shared} se llene de clases
 * sin dueno claro. El {@link GlobalExceptionHandler} la traduce al
 * {@link com.fittrack.shared.response.ApiErrorResponse} con el estado HTTP que transporta.</p>
 */
public class ApiException extends RuntimeException {

	private final HttpStatus status;

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
