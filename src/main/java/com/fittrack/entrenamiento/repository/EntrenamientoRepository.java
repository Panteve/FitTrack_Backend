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


    @EntityGraph(attributePaths = "rutina")// Carga la rutina asociada al entrenamiento
    List<Entrenamiento> findTop20ByUsuario_IdAndStatusTrueOrderByFechaDescIdDesc(
            Long usuarioId);


    @EntityGraph(attributePaths = {// Carga la rutina y los registros de series asociados al entrenamiento
            "rutina",
            "registrosSeries",
            "registrosSeries.ejercicio"
    })
    Optional<Entrenamiento> findDistinctByIdAndUsuario_IdAndStatusTrue(// Busca un entrenamiento activo con sus series
            Long entrenamientoId,
            Long usuarioId);

    //Busca un entrenamiento activo sin cargar sus series.
    Optional<Entrenamiento> findByIdAndUsuario_IdAndStatusTrue(
            Long entrenamientoId,
            Long usuarioId);

    // Busca un entrenamiento del usuario sin filtrar su estado.
    Optional<Entrenamiento> findByIdAndUsuario_Id(
            Long entrenamientoId,
            Long usuarioId);
}
