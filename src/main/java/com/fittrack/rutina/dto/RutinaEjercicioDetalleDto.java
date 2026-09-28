package com.fittrack.rutina.dto;

import java.util.List;

/**
 * Representa la configuración de un ejercicio dentro de una rutina.
 *
 * @param id identificador de la asociación entre rutina y ejercicio
 * @param ejercicioId identificador del ejercicio asociado
 * @param nombre nombre del ejercicio asociado
 * @param orden posición del ejercicio dentro de la rutina
 * @param series series planificadas para el ejercicio
 */
public record RutinaEjercicioDetalleDto(
        Long id,
        Long ejercicioId,
        String nombre,
        Integer orden,
        List<RutinaSerieDetalleDto> series
) {
}
