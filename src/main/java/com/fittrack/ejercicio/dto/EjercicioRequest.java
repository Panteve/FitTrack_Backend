package com.fittrack.ejercicio.dto;

import com.fittrack.ejercicio.enums.GrupoMuscular;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record EjercicioRequest(//DTO para la solicitud de creación o actualización de un ejercicio, contiene el nombre y el grupo muscular del ejercicio
        @NotBlank @Size(max = 100)
        String nombre,
        @NotNull
        GrupoMuscular grupoMuscular) {
}
