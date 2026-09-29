package com.fittrack.ejercicio.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Grupos musculares disponibles para clasificar un ejercicio.
 */
public enum GrupoMuscular {
    PECHO("Pecho"),
    ESPALDA("Espalda"),
    PIERNA("Pierna"),
    BRAZO("Brazo");

    private final String valor;

    GrupoMuscular(String valor) {
        this.valor = valor;
    }

    /**
     * Obtiene el nombre legible enviado a los clientes y guardado en la base de datos.
     *
     * @return nombre legible del grupo muscular
     */
    @JsonValue
    public String getValor() {
        return valor;
    }

    /**
     * Convierte el texto recibido por la API o la base de datos en un grupo muscular.
     *
     * @param valor texto que representa el grupo muscular
     * @return grupo muscular correspondiente
     * @throws IllegalArgumentException si el valor no corresponde a un grupo permitido
     */
    @JsonCreator
    public static GrupoMuscular desdeValor(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El grupo muscular es obligatorio.");
        }

        String valorNormalizado = valor.trim();

        for (GrupoMuscular grupoMuscular : values()) {
            if (grupoMuscular.valor.equalsIgnoreCase(valorNormalizado)
                    || grupoMuscular.name().equalsIgnoreCase(valorNormalizado)) {
                return grupoMuscular;
            }
        }

        throw new IllegalArgumentException("Grupo muscular no válido: " + valorNormalizado);
    }
}
