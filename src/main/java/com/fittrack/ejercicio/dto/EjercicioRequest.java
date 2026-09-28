package com.fittrack.ejercicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EjercicioRequest(
        @NotBlank @Size(max = 100) String nombre,
        String grupoMuscular
) {}
