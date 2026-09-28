package com.fittrack.rutina.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import com.fittrack.rutina.enums.DiaSemana;
import com.fittrack.usuario.entity.Usuario;

import lombok.Getter;
import lombok.Setter;

/**
 * Representa una rutina perteneciente a un usuario.
 */
@Entity
@Table(name = "rutina")
@Getter
@Setter
public class Rutina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    // El default en la definicion permite que ddl-auto=update agregue la columna
    // en bases ya pobladas: un "add column ... not null" sin default seria
    // rechazado por PostgreSQL sobre filas existentes.
    @Column(name = "status", nullable = false,
            columnDefinition = "boolean not null default true")
    private Boolean status = true;

    @OneToMany(
            mappedBy = "rutina",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<RutinaEjercicio> rutinaEjercicios = new ArrayList<>();

    /** Constructor requerido por JPA. */
    public Rutina() {
    }

    /**
     * Crea una rutina activa.
     *
     * @param usuario propietario de la rutina
     * @param nombre nombre de la rutina
     * @param descripcion descripción opcional
     * @param diaSemana día asignado
     */
    public Rutina(Usuario usuario, String nombre, String descripcion, DiaSemana diaSemana) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.diaSemana = diaSemana;
        this.status = true; // Toda rutina nace activa; el borrado logico la desactiva.
    }

    /**
     * Agrega un ejercicio y mantiene ambos lados de la relación.
     *
     * @param rutinaEjercicio ejercicio configurado para la rutina
     */
    public void agregarRutinaEjercicio(RutinaEjercicio rutinaEjercicio) {
        rutinaEjercicio.setRutina(this);
        rutinaEjercicios.add(rutinaEjercicio);
    }

    /**
     * Reemplaza todos los ejercicios configurados de la rutina.
     *
     * @param nuevosEjercicios nueva configuración de ejercicios
     */
    public void reemplazarRutinaEjercicios(
            List<RutinaEjercicio> nuevosEjercicios) {
        rutinaEjercicios.clear();
        nuevosEjercicios.forEach(this::agregarRutinaEjercicio);
    }
}
