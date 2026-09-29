package com.fittrack.entrenamiento.dto;

import java.time.LocalDate;
import java.util.List;

/** Detalle completo de un entrenamiento y sus series realizadas. */
public record EntrenamientoDetalleDto(
        Long id,
        Long rutinaId,
        String nombreRutina,
        LocalDate fecha,
        Integer duracionMinutos,
        Integer seriesTotales,
        String notas,
        String fotoUrl,
        List<RegistroSerieDetalleDto> series) {
}
