package com.fittrack.rutina.dto;

import java.math.BigDecimal;

/**
 * Representa una serie planificada dentro del detalle de una rutina.
 *
 * @param id identificador de la serie planificada
 * @param numeroSerie número de la serie dentro del ejercicio
 * @param repeticionesObjetivo repeticiones planificadas
 * @param pesoObjetivo peso planificado
 */
public record RutinaSerieDetalleDto(
        Long id,
        Integer numeroSerie,
        Integer repeticionesObjetivo,
        BigDecimal pesoObjetivo
) {
}
