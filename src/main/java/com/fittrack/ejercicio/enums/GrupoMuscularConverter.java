package com.fittrack.ejercicio.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte el enum de grupo muscular al texto usado actualmente en la base de datos.
 */
@Converter
public class GrupoMuscularConverter implements AttributeConverter<GrupoMuscular, String> {

    /** {@inheritDoc} */
    @Override
    public String convertToDatabaseColumn(GrupoMuscular grupoMuscular) {
        return grupoMuscular == null ? null : grupoMuscular.getValor();
    }

    /** {@inheritDoc} */
    @Override
    public GrupoMuscular convertToEntityAttribute(String valor) {
        return valor == null ? null : GrupoMuscular.desdeValor(valor);
    }
}
