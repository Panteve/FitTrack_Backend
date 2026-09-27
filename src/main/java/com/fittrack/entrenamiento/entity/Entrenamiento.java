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

@Entity
@Table(name = "entrenamiento")
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

    // Getters and Setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rutina getRutina() {
        return this.rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getDuracionMinutos() {
        return this.duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getNotas() {
        return this.notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getUrlFoto() {
        return this.urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
