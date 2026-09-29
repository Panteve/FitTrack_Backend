package com.fittrack.rutina.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.rutina.entity.Rutina;
import com.fittrack.rutina.enums.DiaSemana;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /**
     * Busca las rutinas activas de un usuario y carga sus ejercicios asociados.
     *
     * @param usuarioId identificador del usuario
     * @return rutinas activas del usuario con sus ejercicios
     */
    @EntityGraph(attributePaths = {
            "rutinaEjercicios",
            "rutinaEjercicios.ejercicio"
    })
    List<Rutina> findDistinctByUsuario_IdAndStatusTrue(Long usuarioId);

    /**
     * Busca una rutina activa perteneciente a un usuario y carga sus ejercicios.
     *
     * @param rutinaId identificador de la rutina
     * @param usuarioId identificador del usuario
     * @return rutina activa encontrada o vacio si no existe, esta inactiva
     *         o pertenece a otro usuario
     */
    @EntityGraph(attributePaths = {
            "rutinaEjercicios",
            "rutinaEjercicios.ejercicio"
    })
    Optional<Rutina> findDistinctByIdAndUsuario_IdAndStatusTrue(Long rutinaId, Long usuarioId);

    /**
     * Busca una rutina de un usuario sin filtrar por estado. Se reserva para el
     * borrado logico, que debe poder localizar tambien una rutina ya desactivada
     * para responder de forma idempotente.
     *
     * @param rutinaId identificador de la rutina
     * @param usuarioId identificador del usuario
     * @return rutina encontrada o vacio si no existe o pertenece a otro usuario
     */
    Optional<Rutina> findByIdAndUsuario_Id(Long rutinaId, Long usuarioId);

    /**
     * Busca una rutina activa de un usuario sin cargar sus ejercicios asociados.
     * Se usa al reemplazar la configuracion completa, donde la coleccion anterior
     * se descarta y no conviene traerla.
     *
     * @param rutinaId identificador de la rutina
     * @param usuarioId identificador del usuario
     * @return rutina activa encontrada o vacio si no existe, esta inactiva
     *         o pertenece a otro usuario
     */
    Optional<Rutina> findByIdAndUsuario_IdAndStatusTrue(Long rutinaId, Long usuarioId);

    /**
     * Busca la primera rutina activa asignada al día indicado.
     *
     * @param usuarioId identificador del usuario
     * @param diaSemana día que debe tener la rutina
     * @return rutina encontrada con sus ejercicios
     */
    @EntityGraph(attributePaths = "rutinaEjercicios")
    Optional<Rutina> findFirstByUsuario_IdAndStatusTrueAndDiaSemanaOrderByIdAsc(
            Long usuarioId,
            DiaSemana diaSemana);

    /**
     * Busca la primera rutina activa disponible para usarla como alternativa.
     *
     * @param usuarioId identificador del usuario
     * @return rutina encontrada con sus ejercicios
     */
    @EntityGraph(attributePaths = "rutinaEjercicios")
    Optional<Rutina> findFirstByUsuario_IdAndStatusTrueOrderByIdAsc(Long usuarioId);
}
