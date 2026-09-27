package com.fittrack.entrenamiento.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "url_foto", nullable = false)
    private String urlFoto;

    // Constructor
    public Entrenamiento() {
    }

    public Entrenamiento(Usuario usuario, Rutina rutina, LocalDate fecha, Integer duracionMinutos, String notas,
            String urlFoto) {
        this.usuario = usuario;
        this.rutina = rutina;
        this.fecha = fecha;
        this.duracionMinutos = duracionMinutos;
        this.notas = notas;
        this.urlFoto = urlFoto;
    }
}
