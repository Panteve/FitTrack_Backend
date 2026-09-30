package com.fittrack.rutina.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.fittrack.ejercicio.entity.Ejercicio;

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
import lombok.Getter;
import lombok.Setter;


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


    public RutinaEjercicio(Ejercicio ejercicio, Integer orden) {
        this.ejercicio = ejercicio;
        this.orden = orden;
    }

    public void agregarSerie(RutinaSerie serie) {
        serie.setRutinaEjercicio(this);
        series.add(serie);
    }
}
