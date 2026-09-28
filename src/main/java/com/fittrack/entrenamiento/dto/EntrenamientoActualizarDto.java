package com.fittrack.entrenamiento.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Datos editables que reemplazan por completo un entrenamiento. */
public record EntrenamientoActualizarDto(
        @NotNull @Positive Long rutinaId,
        @NotNull @PastOrPresent LocalDate fecha,
        @NotNull @Positive Integer duracionMinutos,
        @Size(max = 1000) String notas,
        @NotEmpty List<@Valid RegistroSerieRequest> series) {
}
