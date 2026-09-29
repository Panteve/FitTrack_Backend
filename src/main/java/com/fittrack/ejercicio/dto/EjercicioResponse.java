package com.fittrack.ejercicio.dto;

import com.fittrack.ejercicio.enums.GrupoMuscular;

/**
 * Datos de un ejercicio enviados al cliente.
 *
 * @param id identificador del ejercicio
 * @param nombre nombre visible del ejercicio
 * @param grupoMuscular grupo muscular del ejercicio
 */
public record EjercicioResponse(
        Long id,
        String nombre,
        GrupoMuscular grupoMuscular
) {}
