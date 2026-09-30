package com.fittrack.home.dto;


public record ProximaRutinaDto(// DTO que representa la próxima rutina sugerida para el usuario, incluyendo su ID, nombre y número de ejercicios
        Long id,
        Integer numeroDeEjercicios,
        String nombre) {
}
