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
@Table(
        name = "registro_serie",
        uniqueConstraints = @jakarta.persistence.UniqueConstraint(
                name = "uk_registro_serie_entrenamiento_bloque_numero",
                columnNames = {
                        "id_entrenamiento",
                        "id_rutina_ejercicio_origen",
                        "numero_serie"
                }))
@Getter
@Setter
public class RegistroSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", nullable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "id_rutina_ejercicio_origen", nullable = false)
    private Long rutinaEjercicioId;

    @Column(name = "orden_ejercicio", nullable = false)
    private Integer ordenEjercicio;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "repeticiones", nullable = false)
    private Integer repeticiones;

    @Column(name = "peso", nullable = false, precision = 10, scale = 2)
    private BigDecimal peso;

    /** Constructor requerido por JPA. */
    public RegistroSerie() {
    }

    /**
     * Crea el registro de una serie realizada.
     *
     * @param ejercicio ejercicio realizado
     * @param rutinaEjercicioId identificador original del bloque de rutina
     * @param ordenEjercicio posición del bloque al finalizar el entrenamiento
     * @param numeroSerie número de la serie dentro del bloque
     * @param repeticiones repeticiones realizadas
     * @param peso peso utilizado
     */
    public RegistroSerie(
            Ejercicio ejercicio,
            Long rutinaEjercicioId,
            Integer ordenEjercicio,
            Integer numeroSerie,
            Integer repeticiones,
            BigDecimal peso) {
        this.ejercicio = ejercicio;
        this.rutinaEjercicioId = rutinaEjercicioId;
        this.ordenEjercicio = ordenEjercicio;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }
}
