package com.fittrack.shared.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fittrack.shared.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de errores de la API.
 *
 * <p>Al ser transversal, cualquier modulo que lance una excepcion obtiene la misma
 * forma de respuesta sin duplicar la logica de conversion en cada controlador.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Errores de negocio lanzados por los modulos. */
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
		return build(ex.getStatus(), ex.getMessage(), request);
	}

	/** Credenciales validas pero permisos insuficientes. */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso.", request);
	}

	/**
	 * Red de seguridad para evitar que una excepcion no controlada escape con la
	 * respuesta por defecto de Spring, que no cumple el contrato de
	 * {@link ApiErrorResponse} y podria filtrar detalles internos.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado.", request);
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI());
		return ResponseEntity.status(status).body(body);
	}
}
