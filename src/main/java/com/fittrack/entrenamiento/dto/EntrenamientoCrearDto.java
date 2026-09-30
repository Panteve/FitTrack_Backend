package com.fittrack.entrenamiento.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record EntrenamientoCrearDto(//Datos necesarios para crear un entrenamiento finalizado, 
// incluyendo la rutina asociada, fecha, duración, notas y series realizadas.
        @NotNull @Positive Long rutinaId,
        @NotNull @PastOrPresent LocalDate fecha,
        @NotNull @Positive Integer duracionMinutos,
        @Size(max = 1000) String notas,
        @Positive Integer seriesTotales,
        @NotEmpty List<@Valid RegistroSerieRequest> series) {
}
