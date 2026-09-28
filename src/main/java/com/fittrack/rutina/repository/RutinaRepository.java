package com.fittrack.rutina.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.rutina.entity.Rutina;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    @EntityGraph(attributePaths = {
            "rutinaEjercicios",
            "rutinaEjercicios.ejercicio"
    })
    List<Rutina> findDistinctByUsuario_Id(Long usuarioId);
}
