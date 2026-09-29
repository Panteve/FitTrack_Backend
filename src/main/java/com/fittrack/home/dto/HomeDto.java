package com.fittrack.home.dto;

import java.util.List;

/**
 * Agrupa la información necesaria para cargar la pantalla principal.
 *
 * @param proximaRutina rutina sugerida, o {@code null} si el usuario no tiene rutinas
 * @param ultimosEntrenamientos últimos entrenamientos del usuario
 */
public record HomeDto(
        ProximaRutinaDto proximaRutina,
        List<UltimoEntrenamientoDto> ultimosEntrenamientos) {
}
