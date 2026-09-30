package com.fittrack.ejercicio.dto;

import java.util.List;

/**
 * Separa los ejercicios disponibles según su propietario.
 *
 * @param ejerciciosSistema ejercicios generales sin usuario propietario
 * @param misEjercicios ejercicios creados por el usuario autenticado
 */
public record EjerciciosDisponiblesResponse(//DTO para la respuesta de los ejercicios disponibles, 
// contiene los ejercicios del sistema y los ejercicios del usuario autenticado
        List<EjercicioResponse> ejerciciosSistema,
        List<EjercicioResponse> misEjercicios) {
}
