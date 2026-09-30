package com.fittrack.rutina.dto;

import java.math.BigDecimal;


public record RutinaSerieDetalleDto(
        Long id,
        Integer numeroSerie,
        Integer repeticionesObjetivo,
        BigDecimal pesoObjetivo
) {
}
