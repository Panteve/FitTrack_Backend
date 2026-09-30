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

//Construye la información mostrada en la pantalla principal.
@Service
public class HomeService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

    private final RutinaRepository rutinaRepository;
    private final EntrenamientoRepository entrenamientoRepository;


    public HomeService(// Servicio que construye la información mostrada en la pantalla principal
            RutinaRepository rutinaRepository,
            EntrenamientoRepository entrenamientoRepository) {
        this.rutinaRepository = rutinaRepository;
        this.entrenamientoRepository = entrenamientoRepository;
    }

    @Transactional(readOnly = true)
    public HomeDto obtenerHome(Long usuarioId) {// Obtiene la información principal del usuario autenticado, incluyendo la próxima rutina y los últimos entrenamientos
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

    private Optional<Rutina> buscarRutinaRecomendada(Long usuarioId) {// Busca la próxima rutina sugerida para el usuario, priorizando la rutina del día actual si existe
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

    private DiaSemana obtenerDiaActual() {// Obtiene el día de la semana actual en la zona horaria especificada
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

    private ProximaRutinaDto toProximaRutinaDto(Rutina rutina) {// Convierte una entidad de rutina en un DTO de próxima rutina, 
    // incluyendo su ID, nombre y número de ejercicios
        return new ProximaRutinaDto(
                rutina.getId(),
                rutina.getRutinaEjercicios().size(),
                rutina.getNombre());
    }

    private UltimoEntrenamientoDto toUltimoEntrenamientoDto(// Convierte una entidad de entrenamiento en un DTO de último entrenamiento, 
    // incluyendo su ID, nombre, fecha y duración en minutos
            Entrenamiento entrenamiento) {
        return new UltimoEntrenamientoDto(
                entrenamiento.getId(),
                entrenamiento.getRutina().getNombre(),
                entrenamiento.getFecha(),
                entrenamiento.getDuracionMinutos());
    }
}
