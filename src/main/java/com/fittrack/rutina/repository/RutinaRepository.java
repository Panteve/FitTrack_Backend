package com.fittrack.rutina.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.rutina.entity.Rutina;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /**
     * Busca las rutinas de un usuario y carga sus ejercicios asociados.
     *
     * @param usuarioId identificador del usuario
     * @return rutinas del usuario con sus ejercicios
     */
    @EntityGraph(attributePaths = {
            "rutinaEjercicios",
            "rutinaEjercicios.ejercicio"
    })
    List<Rutina> findDistinctByUsuario_Id(Long usuarioId);

    /**
     * Busca una rutina perteneciente a un usuario y carga sus ejercicios asociados.
     *
     * @param rutinaId identificador de la rutina
     * @param usuarioId identificador del usuario
     * @return rutina encontrada o vacío si no existe o pertenece a otro usuario
     */
    @EntityGraph(attributePaths = {
            "rutinaEjercicios",
            "rutinaEjercicios.ejercicio"
    })
    Optional<Rutina> findDistinctByIdAndUsuario_Id(Long rutinaId, Long usuarioId);

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
}
