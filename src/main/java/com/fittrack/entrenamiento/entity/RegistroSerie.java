package com.fittrack.entrenamiento.entity;

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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "registro_serie")
@Getter
@Setter
public class RegistroSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_entrenamiento", nullable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "repeticiones", nullable = false)
    private Integer repeticiones;

    @Column(name = "peso", nullable = false, precision = 10, scale = 2)
    private BigDecimal peso;

    // Constructor
    public RegistroSerie() {
    }

    public RegistroSerie(Entrenamiento entrenamiento, Ejercicio ejercicio, Integer numeroSerie, Integer repeticiones,
            BigDecimal peso) {
        this.entrenamiento = entrenamiento;
        this.ejercicio = ejercicio;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }
}
