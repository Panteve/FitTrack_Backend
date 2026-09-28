package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos necesarios para crear una rutina con sus ejercicios.
 *
 * @param nombre nombre de la rutina
 * @param descripcion descripción opcional de la rutina
 * @param diaSemana día asignado a la rutina
 * @param ejercicios configuraciones de los ejercicios asociados
 */
public record RutinaCrearDto(
        @NotBlank @Size(max = 100)
        String nombre,

        @Size(max = 500)
        String descripcion,

        @NotNull
        DiaSemana diaSemana,

        @NotEmpty @Valid
        List<RutinaEjercicioCrearDto> ejercicios
) {
}
