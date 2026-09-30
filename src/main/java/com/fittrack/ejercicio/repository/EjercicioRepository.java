package com.fittrack.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.ejercicio.entity.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {//Repositorio de ejercicios que extiende JpaRepository para proporcionar operaciones 
// CRUD y consultas personalizadas para la entidad Ejercicio.


    List<Ejercicio> findAllByUsuario_IdAndStatusTrue(Long usuarioId);

    List<Ejercicio> findAllByUsuarioIsNullAndStatusTrueOrderByNombreAsc();

    List<Ejercicio> findAllByUsuario_IdAndStatusTrueOrderByNombreAsc(Long usuarioId);

    Optional<Ejercicio> findByIdAndUsuario_Id(Long ejercicioId, Long usuarioId);
}
