package com.fittrack.rutina.dto;

import java.util.List;

import com.fittrack.rutina.enums.DiaSemana;


public record RutinaDetalleDto(
        Long id,
        String nombre,
        String descripcion,
        DiaSemana diaSemana,
        List<RutinaEjercicioDetalleDto> ejercicios
) {
}
