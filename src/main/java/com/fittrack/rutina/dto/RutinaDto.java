package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;

/**
 * Representa una rutina con los nombres de sus ejercicios asociados.
 *
 * @param id identificador de la rutina
 * @param nombre nombre de la rutina
 * @param descripcion descripción opcional de la rutina
 * @param diaSemana día de la semana asignado
 * @param ejercicios nombres de los ejercicios asociados, en su orden configurado
 */
public record RutinaDto(
        Long id,
        String nombre,
        String descripcion,
        DiaSemana diaSemana,
        List<String> ejercicios
) {
}
