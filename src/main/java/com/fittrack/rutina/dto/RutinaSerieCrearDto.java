package com.fittrack.rutina.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


public record RutinaSerieCrearDto(// Configuración de una serie planificada para un ejercicio dentro de una rutina
        @NotNull @Positive
        Integer numeroSerie,

        @NotNull @PositiveOrZero
        Integer repeticionesObjetivo,

        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal pesoObjetivo
) {
}
