package com.fittrack.entrenamiento.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//Datos de una serie realizada durante un entrenamiento.
public record RegistroSerieRequest(
        @NotNull @Positive Long rutinaEjercicioId,
        @NotNull @Positive Integer numeroSerie,
        @NotNull @Positive Integer repeticiones,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2)
        BigDecimal peso) {
}
