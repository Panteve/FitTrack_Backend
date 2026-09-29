package com.fittrack.home.dto;

import java.time.LocalDate;

/**
 * Resume un entrenamiento reciente para la pantalla principal.
 *
 * @param id identificador del entrenamiento
 * @param nombre nombre de la rutina realizada
 * @param fecha fecha del entrenamiento
 * @param duracionMinutos duración registrada en minutos
 */
public record UltimoEntrenamientoDto(
        Long id,
        String nombre,
        LocalDate fecha,
        Integer duracionMinutos) {
}
