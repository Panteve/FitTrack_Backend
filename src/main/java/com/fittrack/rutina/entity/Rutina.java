package com.fittrack.rutina.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fittrack.rutina.enums.DiaSemana;
import com.fittrack.usuario.entity.Usuario;

import lombok.Getter;
import lombok.Setter;

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

    // Constructor
    public Rutina() {
    }

    public Rutina(Usuario usuario, String nombre, String descripcion, DiaSemana diaSemana) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.diaSemana = diaSemana;
    }
}
