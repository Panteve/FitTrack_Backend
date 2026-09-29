package com.fittrack.entrenamiento.dto;

import java.math.BigDecimal;

/**
 * Detalle de una serie realizada dentro de un entrenamiento.
 *
 * @param id identificador del registro
 * @param rutinaEjercicioId identificador original del bloque de rutina
 * @param ejercicioId identificador del ejercicio realizado
 * @param nombreEjercicio nombre del ejercicio realizado
 * @param grupoMuscular grupo muscular principal del ejercicio
 * @param ordenEjercicio posición del ejercicio en el entrenamiento
 * @param numeroSerie número de la serie dentro del ejercicio
 * @param repeticiones repeticiones realizadas
 * @param peso peso utilizado
 */
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
