package com.fittrack.ejercicio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fittrack.usuario.entity.Usuario;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ejercicio")
@Getter
@Setter
public class Ejercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    /* TODO: Convertir grupo_muscular en una entidad para estandarizar */
    @Column(name = "grupo_muscular", nullable = false)
    private String grupoMuscular;
    
    //Constructor

    public Ejercicio() {
    }

    public Ejercicio(Usuario usuario, String nombre, String grupoMuscular) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
    }

}
