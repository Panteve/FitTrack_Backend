package com.fittrack.rutina.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Configuración de un ejercicio que se agregará a una rutina.
 *
 * @param ejercicioId identificador del ejercicio existente
 * @param orden posición del ejercicio dentro de la rutina
 * @param series series planificadas para el ejercicio
 */
public record RutinaEjercicioCrearDto(
        @NotNull @Positive
        Long ejercicioId,

        @NotNull @Positive
        Integer orden,

        @NotEmpty @Valid
        List<RutinaSerieCrearDto> series
) {
}
