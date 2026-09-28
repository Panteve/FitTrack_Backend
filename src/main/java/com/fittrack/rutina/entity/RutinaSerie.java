package com.fittrack.rutina.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa una serie planificada para un ejercicio dentro de una rutina.
 */
@Entity
@Table(name = "rutina_serie")
@Getter
@Setter
public class RutinaSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina_ejercicio", nullable = false)
    private RutinaEjercicio rutinaEjercicio;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "repeticiones_objetivo", nullable = false)
    private Integer repeticionesObjetivo;

    @Column(name = "peso_objetivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoObjetivo;

    /** Constructor requerido por JPA. */
    public RutinaSerie() {
    }

    /**
     * Crea una serie planificada.
     *
     * @param numeroSerie número de la serie dentro del ejercicio
     * @param repeticionesObjetivo repeticiones planificadas
     * @param pesoObjetivo peso planificado
     */
    public RutinaSerie(
            Integer numeroSerie,
            Integer repeticionesObjetivo,
            BigDecimal pesoObjetivo) {
        this.numeroSerie = numeroSerie;
        this.repeticionesObjetivo = repeticionesObjetivo;
        this.pesoObjetivo = pesoObjetivo;
    }
}
