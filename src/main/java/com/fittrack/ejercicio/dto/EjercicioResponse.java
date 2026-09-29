package com.fittrack.ejercicio.dto;

public record EjercicioResponse(// response para obtener un ejercicio
    Long id,
    String nombre,
    String grupoMuscular
) {}
