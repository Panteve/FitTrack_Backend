package com.fittrack.home.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fittrack.entrenamiento.entity.Entrenamiento;
import com.fittrack.entrenamiento.repository.EntrenamientoRepository;
import com.fittrack.home.dto.HomeDto;
import com.fittrack.home.dto.ProximaRutinaDto;
import com.fittrack.home.dto.UltimoEntrenamientoDto;
import com.fittrack.rutina.entity.Rutina;
import com.fittrack.rutina.enums.DiaSemana;
import com.fittrack.rutina.repository.RutinaRepository;

/** Construye la información mostrada en la pantalla principal. */
@Service
public class HomeService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

    private final RutinaRepository rutinaRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    /**
     * Crea el servicio con los repositorios requeridos.
     *
     * @param rutinaRepository repositorio de rutinas
     * @param entrenamientoRepository repositorio de entrenamientos
     */
    public HomeService(
            RutinaRepository rutinaRepository,
            EntrenamientoRepository entrenamientoRepository) {
        this.rutinaRepository = rutinaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
    }

    /**
     * Obtiene la rutina recomendada y los últimos veinte entrenamientos.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return información de la pantalla principal
     */
    @Transactional(readOnly = true)
    public HomeDto obtenerHome(Long usuarioId) {
        ProximaRutinaDto proximaRutina = buscarRutinaRecomendada(usuarioId)
                .map(this::toProximaRutinaDto)
                .orElse(null);

        List<UltimoEntrenamientoDto> ultimosEntrenamientos = entrenamientoRepository
                .findTop20ByUsuario_IdAndStatusTrueOrderByFechaDescIdDesc(usuarioId)
                .stream()
                .map(this::toUltimoEntrenamientoDto)
                .toList();

        return new HomeDto(proximaRutina, ultimosEntrenamientos);
    }

    private Optional<Rutina> buscarRutinaRecomendada(Long usuarioId) {
        DiaSemana diaActual = obtenerDiaActual();
        Optional<Rutina> rutinaDelDia = rutinaRepository
                .findFirstByUsuario_IdAndStatusTrueAndDiaSemanaOrderByIdAsc(
                        usuarioId,
                        diaActual);

        if (rutinaDelDia.isPresent()) {
            return rutinaDelDia;
        }

        return rutinaRepository
                .findFirstByUsuario_IdAndStatusTrueOrderByIdAsc(usuarioId);
    }

    private DiaSemana obtenerDiaActual() {
        DayOfWeek diaActual = LocalDate.now(ZONA_HORARIA).getDayOfWeek();

        return switch (diaActual) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    private ProximaRutinaDto toProximaRutinaDto(Rutina rutina) {
        return new ProximaRutinaDto(
                rutina.getId(),
                rutina.getRutinaEjercicios().size(),
                rutina.getNombre());
    }

    private UltimoEntrenamientoDto toUltimoEntrenamientoDto(
            Entrenamiento entrenamiento) {
        return new UltimoEntrenamientoDto(
                entrenamiento.getId(),
                entrenamiento.getRutina().getNombre(),
                entrenamiento.getFecha(),
                entrenamiento.getDuracionMinutos());
    }
}
