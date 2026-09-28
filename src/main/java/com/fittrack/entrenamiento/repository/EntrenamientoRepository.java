package com.fittrack.entrenamiento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fittrack.entrenamiento.dto.EntrenamientoDto;
import com.fittrack.entrenamiento.entity.Entrenamiento;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {

    /**
     * Lista los entrenamientos activos de un usuario sin cargar las series.
     *
     * @param usuarioId identificador del usuario
     * @return resúmenes ordenados del más reciente al más antiguo
     */
    @Query("""
            select new com.fittrack.entrenamiento.dto.EntrenamientoDto(
                    e.id,
                    r.id,
                    r.nombre,
                    e.fecha,
                    e.duracionMinutos,
                    e.notas,
                    case when e.urlFoto is not null then true else false end)
            from Entrenamiento e
            join e.rutina r
            where e.usuario.id = :usuarioId
              and e.status = true
            order by e.fecha desc, e.id desc
            """)
    List<EntrenamientoDto> findResumenByUsuarioId(
            @Param("usuarioId") Long usuarioId);

    /**
     * Obtiene un entrenamiento activo con todas las relaciones necesarias para
     * construir su detalle.
     *
     * @param entrenamientoId identificador del entrenamiento
     * @param usuarioId identificador del propietario
     * @return entrenamiento encontrado
     */
    @EntityGraph(attributePaths = {
            "rutina",
            "registrosSeries",
            "registrosSeries.ejercicio"
    })
    Optional<Entrenamiento> findDistinctByIdAndUsuario_IdAndStatusTrue(
            Long entrenamientoId,
            Long usuarioId);

    /** Busca un entrenamiento activo sin cargar sus series. */
    Optional<Entrenamiento> findByIdAndUsuario_IdAndStatusTrue(
            Long entrenamientoId,
            Long usuarioId);

    /** Busca un entrenamiento del usuario sin filtrar su estado. */
    Optional<Entrenamiento> findByIdAndUsuario_Id(
            Long entrenamientoId,
            Long usuarioId);
}
