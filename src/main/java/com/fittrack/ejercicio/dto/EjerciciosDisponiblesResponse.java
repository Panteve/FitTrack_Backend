package com.fittrack.ejercicio.dto;

import java.util.List;

/**
 * Separa los ejercicios disponibles según su propietario.
 *
 * @param ejerciciosSistema ejercicios generales sin usuario propietario
 * @param misEjercicios ejercicios creados por el usuario autenticado
 */
public record EjerciciosDisponiblesResponse(
        List<EjercicioResponse> ejerciciosSistema,
        List<EjercicioResponse> misEjercicios) {
}
