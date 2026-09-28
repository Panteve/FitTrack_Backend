package com.fittrack.rutina.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import com.fittrack.ejercicio.entity.Ejercicio;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa la aparición de un ejercicio dentro de una rutina.
 */
@Entity
@Table(name = "rutina_ejercicio")
@Getter
@Setter
public class RutinaEjercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "orden")
    private Integer orden;

    @OneToMany(
            mappedBy = "rutinaEjercicio",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("numeroSerie ASC")
    @BatchSize(size = 50)
    private List<RutinaSerie> series = new ArrayList<>();

    /** Constructor requerido por JPA. */
    public RutinaEjercicio() {
    }

    /**
     * Crea una configuración de ejercicio para una rutina.
     *
     * @param ejercicio ejercicio asociado
     * @param orden posición dentro de la rutina
     */
    public RutinaEjercicio(Ejercicio ejercicio, Integer orden) {
        this.ejercicio = ejercicio;
        this.orden = orden;
    }

    /**
     * Agrega una serie planificada y mantiene ambos lados de la relación.
     *
     * @param serie serie planificada para el ejercicio
     */
    public void agregarSerie(RutinaSerie serie) {
        serie.setRutinaEjercicio(this);
        series.add(serie);
    }
}
