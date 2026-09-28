package com.fittrack.entrenamiento.dto;

import java.time.LocalDate;

/** Resumen de un entrenamiento para listados. */
public record EntrenamientoDto(
        Long id,
        Long rutinaId,
        String nombreRutina,
        LocalDate fecha,
        Integer duracionMinutos,
        String notas,
        Boolean tieneFoto) {
}
