package com.fittrack.rutina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fittrack.rutina.entity.RutinaEjercicio;

public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicio, Long> {

    /**
     * Comprueba si un ejercicio está asociado a alguna rutina.
     *
     * @param ejercicioId identificador del ejercicio
     * @return {@code true} cuando existe al menos una asociación
     */
    boolean existsByEjercicio_Id(Long ejercicioId);

    /**
     * Elimina todas las asociaciones de una rutina de una sola vez. Se usa al
     * reemplazar por completo la configuracion de ejercicios, porque el detalle
     * anterior se descarta y no debe quedar en la base de datos.
     *
     * @param rutinaId identificador de la rutina
     */
    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM RutinaEjercicio re WHERE re.rutina.id = :rutinaId")
    void deleteAllByRutinaId(@Param("rutinaId") Long rutinaId);
}
