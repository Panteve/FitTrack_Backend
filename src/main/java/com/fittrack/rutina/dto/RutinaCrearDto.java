package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


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
