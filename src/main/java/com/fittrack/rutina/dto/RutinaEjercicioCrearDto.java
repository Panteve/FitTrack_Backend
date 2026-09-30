package com.fittrack.rutina.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record RutinaEjercicioCrearDto(
        @NotNull @Positive
        Long ejercicioId,

        @NotNull @Positive
        Integer orden,

        @NotEmpty @Valid
        List<RutinaSerieCrearDto> series
) {
}
