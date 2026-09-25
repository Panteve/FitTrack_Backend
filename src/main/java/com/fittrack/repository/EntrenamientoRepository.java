package com.fittrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.entity.Entrenamiento;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {

}
