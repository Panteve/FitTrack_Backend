package com.fittrack.ejercicio.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum GrupoMuscular {
    PECHO("Pecho"),
    ESPALDA("Espalda"),
    PIERNA("Pierna"),
    BRAZO("Brazo");

    private final String valor;

    GrupoMuscular(String valor) {
        this.valor = valor;
    }


    @JsonValue
    public String getValor() {
        return valor;
    }


    @JsonCreator
    public static GrupoMuscular desdeValor(String valor) {//Método estático que permite crear una instancia de GrupoMuscular a partir de un valor de cadena
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
