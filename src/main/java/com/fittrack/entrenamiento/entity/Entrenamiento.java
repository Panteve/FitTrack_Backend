package com.fittrack.entrenamiento.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fittrack.rutina.entity.Rutina;
import com.fittrack.usuario.entity.Usuario;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "entrenamiento")
@Getter
@Setter
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "notas")
    private String notas;

    @Column(name = "url_foto", nullable = true)
    private String urlFoto;

    @Column(name = "status", nullable = false,
            columnDefinition = "boolean not null default true")
    private Boolean status = true;

    @OneToMany(
            mappedBy = "entrenamiento",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RegistroSerie> registrosSeries = new ArrayList<>();

    /** Constructor requerido por JPA. */
    public Entrenamiento() {
    }

    /**
     * Crea un entrenamiento activo y sin fotografía.
     *
     * @param usuario propietario del entrenamiento
     * @param rutina rutina realizada
     * @param fecha fecha de realización
     * @param duracionMinutos duración total en minutos
     * @param notas observaciones opcionales
     */
    public Entrenamiento(
            Usuario usuario,
            Rutina rutina,
            LocalDate fecha,
            Integer duracionMinutos,
            String notas) {
        this.usuario = usuario;
        this.rutina = rutina;
        this.fecha = fecha;
        this.duracionMinutos = duracionMinutos;
        this.notas = notas;
        this.status = true;
    }

    /**
     * Agrega una serie realizada y mantiene ambos lados de la relación.
     *
     * @param registroSerie serie realizada
     */
    public void agregarRegistroSerie(RegistroSerie registroSerie) {
        registroSerie.setEntrenamiento(this);
        registrosSeries.add(registroSerie);
    }

    /**
     * Reemplaza todas las series realizadas del entrenamiento.
     *
     * @param nuevosRegistros nuevas series realizadas
     */
    public void reemplazarRegistrosSeries(
            List<RegistroSerie> nuevosRegistros) {
        registrosSeries.clear();
        nuevosRegistros.forEach(this::agregarRegistroSerie);
    }
}
