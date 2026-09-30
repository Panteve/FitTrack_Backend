package com.fittrack.rutina.dto;

import java.util.List;


public record RutinaEjercicioDetalleDto(
        Long id,
        Long ejercicioId,
        String nombre,
        String grupoMuscular,
        Integer orden,
        List<RutinaSerieDetalleDto> series
) {
}
