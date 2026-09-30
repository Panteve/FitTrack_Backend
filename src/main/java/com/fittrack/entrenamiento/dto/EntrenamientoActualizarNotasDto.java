package com.fittrack.entrenamiento.dto;

import jakarta.validation.constraints.Size;


public record EntrenamientoActualizarNotasDto(// Contiene la información para actualizar las notas de un entrenamiento.
        @Size(max = 1000) String notas) {
}
