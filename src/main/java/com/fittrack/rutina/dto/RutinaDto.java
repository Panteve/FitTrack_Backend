package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;


public record RutinaDto(
        Long id,
        String nombre,
        String descripcion,
        DiaSemana diaSemana,
        List<String> ejercicios
) {
}
