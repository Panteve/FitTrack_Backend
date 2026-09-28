package com.fittrack.shared.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fittrack.shared.response.ApiErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Errores de negocio lanzados por los modulos. */
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiErrorResponseDto> handleApiException(ApiException ex, HttpServletRequest request) {
		return build(ex.getStatus(), ex.getMessage(), request);
	}

	/** Credenciales validas pero permisos insuficientes. */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponseDto> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso.", request);
	}

	/** DTOs que no cumplen las anotaciones de validacion ({@code @Valid}). */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponseDto> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String detalle = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return build(HttpStatus.BAD_REQUEST,
				detalle.isBlank() ? "La peticion no supera la validacion requerida." : detalle, request);
	}

	/** Peticiones con el body ausente o con un JSON mal formado. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponseDto> handleUnreadableMessage(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "La petición debe incluir un body JSON válido.", request);
	}

	/** Content-Type no soportado, por ejemplo un formulario en vez de JSON. */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiErrorResponseDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
				"El Content-Type '" + ex.getContentType() + "' no está soportado. Usa application/json.", request);
	}

	/** Metodo HTTP no implementado por el endpoint (por ejemplo GET sobre un POST). */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		return build(
				HttpStatus.CONFLICT,
				"La operación no se puede completar porque el recurso está relacionado con otros datos.",
				request);
	}
	/**
	 * Cualquier otra excepcion. Se registra con su stack trace completo para que el fallo
	 * sea diagnosticable desde el log y no solo como un 500 opaco en la respuesta.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponseDto> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado.", request);
	}

	private ResponseEntity<ApiErrorResponseDto> build(HttpStatus status, String message, HttpServletRequest request) {
		ApiErrorResponseDto body = new ApiErrorResponseDto(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI());
		return ResponseEntity.status(status).body(body);
	}
}
