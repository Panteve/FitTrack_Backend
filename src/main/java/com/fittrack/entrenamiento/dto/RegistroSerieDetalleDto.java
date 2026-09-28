package com.fittrack.entrenamiento.dto;

import java.math.BigDecimal;

/** Detalle de una serie realizada. */
public record RegistroSerieDetalleDto(
        Long id,
        Long rutinaEjercicioId,
        Long ejercicioId,
        String nombreEjercicio,
        Integer ordenEjercicio,
        Integer numeroSerie,
        Integer repeticiones,
        BigDecimal peso) {
}
