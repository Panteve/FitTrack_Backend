package com.fittrack.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.ejercicio.entity.Ejercicio;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    /**
     * Busca los ejercicios creados por un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return ejercicios pertenecientes al usuario
     */
    List<Ejercicio> findAllByUsuario_Id(Long usuarioId);

    /**
     * Busca un ejercicio que pertenezca a un usuario determinado.
     *
     * @param ejercicioId identificador del ejercicio
     * @param usuarioId identificador del usuario
     * @return ejercicio encontrado o vacío si no existe o pertenece a otro usuario
     */
    Optional<Ejercicio> findByIdAndUsuario_Id(Long ejercicioId, Long usuarioId);
}
