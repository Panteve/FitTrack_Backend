package com.fittrack.shared.response;

import java.time.Instant;

/**
 * Respuesta de error estandarizada para toda la API.
 *
 * <p>Es la contraparte de los DTOs de cada módulo: los controladores nunca devuelven
 * entidades JPA, y tampoco deben construir a mano el cuerpo de un error. Al concentrar
 * la forma del error aqui, todos los módulos exponen el mismo contrato ante un fallo.</p>
 *
 * @param timestamp momento en que se produjo el error
 * @param status    codigo de estado HTTP
 * @param error     razon textual del codigo de estado
 * @param message   descripcion legible del error
 * @param path      ruta que se estaba procesando
 */
public record ApiErrorResponseDto(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path) {
}
