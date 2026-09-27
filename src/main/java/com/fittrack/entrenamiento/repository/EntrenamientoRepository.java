package com.fittrack.entrenamiento.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.entrenamiento.entity.Entrenamiento;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {

}
