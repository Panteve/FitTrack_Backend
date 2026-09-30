package com.fittrack.entrenamiento.dto;

import java.math.BigDecimal;

//Detalle de una serie realizada en un entrenamiento.
public record RegistroSerieDetalleDto(
        Long id,
        Long rutinaEjercicioId,
        Long ejercicioId,
        String nombreEjercicio,
        String grupoMuscular,
        Integer ordenEjercicio,
        Integer numeroSerie,
        Integer repeticiones,
        BigDecimal peso) {
}
