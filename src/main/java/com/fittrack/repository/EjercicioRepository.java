package com.fittrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.entity.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

}
