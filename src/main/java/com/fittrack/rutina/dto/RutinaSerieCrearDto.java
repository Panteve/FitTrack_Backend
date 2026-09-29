package com.fittrack.rutina.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de una serie planificada para un ejercicio de la rutina.
 *
 * @param numeroSerie número de la serie dentro del ejercicio
 * @param repeticionesObjetivo repeticiones planificadas
 * @param pesoObjetivo peso planificado
 */
public record RutinaSerieCrearDto(
        @NotNull @Positive
        Integer numeroSerie,

        @NotNull @PositiveOrZero
        Integer repeticionesObjetivo,

        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal pesoObjetivo
) {
}
