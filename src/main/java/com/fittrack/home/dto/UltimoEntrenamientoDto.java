package com.fittrack.home.dto;

import java.time.LocalDate;


public record UltimoEntrenamientoDto(// DTO que representa el último entrenamiento realizado por el usuario, incluyendo su ID, nombre, fecha y duración en minutos
        Long id,
        String nombre,
        LocalDate fecha,
        Integer duracionMinutos) {
}
