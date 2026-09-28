package com.fittrack.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.ejercicio.entity.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {


    List<Ejercicio> findAllByUsuario_IdAndStatusTrue(Long usuarioId);
    Optional<Ejercicio> findByIdAndUsuario_Id(Long ejercicioId, Long usuarioId);
}
