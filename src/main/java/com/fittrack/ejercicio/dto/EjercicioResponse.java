package com.fittrack.ejercicio.dto;

import com.fittrack.ejercicio.enums.GrupoMuscular;


public record EjercicioResponse(//DTO para la respuesta de un ejercicio, contiene el ID, nombre y grupo muscular del ejercicio
        Long id,
        String nombre,
        GrupoMuscular grupoMuscular
) {}
