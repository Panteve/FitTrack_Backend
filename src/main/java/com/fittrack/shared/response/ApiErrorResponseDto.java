package com.fittrack.shared.response;

import java.time.Instant;


public record ApiErrorResponseDto(// Representa la estructura de respuesta de error de la API, incluyendo información sobre el error y el contexto de la solicitud.
		Instant timestamp,
		int status,
		String error,
		String message,
		String path) {
}
