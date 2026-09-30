package com.fittrack.entrenamiento.dto;

import jakarta.validation.constraints.Size;

/**
 * Datos para actualizar las notas de un entrenamiento.
 *
 * @param notas notas opcionales del entrenamiento
 */
public record EntrenamientoActualizarNotasDto(
        @Size(max = 1000) String notas) {
}
