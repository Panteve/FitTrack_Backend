package com.fittrack.rutina.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Configuración de un ejercicio que se agregará a una rutina.
 *
 * @param ejercicioId identificador del ejercicio existente
 * @param seriesObjetivo cantidad objetivo de series
 * @param repeticionesObjetivo cantidad objetivo de repeticiones por serie
 * @param pesoObjetivo peso objetivo configurado
 * @param orden posición del ejercicio dentro de la rutina
 */
public record RutinaEjercicioCrearDto(
        @NotNull @Positive
        Long ejercicioId,

        @NotNull @Positive
        Integer seriesObjetivo,

        @NotNull @Positive
        Integer repeticionesObjetivo,

        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal pesoObjetivo,

        @NotNull @Positive
        Integer orden
) {
}
