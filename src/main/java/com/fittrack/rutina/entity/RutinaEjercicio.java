package com.fittrack.rutina.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fittrack.ejercicio.entity.Ejercicio;

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

    // Constructor
    public RutinaEjercicio() {
    }

    public RutinaEjercicio(Rutina rutina, Ejercicio ejercicio, Integer seriesObjetivo, Integer repeticionesObjetivo,
            BigDecimal pesoObjetivo, Integer orden) {
        this.rutina = rutina;
        this.ejercicio = ejercicio;
        this.seriesObjetivo = seriesObjetivo;
        this.repeticionesObjetivo = repeticionesObjetivo;
        this.pesoObjetivo = pesoObjetivo;
        this.orden = orden;
    }
    
    // Getters and Setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rutina getRutina() {
        return this.rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public Ejercicio getEjercicio() {
        return this.ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Integer getSeriesObjetivo() {
        return this.seriesObjetivo;
    }

    public void setSeriesObjetivo(Integer seriesObjetivo) {
        this.seriesObjetivo = seriesObjetivo;
    }

    public Integer getRepeticionesObjetivo() {
        return this.repeticionesObjetivo;
    }

    public void setRepeticionesObjetivo(Integer repeticionesObjetivo) {
        this.repeticionesObjetivo = repeticionesObjetivo;
    }

    public BigDecimal getPesoObjetivo() {
        return this.pesoObjetivo;
    }

    public void setPesoObjetivo(BigDecimal pesoObjetivo) {
        this.pesoObjetivo = pesoObjetivo;
    }

    public Integer getOrden() {
        return this.orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}