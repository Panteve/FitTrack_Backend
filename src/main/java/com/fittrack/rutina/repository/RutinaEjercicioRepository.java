package com.fittrack.rutina.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.rutina.entity.RutinaEjercicio;

public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {

    /**
     * Comprueba si un ejercicio está asociado a alguna rutina.
     *
     * @param ejercicioId identificador del ejercicio
     * @return {@code true} cuando existe al menos una asociación
     */
    boolean existsByEjercicio_Id(Long ejercicioId);
}
