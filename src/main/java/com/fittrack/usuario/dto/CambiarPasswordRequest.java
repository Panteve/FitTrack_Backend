package com.fittrack.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Petición para cambiar la contraseña del usuario autenticado.
 *
 * <p>La contraseña nueva aplica la misma regla que el registro: entre 8 y 72
 * caracteres, porque BCrypt solo considera los primeros 72 bytes. Las
 * contraseñas se reciben tal cual, sin recortes ni normalización.</p>
 */
public class CambiarPasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @Size(
            min = 8,
            max = 72,
            message = "La contraseña nueva debe tener entre 8 y 72 caracteres"
    )
    private String passwordNueva;

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }
}
