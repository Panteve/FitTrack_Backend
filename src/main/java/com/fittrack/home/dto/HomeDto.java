package com.fittrack.home.dto;

import java.util.List;


public record HomeDto(// DTO que representa la información principal del usuario autenticado, incluyendo la próxima rutina y los últimos entrenamientos
        ProximaRutinaDto proximaRutina,
        List<UltimoEntrenamientoDto> ultimosEntrenamientos) {
}
