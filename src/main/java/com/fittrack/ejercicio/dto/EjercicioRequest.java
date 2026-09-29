package com.fittrack.ejercicio.dto;

import com.fittrack.ejercicio.enums.GrupoMuscular;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos requeridos para crear o actualizar un ejercicio.
 *
 * @param nombre nombre visible del ejercicio
 * @param grupoMuscular grupo muscular permitido
 */
public record EjercicioRequest(
        @NotBlank @Size(max = 100)
        String nombre,
        @NotNull
        GrupoMuscular grupoMuscular) {
}
