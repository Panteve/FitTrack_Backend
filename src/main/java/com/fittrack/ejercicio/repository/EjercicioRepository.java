package com.fittrack.ejercicio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.ejercicio.entity.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

}
