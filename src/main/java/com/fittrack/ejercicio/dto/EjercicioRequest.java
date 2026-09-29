package com.fittrack.ejercicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EjercicioRequest(// request para crear un nuevo ejercicio
        @NotBlank @Size(max = 100)
        String nombre,
        @NotBlank @Size(max = 100)
        String grupoMuscular) {
}
