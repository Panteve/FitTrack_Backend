package com.fittrack.home.dto;

/**
 * Resume la rutina sugerida para iniciar desde la pantalla principal.
 *
 * @param id identificador de la rutina
 * @param numeroDeEjercicios cantidad de ejercicios configurados
 * @param nombre nombre de la rutina
 */
public record ProximaRutinaDto(
        Long id,
        Integer numeroDeEjercicios,
        String nombre) {
}
