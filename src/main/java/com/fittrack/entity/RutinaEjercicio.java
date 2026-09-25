package com.fittrack.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rutina_ejercicio")
public class RutinaEjercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "series_objetivo", nullable = false)
    private Integer seriesObjetivo;

    @Column(name = "repeticiones_objetivo", nullable = false)
    private Integer repeticionesObjetivo;

    @Column(name = "peso_objetivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoObjetivo;

    @Column(name = "orden")
    private Integer orden;

}