package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;

/**
 * Representa el detalle de una rutina y la configuración de sus ejercicios.
 *
 * @param id identificador de la rutina
 * @param nombre nombre de la rutina
 * @param descripcion descripción opcional de la rutina
 * @param diaSemana día de la semana asignado
 * @param ejercicios configuración de los ejercicios asociados
 */
public record RutinaDetalleDto(
        Long id,
        String nombre,
        String descripcion,
        DiaSemana diaSemana,
        List<RutinaEjercicioDetalleDto> ejercicios
) {
}
