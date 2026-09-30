package com.fittrack.rutina.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.rutina.entity.RutinaEjercicio;

public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {


    boolean existsByEjercicio_Id(Long ejercicioId);// Verifica si existe al menos un RutinaEjercicio asociado a un ejercicio específico.

}
